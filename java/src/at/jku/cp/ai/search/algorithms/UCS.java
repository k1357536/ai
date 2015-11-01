package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

// Uniform Cost Search
public class UCS implements Search
{
	private Function<Node, Double> cost;

	public UCS(Function<Node, Double> cost) {
		this.cost = cost;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate)
	{
		StablePriorityQueue<Double, Node> fringe = new StablePriorityQueue<>();
		HashSet<Node> closedList = new HashSet<>();
		fringe.add(new Pair<>(cost.apply(start), start));

		Node current;
		Double pathCosts;
		Pair<Double, Node> curPair;
		while(!fringe.isEmpty()) {
			curPair = fringe.poll();
			current = curPair.s;
			pathCosts = curPair.f;

			if (endPredicate.test(current)) {
				return current;
			}

			if (!closedList.contains(current)) {
				closedList.add(current);
				for (Node n : current.adjacent()) {
					fringe.add(new Pair<>(pathCosts + cost.apply(n), n));
				}
			}
		}
		return null;
	}
}
