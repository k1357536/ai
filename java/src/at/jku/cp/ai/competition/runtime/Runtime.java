package at.jku.cp.ai.competition.runtime;

import static at.jku.cp.ai.competition.runtime.Errorlevels.IMPROPER_MOVE_LIMIT;
import static at.jku.cp.ai.competition.runtime.Errorlevels.IMPROPER_SEED;
import static at.jku.cp.ai.competition.runtime.Errorlevels.IMPROPER_TIME_LIMIT;
import static at.jku.cp.ai.competition.runtime.Errorlevels.LEVELFILE_NOTFOUND;
import static at.jku.cp.ai.competition.runtime.Errorlevels.OK;
import static at.jku.cp.ai.competition.runtime.Errorlevels.PLAYER_INSTANTIATION_FAILED;
import static at.jku.cp.ai.competition.runtime.Errorlevels.UNFORESEEN_CONSEQUENCES;
import static at.jku.cp.ai.competition.runtime.Errorlevels.WRONG_NARGS;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import at.jku.cp.ai.competition.players.Player;
import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.endconditions.PointCollecting;
import at.jku.cp.ai.rau.objects.Move;

public class Runtime
{

	public static void main(String[] args)
	{
		if (args.length != 7)
		{
			System.out.println("usage: java at.jku.cp.rau.runtime.Runtime <p0> <p1> <level> <timelimit [s]> <movelimit> <seed> <[verbose|silent]>");
			System.exit(WRONG_NARGS);
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

		
		long seed = System.currentTimeMillis();
		try
		{
			seed = Long.parseLong(args[5]);
		}
		catch (NumberFormatException e)
		{
			System.out.println("seed number is not a 'long' integer.");
			System.exit(IMPROPER_SEED);
		}

		boolean verbose = false;
		if ("verbose".equals(args[6]))
		{
			verbose = true;
		}

		// so this is either seeded with the time, or from the command line, and
		// the first two longs are used as seeds to the RNG's for the players
		Random masterRandom = new Random(seed);

		List<Player> players = new ArrayList<>();
		Map<Player, PlayerInfo> playerInfoMapping = new HashMap<>();

		for (int i = 0; i < 2; i++)
		{
			try
			{
				String classFilename = args[i];
				Player player = (Player) Class.forName(classFilename).newInstance();
				players.add(player);

				PlayerInfo info = new PlayerInfo(
						timeLimit,
						moveLimit / 2,
						i,
						(i + 1) % 2,
						new Random(masterRandom.nextLong()));
				playerInfoMapping.put(player, info);

			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
			{
				e.printStackTrace();
				System.exit(PLAYER_INSTANTIATION_FAILED);
			}
		}

		String levelName = args[2];
		if (!new File(levelName).exists())
		{
			System.out.println("level file does not exist");
			System.exit(LEVELFILE_NOTFOUND);
		}

		Board masterBoard = Board.fromLevelFile(levelName);
		PointCollecting pointCollecting = new PointCollecting(moveLimit);
		masterBoard.setEndCondition(pointCollecting);

		final PrintStream stdout = System.out;
		if (!verbose)
		{
			System.setOut(NullPrinter.out);
		}

		try
		{
			while (masterBoard.isRunning() && masterBoard.getTick() < moveLimit)
			{
				System.out.println(masterBoard);

				System.out.println(String.format("%d %d %d %d %d %d",
						masterBoard.getTick(),
						masterBoard.getEndCondition().getWinner(),
						pointCollecting.getScore(0),
						pointCollecting.getScore(1),
						playerInfoMapping.get(players.get(0)).remainingTime,
						playerInfoMapping.get(players.get(1)).remainingTime));

				Player player = players.get(masterBoard.getCurrentUnicorn().id);
				PlayerInfo info = playerInfoMapping.get(player);

				// make a copy of the master board, so no state is shared
				// between players
				IBoard copyBoard = masterBoard.deepCopy();

				// copy all the additional state that could be changed by
				// the getNextMove method
				// technically, one can still mess up the Random instance,
				// but that is the player's problem ...
				PlayerInfo copyInfo = new PlayerInfo(info);

				// let the player determine its next move, measure time
				// taken
				long start = System.currentTimeMillis();
				Move move = player.getNextMove(copyInfo, copyBoard);
				long end = System.currentTimeMillis();
				long timeTaken = end - start;
				System.out.println("time taken : " + timeTaken + " [ms]");
				// if for some reason the player method returns a 'null',
				// replace it with the 'do-nothing' move (execute a 'STAY' move)
				if (move == null)
				{
					move = Move.STAY;
				}

				info.remainingTime = info.remainingTime - timeTaken;
				info.remainingMoves--;

				masterBoard.executeMove(move);
			}

			System.out.println("--- game finished ---");
			System.out.println(masterBoard);
			System.out.println("--- result ---");

			// always print this ...
			System.setOut(stdout);
			System.out.println(String.format("%d %d %d %d %d %d %s %s %s",
					masterBoard.getTick(),
					masterBoard.getEndCondition().getWinner(),
					pointCollecting.getScore(0),
					pointCollecting.getScore(1),
					playerInfoMapping.get(players.get(0)).remainingTime,
					playerInfoMapping.get(players.get(1)).remainingTime,
					args[0], args[1], args[2]));

		} catch (Throwable t)
		{
			System.out.println("unforseen consequences...");
			if (masterBoard != null)
			{
				System.out.println(masterBoard);
				System.out.println(Board.toStateRepresentation(masterBoard));
			}
			t.printStackTrace();
			System.exit(UNFORESEEN_CONSEQUENCES);
		}

		// all in order ...
		System.exit(OK);
	}

}
