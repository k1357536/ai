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
public class TestMoves
{
	@Parameters
	public static Collection<Object[]> generateParams()
	{

		List<Object[]> params = new ArrayList<Object[]>();
		
		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#...#",
						"#.p.#",
						"#...#",
						"#####"
						),
				Arrays.asList(Move.values())
		});
		
		params.add(new Object[]{
				Arrays.asList(
						"###",
						"#p#",
						"#.#",
						"###"
						),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.DOWN)
		});
				
		params.add(new Object[]{
				Arrays.asList(
						"###",
						"#.#",
						"#p#",
						"###"
						),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.UP)
		});
		
		params.add(new Object[]{
				Arrays.asList(
						"####",
						"#p.#",
						"####"
						),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.RIGHT)
		});
		
		params.add(new Object[]{
				Arrays.asList(
						"####",
						"#.p#",
						"####"
						),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT)
		});
		
		params.add(new Object[]{
				Arrays.asList(
						"###",
						"#p#",
						"###"
						),
				Arrays.asList(Move.STAY, Move.SPAWN)
		});
		
		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#*pf#",
						"#####"
						),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.RIGHT)
		});
		
		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#*p*#",
						"#####"
						),
				Arrays.asList(Move.STAY, Move.SPAWN)
		});
		
		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#.*.#",
						"#*p*#",
						"#.*.#",
						"#####"
						),
				Arrays.asList(Move.STAY, Move.SPAWN)
		});
		
		params.add(new Object[]{
				Arrays.asList(
						"#####",
						"#fff#",
						"#fpf#",
						"#fff#",
						"#####"
						),
				Arrays.asList(Move.STAY, Move.SPAWN, Move.LEFT, Move.RIGHT, Move.UP, Move.DOWN)
		});
		
		return params;
	}

	private Board masterBoard;
	private List<Move> expectedValid;

	public TestMoves(List<String> lvl, List<Move> expected)
	{
		this.masterBoard = Board.fromLevelRepresentation(lvl);
		this.expectedValid = expected;
	}
	
	@Test
	public void allMovesActuallyPossible()
	{
		for(Move move: expectedValid)
		{
			IBoard board = masterBoard.copy();
			
			// ***can't*** execute valid move? --> goto fail!
			if (!board.executeMove(move))
			{
				fail("could NOT execute '" + move + "' !");
			}
		}
		
		List<Move> expectedInvalid = new ArrayList<>();
		
		for(Move move: Move.values())
		{
			if(!expectedValid.contains(move))
			{
				expectedInvalid.add(move);
			}
		}

		for(Move move: expectedInvalid)
		{
			IBoard board = masterBoard.copy();
			
			// ***can*** execute invalid move? --> goto fail!
			if (board.executeMove(move))
			{
				fail("could execute '" + move + "' despite being forbidden !");
			}
		}
	}
}