package at.jku.cp.ai.search.algorithms;

import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

// Uniform Cost Search
public class UCS implements Search
{
	@SuppressWarnings("unused")
	private Function<Node, Double> cost;

	public UCS(Function<Node, Double> cost) {
		this.cost = cost;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate)
	{
		// TODO, assignment1
		return null;
	}
}
