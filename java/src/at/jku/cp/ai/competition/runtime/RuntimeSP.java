package at.jku.cp.ai.competition.runtime;

import static at.jku.cp.ai.competition.runtime.Errorlevels.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.competition.runtime.server.JVMPlayer;
import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.BoardWithHistory;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.endconditions.PointCollecting;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.Unicorn;

public class RuntimeSP
{

	public static void main(String[] args) throws FileNotFoundException
	{
		if (args.length != 7)
		{
			System.err.println("usage: java at.jku.cp.rau.runtime.Runtime <p0> <p1> <level> <timelimit [s]> <movelimit> <seed> <logdir>");
			System.exit(WRONG_NARGS);
		}

		long seed = 0L;
		try
		{
			seed = Long.parseLong(args[5]);
		} catch (NumberFormatException e)
		{
			System.err.println("seed number is not a 'long' integer.");
			System.exit(IMPROPER_SEED);
		}

		String logdir = args[6];
		if (Files.exists(Paths.get(logdir)))
		{
			System.setOut(new PrintStream(new File(logdir + File.separator + "log")));
			System.setErr(new PrintStream(new File(logdir + File.separator + "err")));
		} else
		{
			System.err.println("logdir '" + logdir + "' does not exist");
			System.exit(INVALID_LOGDIR);
		}

		long timeLimit = 0L;
		try
		{
			// timeLimit is in [ms], cmdline is in [s]
			timeLimit = 1000L * Integer.parseInt(args[3]);
		} catch (Exception e)
		{
			System.err.println("time limit is not an 'int'.");
			System.exit(IMPROPER_TIME_LIMIT);
		}

		int moveLimit = -1;
		try
		{
			moveLimit = Integer.parseInt(args[4]);
		} catch (NumberFormatException e)
		{
			System.err.println("move limit is not an 'int'.");
			System.exit(IMPROPER_MOVE_LIMIT);
		}

		try
		{
			// the first two longs are used as seeds to the RNG's for the
			// players
			Random masterRandom = new Random(seed);

			List<JVMPlayer> players = new ArrayList<>();
			Map<JVMPlayer, PlayerInfo> playerInfoMapping = new HashMap<>();

			for (int i = 0; i < 2; i++)
			{
				try
				{
					String classFilename = args[i];
					JVMPlayer jvmplayer = new JVMPlayer(9998 + i, classFilename);
					players.add(jvmplayer);

					PlayerInfo info = new PlayerInfo(timeLimit, moveLimit / 2, i, (i + 1) % 2, new Random(masterRandom.nextLong()));
					playerInfoMapping.put(jvmplayer, info);
				} catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
					System.exit(PLAYER_INSTANTIATION_FAILED);
				}
			}

			String levelName = args[2];
			if (!new File(levelName).exists())
			{
				System.err.println("level file does not exist");
				System.exit(LEVELFILE_NOTFOUND);
			}

			BoardWithHistory historyBoard = new BoardWithHistory(Board.fromLevelFile(levelName));
			PointCollecting pointCollecting = new PointCollecting();
			historyBoard.setEndCondition(pointCollecting);

			ExecutorService executor = Executors.newSingleThreadExecutor();

			boolean inTime = true;
			boolean inMemory = true;
			while (historyBoard.isRunning()
					&& historyBoard.getTick() < moveLimit
					&& inTime
					&& inMemory)
			{
				final JVMPlayer player = players.get(historyBoard.getCurrentUnicorn().id);
				PlayerInfo info = playerInfoMapping.get(player);

				// make a copy of the master board, so no state is shared
				// between players
				final IBoard copyBoard = historyBoard.getBoard().deepCopy();

				// copy all the additional state that could be changed by
				// the getNextMove method
				// technically, one can still mess up the Random instance,
				// but that is the player's problem ...
				final PlayerInfo copyInfo = new PlayerInfo(info);

				Future<Move> future = executor.submit(new Callable<Move>()
				{
					@Override
					public Move call() throws Exception
					{
						return player.getNextMove(copyInfo, copyBoard);
					}
				});

				try
				{
					// set the timeout + grace-value for communication
					Move move = future.get(copyInfo.remainingTime + 100, TimeUnit.MILLISECONDS);

					info.remainingTime = info.remainingTime - player.getTimeTaken();
					info.remainingMoves--;

					// if the future didn't throw b/c of the grace-value, throw
					// if time ran out
					if (info.remainingTime <= 0)
					{
						throw new TimeoutException();
					}

					// if for some reason the player method returns a 'null',
					// replace it with the 'do-nothing' move (execute a 'STAY'
					// move)
					if (move == null)
					{
						move = Move.STAY;
					}

					historyBoard.executeMove(move);
				} catch (TimeoutException e)
				{
					Unicorn loser = historyBoard.getCurrentUnicorn();

					// take the losing player off the board
					historyBoard.getUnicorns().remove(loser);

					// set the winner
					pointCollecting.setWinnerOnTimeout(loser.id == 0 ? 1 : 0);
					info.remainingTime = 0;
					inTime = false;
				} catch (Exception e)
				{
					// all other cases, we'll treat as an OOM Exception
					Unicorn loser = historyBoard.getCurrentUnicorn();

					// take the losing player off the board
					historyBoard.getUnicorns().remove(loser);

					// set the winner
					pointCollecting.setWinnerOnMemout(loser.id == 0 ? 1 : 0);
					inMemory = false;
				}
			}

			players.get(0).destroy();
			players.get(1).destroy();

			// <tick> <winner> <score-p0> <score-p1> <time-p0> <time-p1> <p0>
			// <p1> <level> <timelimit> <movelimit> <seed>
			PrintStream resultfile = new PrintStream(new File(logdir + File.separator + "result.yaml"));
			resultfile.println(
					String.format("tick:%d\n"
							+ "outcome:%s\n"
							+ "winner:%d\n"
							+ "score_p0:%d\n"
							+ "score_p1:%d\n"
							+ "time_p0:%d\n"
							+ "time_p1:%d\n"
							+ "p0:%s\n"
							+ "p1:%s\n"
							+ "level:%s\n"
							+ "timelimit:%s\n"
							+ "movelimit:%s\n"
							+ "seed:%s\n",
							historyBoard.getTick(),
							historyBoard.getEndCondition().getOutcome(),
							historyBoard.getEndCondition().getWinner(),
							pointCollecting.getScore(0),
							pointCollecting.getScore(1),
							playerInfoMapping.get(players.get(0)).remainingTime,
							playerInfoMapping.get(players.get(1)).remainingTime,
							args[0],
							args[1],
							args[2],
							args[3],
							args[4],
							args[5]));

			resultfile.flush();
			resultfile.close();
		} catch (Throwable t)
		{
			System.err.println("unforseen consequences...");
			t.printStackTrace();
			System.exit(UNFORESEEN_CONSEQUENCES);
		}

		// all in order ...
		System.exit(OK);
	}
}
