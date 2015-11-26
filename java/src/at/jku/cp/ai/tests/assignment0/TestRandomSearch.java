package at.jku.cp.ai.tests.assignment0;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.IBoardPredicate;
import at.jku.cp.ai.rau.nodes.AlwaysMoveNode;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.algorithms.RS;
import at.jku.cp.ai.utils.Constants;
import at.jku.cp.ai.utils.PathUtils;
import at.jku.cp.ai.utils.TestUtils;

@RunWith(Parameterized.class)
public class TestRandomSearch {

	public static final int to = 40000;

	@Parameters
	public static Collection<Object[]> generateParams() {
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < Constants.NUMBER_OF_LEVELS; i++) {
			params.add(new Object[] { new Integer(i) });
		}
		return params;
	}

	private String pathToLevel;

	public TestRandomSearch(Integer i) {
		pathToLevel = String.format(Constants.ASSET_PATH + "/assignment1/L%d", i);
	}

	@Test(timeout = to)
	public void randomSearch() throws Exception {
		testSearcherForLevel(Board.fromLevelFile(pathToLevel + "/level"), PathUtils.fromFile(pathToLevel + "/rs.path"),
				AlwaysMoveNode.class, new RS());
	}

	private void testSearcherForLevel(IBoard board, List<V> expectedPath, Class<?> nodeClazz, Search searcher)
			throws Exception {
		IBoard startBoard = board.copy();
		final Fountain end = board.getFountains().get(0);

		Predicate<Node> endReached = new IBoardPredicate(
				b -> b.isRunning() && b.getCurrentUnicorn().pos.equals(end.pos));

		List<Move> expectedMoveSequence = PathUtils.vsToMoves(expectedPath);
		List<IBoard> expectedBoardStates = PathUtils.movesToIBoards(expectedMoveSequence, board.copy());

		Node startNode = (Node) nodeClazz.getDeclaredConstructor(IBoard.class).newInstance(startBoard);
		Node endNode = searcher.search(startNode, endReached);

		List<Node> path = PathUtils.getPath(endNode);
		List<IBoard> actualBoardStates = PathUtils.getStates(path);
		TestUtils.assertListEquals(expectedBoardStates, actualBoardStates);
	}
}
