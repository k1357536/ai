package at.jku.cp.ai.tests.assignment1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.DistanceHeuristic;
import at.jku.cp.ai.rau.functions.IBoardPredicate;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.algorithms.GBFS;
import at.jku.cp.ai.utils.Constants;
import at.jku.cp.ai.utils.PathUtils;
import at.jku.cp.ai.utils.TestUtils;

@RunWith(Parameterized.class)
public class TestHeuristicSearchers {
	@Parameters
	public static Collection<Object[]> generateParams() {
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < Constants.NUMBER_OF_LEVELS; i++) {
			params.add(new Object[] { new Integer(i) });
		}
		return params;
	}

	@Rule
	public Timeout timeout = new Timeout(10000);
	
	private String pathToLevel;

	public TestHeuristicSearchers(Integer i) {
		pathToLevel = String.format(Constants.ASSET_PATH + "/assignment1/L%d", i);
	}

	@Test
	public void testGBFSforEuclideanDistance() throws Exception {
		testSearcherForLevel(
				Board.fromLevelFile(pathToLevel + "/level"),
				IBoardNode.class,
				GBFS.class,
				new DistanceHeuristic(
						board -> board.getCurrentUnicorn().pos,
						board -> board.getFountains().get(0).pos,
						(a, b) -> V.euclidean(a, b)
				),
				PathUtils.fromFile(pathToLevel + "/gbfs_ec.path"));
	}
	
	@Test
	public void testGBFSforManhattanDistance() throws Exception {
		testSearcherForLevel(
				Board.fromLevelFile(pathToLevel + "/level"),
				IBoardNode.class,
				GBFS.class,
				new DistanceHeuristic(
						board -> board.getCurrentUnicorn().pos,
						board -> board.getFountains().get(0).pos,
						(a, b) -> (double) V.manhattan(a, b)
				),
				PathUtils.fromFile(pathToLevel + "/gbfs_mh.path"));
	}

	private void testSearcherForLevel(IBoard board, Class<?> nodeClazz, Class<?> searcherClazz, Function<Node, Double> heuristic, List<V> expectedPath) throws Exception {
		IBoard startBoard = board.copy();
		final Fountain end = board.getFountains().get(0);

		Predicate<Node> endReached = new IBoardPredicate(
				b -> b.isRunning() && b.getCurrentUnicorn().pos.equals(end.pos));

		List<Move> expectedMoveSequence = PathUtils.vsToMoves(expectedPath);
		List<IBoard> expectedBoardStates = PathUtils.movesToIBoards(expectedMoveSequence, board.copy());

		Search searcher = (Search) searcherClazz.getDeclaredConstructor(Function.class).newInstance(heuristic);
		Node startNode = (Node) nodeClazz.getDeclaredConstructor(IBoard.class).newInstance(startBoard);
		Node endNode = searcher.search(startNode, endReached);

		List<Node> path = PathUtils.getPath(endNode);
		List<IBoard> actualBoardStates = PathUtils.getStates(path);
		List<Move> actualMoveSequence = PathUtils.getActions(path);
		
		TestUtils.assertListEquals(expectedBoardStates, actualBoardStates);
		TestUtils.assertListEquals(expectedMoveSequence, actualMoveSequence);
	}
}
