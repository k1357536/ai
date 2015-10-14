package at.jku.cp.ai.tests.runtime;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import at.jku.cp.ai.competition.players.Player;
import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.competition.players.random.RandomWalkPlayer;
import at.jku.cp.ai.competition.runtime.server.JVMPlayer;
import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.utils.Constants;

public class TestSpawningMultipleJVMs
{
	@Test
	public void testOneJVMPlayer() throws Exception
	{
		long seed = 23L;
		IBoard board = Board.fromLevelFile(Constants.ASSET_PATH+"/default.lvl");
		
		PlayerInfo remoteInfo = new PlayerInfo(0L, 0, 0, 1, new Random(seed));
		PlayerInfo localInfo = new PlayerInfo(0L, 0, 0, 1, new Random(seed));

		JVMPlayer remoteP0 = new JVMPlayer(9999,
				RandomWalkPlayer.class.getCanonicalName());
		Player localP0 = new RandomWalkPlayer();

		for (int i = 0; i < 10; i++)
		{
			Move remoteMove = remoteP0.getNextMove(remoteInfo, board);
			Move localMove = localP0.getNextMove(localInfo, board);

			assertEquals(localMove, remoteMove);

			board.executeMove(remoteMove);
		}
		remoteP0.close();
	}

	@Test
	public void testTwoJVMPlayer() throws Exception
	{
		long seedP0 = 23L;
		long seedP1 = 42L;
		IBoard board = Board.fromLevelFile(Constants.ASSET_PATH+"/default.lvl");
		
		PlayerInfo remoteInfoP0 = new PlayerInfo(0L, 0, 0, 1,new Random(seedP0));
		PlayerInfo remoteInfoP1 = new PlayerInfo(0L, 0, 1, 0, new Random(seedP1));
		
		PlayerInfo localInfoP0 = new PlayerInfo(0, 0, 0, 1, new Random(seedP0));
		PlayerInfo localInfoP1 = new PlayerInfo(0, 0, 1, 0, new Random(seedP1));
		

		JVMPlayer remoteP0 = new JVMPlayer(9998, RandomWalkPlayer.class.getCanonicalName());
		JVMPlayer remoteP1 = new JVMPlayer(9999, RandomWalkPlayer.class.getCanonicalName());

		Player localP0 = new RandomWalkPlayer();
		Player localP1 = new RandomWalkPlayer();
		
		Move localMoveP0;
		Move localMoveP1;
		Move remoteMoveP0;
		Move remoteMoveP1;
		for (int i = 0; i < 100; i++)
		{
			localMoveP0 = localP0.getNextMove(localInfoP0, board);
			remoteMoveP0 = remoteP0.getNextMove(remoteInfoP0, board);
			assertEquals(localMoveP0, remoteMoveP0);
			board.executeMove(remoteMoveP0);
			
			localMoveP1 = localP1.getNextMove(localInfoP1, board);
			remoteMoveP1 = remoteP1.getNextMove(remoteInfoP1, board);
			assertEquals(localMoveP1, remoteMoveP1);
			board.executeMove(remoteMoveP1);
		}
		remoteP0.close();
		remoteP1.close();
	}
}
