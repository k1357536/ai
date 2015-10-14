package at.jku.cp.ai.search.algorithms;

import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

// Greedy Best-First Search
public class GBFS implements Search
{
	@SuppressWarnings("unused")
	private Function<Node, Double> heuristic;

	public GBFS(Function<Node, Double> heuristic) {
		this.heuristic = heuristic;
	}
	
	@Override
	public Node search(Node start, Predicate<Node> endPredicate)
	{
		// TODO, assignment 1
		return null;
	}
}
