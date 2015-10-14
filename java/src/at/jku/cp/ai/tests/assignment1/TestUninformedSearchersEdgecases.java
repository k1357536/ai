package at.jku.cp.ai.tests.assignment1;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.algorithms.BFS;
import at.jku.cp.ai.search.algorithms.DLDFS;
import at.jku.cp.ai.search.algorithms.IDS;
import at.jku.cp.ai.utils.PathUtils;
import at.jku.cp.ai.utils.TestUtils;


public class TestUninformedSearchersEdgecases {
	@Rule
	public TestRule globalTimeout = new Timeout(1000);
	
	@Test
	public void bfsNoInfiniteLoop() {
		noInfiniteLoop(new BFS());
	}
	
	@Test
	public void idsNoInfiniteLoop() {
		noInfiniteLoop(new IDS(3));
	}
	
	@Test
	public void dfsNoInfiniteLoop() {
		noInfiniteLoop(new DLDFS(3));
	}
	
	private void noInfiniteLoop(Search searcher) {
		IBoard board = Board.fromLevelRepresentation(Arrays.asList(
				"####",
				"#p.#",
				"####"));

		IBoard startBoard = board.copy();
		Predicate<Node> endReached = b -> false;
		
		Node startNode = new IBoardNode(startBoard);
		Node endNode = searcher.search(startNode, endReached);

		List<Node> path = PathUtils.getPath(endNode);
		List<Node> actualBoardNodes = PathUtils.getStates(path);
		List<IBoard> actualBoardStates = PathUtils.getStates(actualBoardNodes);
		TestUtils.assertListEquals(Collections.emptyList(), actualBoardStates);
		
		List<Move> actualMoveSequence = PathUtils.getActions(path);
		TestUtils.assertListEquals(Collections.emptyList(), actualMoveSequence);
	}
}
