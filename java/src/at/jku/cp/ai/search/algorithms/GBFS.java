package at.jku.cp.ai.search.algorithms;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;
import at.jku.cp.ai.search.datastructures.StackWithFastContains;

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

		StablePriorityQueue<Double, Node> pq = new StablePriorityQueue<Double, Node>();
		pq.add(new Pair<Double, Node>(heuristic.apply(start), start));
		visited.add(start);
		
		Node n;
		do {
			n = pq.poll().s;

			if (endPredicate.test(n))
				return n;
			
			for (Node n1 : n.adjacent()) {
				if (!visited.contains(n1)) {
					pq.add(new Pair<Double, Node>(heuristic.apply(n1), n1));
					visited.add(n1);
				}
			}
		} while (pq.size() > 0);
		return null;
	}
}
