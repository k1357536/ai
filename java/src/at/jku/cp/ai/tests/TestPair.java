package at.jku.cp.ai.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.utils.Constants;

public class TestPair {
	@Test
	public void hashCodesEqual()
	{
		for (Move move : Move.values())
		{
			Pair<IBoard, Move> a = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L0/level"), move);
			Pair<IBoard, Move> b = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L0/level"), move);

			assertEquals(a, b);
			assertEquals(a.hashCode(), b.hashCode());
		}
	}

	@Test
	public void hashCodesNotEqualBCMoves()
	{
		Pair<IBoard, Move> a = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L0/level"), Move.DOWN);
		Pair<IBoard, Move> b = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L0/level"), Move.UP);

		assertNotEquals(a, b);
		assertNotEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void hashCodesNotEqualBCBoards()
	{
		Pair<IBoard, Move> a = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L0/level"), Move.STAY);
		Pair<IBoard, Move> b = new Pair<>(Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L1/level"), Move.STAY);

		assertNotEquals(a, b);
		assertNotEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void useInMap()
	{
		Board a = Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L0/level");
		Board b = Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L1/level");

		Map<Pair<IBoard, Move>, Integer> qmatrix = new HashMap<>();
		qmatrix.put(new Pair<>(a.copy(), Move.DOWN), 1);
		qmatrix.put(new Pair<>(b.copy(), Move.UP), 2);

		for (int i = 0; i < 10; i++) {
			a.executeMove(Move.STAY);
			b.executeMove(Move.STAY);
		}

		assertEquals(2, qmatrix.size());

		assertEquals((int) 1, (int) qmatrix.get(new Pair<>(a.copy(), Move.DOWN)));
		assertEquals((int) 2, (int) qmatrix.get(new Pair<>(b.copy(), Move.UP)));
	}

	@Test
	public void useInMap2()
	{
		Board a = Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L0/level");
		Board b = Board.fromLevelFile(Constants.ASSET_PATH + "/assignment1/L0/level");

		IBoard aKey = a.copy();
		IBoard bKey = b.copy();

		Map<Pair<IBoard, Move>, Integer> qmatrix = new HashMap<>();
		qmatrix.put(new Pair<>(aKey, Move.DOWN), 1);
		qmatrix.put(new Pair<>(bKey, Move.UP), 2);

		Random random = new Random(0L);
		for (int i = 0; i < 100; i++) {
			a.executeMove(Move.values()[random.nextInt(Move.values().length)]);
			b.executeMove(Move.values()[random.nextInt(Move.values().length)]);
		}

		assertNotEquals(a, aKey);
		assertNotEquals(b, bKey);

		assertEquals(2, qmatrix.size());

		assertEquals((int) 1, (int) qmatrix.get(new Pair<>(aKey, Move.DOWN)));
		assertEquals((int) 2, (int) qmatrix.get(new Pair<>(bKey, Move.UP)));
	}

	@Test
	public void equalsContract()
	{
		EqualsVerifier.forClass(Pair.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}
}
