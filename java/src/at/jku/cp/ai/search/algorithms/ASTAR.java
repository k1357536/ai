package at.jku.cp.ai.search.algorithms;

import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

// A* Search
public class ASTAR implements Search
{
	@SuppressWarnings("unused")
	private Function<Node, Double> heuristic;
	@SuppressWarnings("unused")
	private Function<Node, Double> cost;

	public ASTAR(Function<Node, Double> heuristic, Function<Node, Double> cost) {
		this.heuristic = heuristic;
		this.cost = cost;
	}
	
	
	@Override
	public Node search(Node start, Predicate<Node> endPredicate)
	{
		// TODO, assignment1
		return null;
	}
}
