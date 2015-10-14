package at.jku.cp.ai.tests;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;

@RunWith(Parameterized.class)
public class TestMoveSequences
{
	@Parameters
	public static Collection<Object[]> generateParams()
	{
		List<Object[]> params = new ArrayList<Object[]>();

		params.add(new Object[] {
				Arrays.asList(
						"#######",
						"#.....#",
						"#..p..#",
						"#.....#",
						"#######"
						),
				Arrays.asList(Move.STAY, Move.STAY),
				Arrays.asList(
						Arrays.asList(Move.values()),
						Arrays.asList(Move.values())
						)
		});

		params.add(new Object[] {
				Arrays.asList(
						"###",
						"#p#",
						"#.###",
						"#...#",
						"#####"
						),
				Arrays.asList(Move.DOWN, Move.DOWN, Move.RIGHT, Move.RIGHT),
				Arrays.asList(
						Arrays.asList(Move.STAY, Move.SPAWN, Move.DOWN),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.UP, Move.DOWN),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.UP, Move.RIGHT),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT, Move.RIGHT)
						)
		});

		params.add(new Object[] {
				Arrays.asList(
						"##########",
						"#p#......#",
						"#.#.######",
						"#.......f#",
						"#.#.######",
						"#........#",
						"##########"
						),
				Arrays.asList(Move.DOWN, Move.DOWN, Move.RIGHT, Move.RIGHT, Move.RIGHT, Move.RIGHT),
				Arrays.asList(
						Arrays.asList(Move.STAY, Move.SPAWN, Move.DOWN),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.DOWN, Move.UP),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.UP, Move.DOWN, Move.RIGHT),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT, Move.RIGHT),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.UP, Move.DOWN, Move.LEFT, Move.RIGHT),
						Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT, Move.RIGHT)
						)
		});

		return params;
	}

	private Board masterBoard;
	private List<Move> path;
	private List<List<Move>> expectedValid;

	public TestMoveSequences(List<String> lvl, List<Move> path, List<List<Move>> expected)
	{
		this.masterBoard = Board.fromLevelRepresentation(lvl);
		this.path = path;
		this.expectedValid = expected;
	}

	@Test
	public void completeMoveSequencePossible()
	{
		IBoard board = masterBoard.copy();
		for (int i = 0; i < path.size(); i++)
		{
			List<Move> current = expectedValid.get(i);
			allMovesActuallyPossible(board, current);
			Move move = path.get(i);
			board.executeMove(move);
		}
	}

	private void allMovesActuallyPossible(IBoard testBoard, List<Move> currentExpectedValid)
	{
		for (Move move : currentExpectedValid)
		{
			IBoard board = testBoard.copy();

			// ***can't*** execute valid move? --> goto fail!
			if (!board.executeMove(move))
			{
				fail("could NOT execute '" + move + "' !");
			}
		}

		List<Move> expectedInvalid = new ArrayList<>();

		for (Move move : Move.values())
		{
			if (!currentExpectedValid.contains(move))
			{
				expectedInvalid.add(move);
			}
		}

		for (Move move : expectedInvalid)
		{
			IBoard board = testBoard.copy();

			// ***can*** execute invalid move? --> goto fail!
			if (board.executeMove(move))
			{
				fail("could execute '" + move + "' despite being forbidden !");
			}
		}
	}
}
