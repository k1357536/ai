package at.jku.cp.ai.tests.assignment2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.algorithms.AlphaBetaSearch;

@RunWith(Parameterized.class)
public class TestAlphaBeta extends AdversarialSearchersBase
{

	@Parameters
	public static Collection<Object[]> generateParams()
	{
		// test AlphaBeta on all the tests for the MinMax algorithm
		List<Object[]> params = (List<Object[]>) TestMinMax.generateParams();

		// for the following two test cases, MinMax
		// takes too long. So these are AlphaBeta exclusive

		// numbering starts with '12' b/c of the 11 tests in TestMinMax
		// 12
		params.add(new Object[] {
				Arrays.asList(
						"####",
						"#p##",
						"#f.#",
						"#.##",
						"#p##",
						"#f##",
						"####"
						),
				Arrays.asList(
						Move.DOWN,
						Move.DOWN,
						Move.SPAWN,
						Move.STAY,
						Move.UP,
						Move.RIGHT
						),
				Arrays.asList(
						Move.DOWN,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY
						),
				14
		});

		// 13
		params.add(new Object[] {
				Arrays.asList(
						"#####",
						"#p.######",
						"#.c....f#",
						"#.#######",
						"#cf##",
						"#p###",
						"#####"
						),
				Arrays.asList(
						Move.RIGHT,
						Move.SPAWN,
						Move.STAY,
						Move.LEFT,
						Move.DOWN,
						Move.RIGHT,
						Move.RIGHT,
						Move.RIGHT,
						Move.RIGHT,
						Move.RIGHT,
						Move.RIGHT,
						Move.STAY
						),
				Arrays.asList(
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY
						),
				20
		});

		return params;
	}

	public TestAlphaBeta(List<String> level, List<Move> expectedMAXMoves, List<Move> expectedMINMoves, Integer depth)
	{
		super(level, expectedMAXMoves, expectedMINMoves, depth);
	}

	@Test
	public void testAlphaBetaMAX()
	{
		testGameSearchMAX(new AlphaBetaSearch((depth, board) -> depth <= this.depth));
	}
}
