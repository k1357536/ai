package at.jku.cp.ai.tests.assignment2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.function.Function;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.endconditions.PointCollecting;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.AdversarialSearch;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.datastructures.Pair;

public abstract class AdversarialSearchersBase
{
	protected IBoard startBoard;
	protected List<Move> expectedMAXMoves;
	protected List<Move> expectedMINMoves;
	protected Integer depth;
	protected boolean verbose;

	public AdversarialSearchersBase(
			List<String> level,
			List<Move> expectedMAXMoves,
			List<Move> expectedMINMoves,
			Integer depth)
	{
		this.startBoard = Board.fromLevelRepresentation(level);
		this.expectedMAXMoves = expectedMAXMoves;
		this.expectedMINMoves = expectedMINMoves;
		this.depth = depth;
		
		// TODO: in case you are wondering, what's going on, set this to 'true'
		this.verbose = false;
	}

	static Function<Node, Double> testEvalFunction = new Function<Node, Double>()
	{
		@Override
		public Double apply(Node node)
		{
			IBoard board = (IBoard) node.getState();
			PointCollecting endCondition = ((PointCollecting) board.getEndCondition());
			int winner = endCondition.getWinner();
			double points = endCondition.getScore(0) - endCondition.getScore(1);
			if (winner != -1)
			{
				if (winner == 0)
					points += (10000 - board.getTick());
				else if (winner == 1)
					points += -(10000 - board.getTick());
			}
			return points;
		}
	};

	protected void testGameSearchMAX(AdversarialSearch searcher)
	{
		IBoard board = startBoard.copy();
		board.setEndCondition(new PointCollecting());

		if (verbose)
			System.out.println(board);

		for (int i = 0; i < expectedMAXMoves.size(); i++)
		{
			Pair<Node, Double> bestMinMax = searcher.search(new IBoardNode(board), testEvalFunction);
			Move actualMAXMove = bestMinMax.f.getAction();
			
			if (verbose)
				System.out.println("actual MAX move   : " + actualMAXMove);

			if (i < expectedMAXMoves.size())
			{
				Move expectedMAXMove = expectedMAXMoves.get(i);
				assertEquals(expectedMAXMove, actualMAXMove);
				board.executeMove(actualMAXMove);
				
				if (verbose)
					System.out.println(board);
			}

			if (i < expectedMINMoves.size())
			{
				if (verbose)
					System.out.println("expected MIN move : " + expectedMINMoves.get(i));
				
				board.executeMove(expectedMINMoves.get(i));
				
				if (verbose)
					System.out.println(board);
			}
			else
			{
				fail("MAX player took too long! no more moves for the MIN player in test fixture!");
			}

		}
	}
}
