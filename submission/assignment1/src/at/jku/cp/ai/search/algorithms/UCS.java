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
		// priority queue for fringe -> nodes must be expanded by there priority order
		StablePriorityQueue<Double, Node> fringe = new StablePriorityQueue<>();
		// HashSet for closed list, for O(1) insert and contains (see theory part)
		HashSet<Node> closedList = new HashSet<>();

		fringe.add(new Pair<>(cost.apply(start), start));

		Pair<Double, Node> curPair;
		Node current;
		Double pathCosts;
		while(!fringe.isEmpty()) {
			// get node from fringe with lowest cost
			curPair = fringe.poll();
			current = curPair.s;
			pathCosts = curPair.f;

			if (endPredicate.test(current)) {
				return current;
			}

			// only expand node if not already visited (= in closed list)
			if (!closedList.contains(current)) {
				closedList.add(current);
				for (Node n : current.adjacent()) {
					// calculate cost for node: costs from root to current node + cost to next node
					fringe.add(new Pair<>(pathCosts + cost.apply(n), n));
				}
			}
		}
		return null;
	}
}
