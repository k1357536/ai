package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

// Greedy Best-First Search
public class GBFS implements Search {
	private Function<Node, Double> heuristic;

	public GBFS(Function<Node, Double> heuristic) {
		this.heuristic = heuristic;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		// 18 sec
		HashSet<Node> visited = new HashSet<Node>();

		StablePriorityQueue<Double, Node> fringe = new StablePriorityQueue<Double, Node>();
		fringe.add(new Pair<Double, Node>(heuristic.apply(start), start));
		visited.add(start);

		Node n;
		do {
			n = fringe.poll().s;

			if (endPredicate.test(n))
				return n;

			for (Node n1 : n.adjacent()) {
				if (!visited.contains(n1)) {
					fringe.add(new Pair<Double, Node>(heuristic.apply(n1), n1));
					visited.add(n1);
				}
			}
		} while (!fringe.isEmpty());
		return null;
	}
}
