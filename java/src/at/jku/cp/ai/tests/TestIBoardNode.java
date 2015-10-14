package at.jku.cp.ai.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Seed;
import at.jku.cp.ai.search.Node;

public class TestIBoardNode {

	@Test
	public void saneHashCode()
	{
		IBoard board = Board.fromLevelRepresentation(Arrays.asList(
				"###",
				"#p#",
				"###"));

		IBoardNode node = new IBoardNode(board);
		
		int expectedHash = node.hashCode();

		Node current = node;
		for (int i = 0; i < Seed.DEFAULT_FUSE; i++)
		{
			assertEquals(2, current.adjacent().size());
			
			// this is always the 'STAY' node
			current = current.adjacent().get(0);
		}
		int actualHash = current.hashCode();

		assertEquals(expectedHash, actualHash);
	}
}
