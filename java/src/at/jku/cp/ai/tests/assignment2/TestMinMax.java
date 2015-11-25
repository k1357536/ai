package at.jku.cp.ai.tests.assignment2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.algorithms.MinMaxSearch;

@RunWith(Parameterized.class)
public class TestMinMax extends AdversarialSearchersBase
{
	
	@Parameters
	public static Collection<Object[]> generateParams()
	{

		List<Object[]> params = new ArrayList<Object[]>();

		// tests 0 - 11
		// 0
		params.add(new Object[] {
				Arrays.asList(
						"#######",
						"#p.f#p#",
						"#######"
						),
				Arrays.asList(
						Move.RIGHT,
						Move.RIGHT,
						Move.STAY
						),
				Arrays.asList(
						Move.STAY,
						Move.STAY,
						Move.STAY
						),
				10
		});
		
		// 1
		params.add(new Object[] {
				Arrays.asList(
						"##########",
						"#f.p..f#p#",
						"##########"
						),
				Arrays.asList(
						Move.LEFT,
						Move.LEFT,
						Move.STAY
						),
				Arrays.asList(
						Move.STAY,
						Move.STAY,
						Move.STAY
						),
				6
		});
		
		// 2
		params.add(new Object[] {
				Arrays.asList(
						"#########",
						"#p..f..p#",
						"#########"
						),
				Arrays.asList(
						Move.RIGHT,
						Move.RIGHT,
						Move.RIGHT
						),
				Arrays.asList(
						Move.LEFT,
						Move.LEFT,
						Move.LEFT
						),
				6
		});
		
		// 3
		params.add(new Object[] {
				Arrays.asList(
						"####",
						"#pf#",
						"#.##",
						"#.#",
						"#p#",
						"###"
						),
				Arrays.asList(
						Move.DOWN,
						Move.SPAWN,
						Move.UP,
						Move.RIGHT,
						Move.STAY
						),
				Arrays.asList(
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY,
						Move.STAY
						),
				10
		});

		// different search depth gives different best moves ...
		// 4
		params.add(new Object[] {
				Arrays.asList(
						"####",
						"#pf#",
						"#.##",
						"#.#",
						"#p#",
						"###"
						),
				Arrays.asList(
						Move.RIGHT
						),
				Arrays.asList(
						Move.STAY
						),
				2
		});

		// 5
		params.add(new Object[] {
				Arrays.asList(
						"###",
						"#p##",
						"#f.#",
						"#.##",
						"#p#",
						"#f#",
						"###"
						),
				Arrays.asList(
						Move.DOWN,
						Move.STAY,
						Move.STAY
						),
				Arrays.asList(
						Move.DOWN,
						Move.STAY,
						Move.STAY
						),
				6
		});

		// 6
		params.add(new Object[] {
				Arrays.asList(
						"####",
						"#p.#",
						"#.##",
						"#c#",
						"#f#",
						"###",
						"#p#",
						"###"
						),
				Arrays.asList(
						Move.DOWN,
						Move.SPAWN,
						Move.STAY,
						Move.UP,
						Move.RIGHT,
						Move.LEFT,
						Move.DOWN,
						Move.DOWN,
						Move.DOWN,
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
						Move.STAY
						),
				17
		});

		// 7
		params.add(new Object[] {
				Arrays.asList(
						"#####",
						"#p.##",
						"#.cf#",
						"#c###",
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
						Move.STAY
						),
				17
		});

		// 8
		params.add(new Object[] {
				Arrays.asList(
						"####",
						"#..#",
						"#pf#",
						"#.##",
						"#p##",
						"####"
						),
				Arrays.asList(
						Move.RIGHT,
						Move.STAY
						),
				Arrays.asList(
						Move.UP,
						Move.UP
						),
				8
		});

		// 9
		params.add(new Object[] {
				Arrays.asList(
						"######",
						"#..###",
						"##p.f#",
						"##.###",
						"##p###",
						"######"
						),
				Arrays.asList(
						Move.RIGHT,
						Move.RIGHT,
						Move.STAY,
						Move.STAY,
						Move.STAY
						),
				Arrays.asList(
						Move.UP,
						Move.UP,
						Move.SPAWN,
						Move.UP,
						Move.LEFT
						),
				6
		});

		// 10
		params.add(new Object[] {
				Arrays.asList(
						"######",
						"#..###",
						"##p.f#",
						"##.###",
						"##p###",
						"######"
						),
				Arrays.asList(
						Move.SPAWN,
						Move.STAY,
						Move.UP,
						Move.LEFT
						),
				Arrays.asList(
						Move.UP,
						Move.STAY,
						Move.STAY,
						Move.STAY
						),
				10
		});

		// 11
		params.add(new Object[] {
				Arrays.asList(
						"####",
						"#..#",
						"#pf#",
						"#.##",
						"#p##",
						"####"
						),
				Arrays.asList(
						Move.SPAWN,
						Move.RIGHT,
						Move.STAY,
						Move.UP
						),
				Arrays.asList(
						Move.UP,
						Move.STAY,
						Move.STAY,
						Move.STAY
						),
				10
		});

		return params;
	}

	public TestMinMax(List<String> level, List<Move> expectedMAXMoves, List<Move> expectedMINMoves, Integer depth)
	{
		super(level, expectedMAXMoves, expectedMINMoves, depth);
	}

	@Test
	public void testMinMaxMAX()
	{
		testGameSearchMAX(new MinMaxSearch((depth, board) -> depth <= this.depth));
	}

}
