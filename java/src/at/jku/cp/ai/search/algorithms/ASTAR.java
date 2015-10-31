package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

// A* Search
public class ASTAR implements Search {
	private Function<Node, Double> heuristic;
	private Function<Node, Double> cost;

	public ASTAR(Function<Node, Double> heuristic, Function<Node, Double> cost) {
		this.heuristic = heuristic;
		this.cost = cost;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		HashSet<Node> visited = new HashSet<Node>();

		StablePriorityQueue<Double, Node> pq = new StablePriorityQueue<Double, Node>();
		pq.add(new Pair<Double, Node>(heuristic.apply(start), start));
		visited.add(start);

		Node n;
		do {
			n = pq.poll().s;

			if (endPredicate.test(n))
				return n;

			for (Node n1 : n.adjacent()) {
				double c = heuristic.apply(n1) + cost.apply(n1);
				if (!visited.contains(n1)) {
					pq.add(new Pair<Double, Node>(c, n1));
					visited.add(n1);
				}
			}
		} while (pq.size() > 0);
		return null;
	}
}
