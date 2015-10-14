package at.jku.cp.ai.search.algorithms;

import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

// Iterative Deepening Search
public class IDS implements Search
{
	@SuppressWarnings("unused")
	private int limit;

	public IDS(int limit)
	{
		this.limit = limit;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate)
	{
		// TODO, assignment1
		return null;
	}

}
