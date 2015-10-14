package at.jku.cp.ai.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.BoardWithHistory;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.endconditions.PointCollecting;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Move;

public class TestPointCollecting
{
	@Test
	public void doesNotCrashIfNoFountains()
	{
		IBoard board = Board.fromLevelRepresentation(
				Arrays.asList(
						"####",
						"#p.#",
						"####"));

		PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		board.executeMove(Move.RIGHT);
		board.executeMove(Move.LEFT);

		assertEquals(0, fc.getScore(0));
	}

	@Test
	public void oneUnicornOneFlag()
	{
		IBoard board = Board.fromLevelRepresentation(
				Arrays.asList(
						"####",
						"#pf#",
						"####"));

		PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		assertEquals(-1, board.getFountains().get(0).lastVisitedBy);

		board.executeMove(Move.RIGHT);

		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		assertEquals(1, fc.getScore(0));
	}

	@Test
	public void twoUnicornsOneFlag()
	{
		IBoard board = Board.fromLevelRepresentation(
				Arrays.asList(
						"#####",
						"#pfp#",
						"#####"));
		PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		assertEquals(-1, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.RIGHT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1
		board.executeMove(Move.STAY);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.LEFT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1
		board.executeMove(Move.LEFT);
		assertEquals(1, board.getFountains().get(0).lastVisitedBy);

		assertEquals(0, board.getEndCondition().getWinner());

		assertEquals(3, fc.getScore(0));
		assertEquals(1, fc.getScore(1));
	}

	@Test
	public void twoUnicornsOneFlagDraw()
	{
		IBoard board = Board.fromLevelRepresentation(
				Arrays.asList(
						"#####",
						"#pfp#",
						"#####"));
		PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		assertEquals(-1, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.RIGHT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1
		board.executeMove(Move.STAY);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.LEFT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1
		board.executeMove(Move.LEFT);
		assertEquals(1, board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.LEFT);

		// player 1
		board.executeMove(Move.STAY);

		assertEquals(-1, board.getEndCondition().getWinner());

		assertEquals(3, fc.getScore(0));
		assertEquals(3, fc.getScore(1));
	}

	@Test
	public void twoUnicornsOnOneFlagSimultaenously()
	{
		BoardWithHistory board = new BoardWithHistory(
				Board.fromLevelRepresentation(
						Arrays.asList(
								"#####",
								"#pfp#",
								"#####")));

		PointCollecting fc = new PointCollecting();
		board.setEndCondition(fc);

		assertEquals(Fountain.LAST_VISITED_BY_DEFAULT,
				board.getFountains().get(0).lastVisitedBy);

		// player 0
		board.executeMove(Move.RIGHT);
		assertEquals(0, board.getFountains().get(0).lastVisitedBy);

		// player 1 -- as soon as this player steps on the fountain,
		// the fountain is neutral again
		board.executeMove(Move.LEFT);
		assertEquals(Fountain.LAST_VISITED_BY_DEFAULT,
				board.getFountains().get(0).lastVisitedBy);

		board.executeMove(Move.STAY);
		board.executeMove(Move.STAY);
		board.executeMove(Move.STAY);
		board.executeMove(Move.STAY);

		assertEquals(0, board.getEndCondition().getWinner());

		assertEquals(1, fc.getScore(0));
		assertEquals(0, fc.getScore(1));
	}

	@Test
	public void moveLimit()
	{
		BoardWithHistory board = new BoardWithHistory(
				Board.fromLevelRepresentation(
						Arrays.asList(
								"#######",
								"#p.f.p#",
								"#######")));

		PointCollecting fc = new PointCollecting(2);
		board.setEndCondition(fc);

		// tick 0
		assertTrue(board.executeMove(Move.RIGHT));
		
		// tick 1
		assertTrue(board.executeMove(Move.STAY));

		// tick 2
		assertTrue(board.executeMove(Move.RIGHT));

		assertFalse(board.isRunning());
		assertEquals(2, board.getTick());
		
		// player 0 won
		assertEquals(0, board.getEndCondition().getWinner());

		// with a score of 1
		assertEquals(1, fc.getScore(0));
		assertEquals(0, fc.getScore(1));
	}
	
}
