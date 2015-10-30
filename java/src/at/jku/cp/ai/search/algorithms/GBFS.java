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
		// 24 sec, use push instead of add
		//StackWithFastContains<Node> visited = new StackWithFastContains<>();
		
		// 18 sec
		Set<Node> visited = new HashSet<>();
		
		StablePriorityQueue<Double, Node> pq = new StablePriorityQueue<Double, Node>();
		pq.add(new Pair<Double, Node>(heuristic.apply(start), start));

		while (pq.size() > 0) {
			Node n = pq.poll().s;

			if (visited.contains(n))
				continue;
			if (endPredicate.test(n))
				return n;

			visited.add(n);
			for (Node n1 : n.adjacent())
				pq.add(new Pair<Double, Node>(heuristic.apply(n1), n1));
		}
		return null;
	}
}
