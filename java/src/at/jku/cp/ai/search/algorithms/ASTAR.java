package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
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

	private class MyPair implements Comparable<MyPair> {
		double heuristic;
		double cost;

		public MyPair(double heuristic, double cost) {
			this.heuristic = heuristic;
			this.cost = cost;
		}

		@Override
		public int compareTo(MyPair other) {
			return Double.compare(heuristic + cost, other.heuristic + other.cost);
		}
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		HashSet<Node> visited = new HashSet<Node>();

		StablePriorityQueue<MyPair, Node> fringe = new StablePriorityQueue<>();
		fringe.add(new Pair<MyPair, Node>(new MyPair(heuristic.apply(start), cost.apply(start)), start));

		Pair<MyPair, Node> p;
		Node n;
		do {
			p = fringe.poll();
			n = p.s;
			if (visited.contains(n))
				continue;

			if (endPredicate.test(n))
				return n;

			visited.add(n);

			for (Node n1 : n.adjacent()) {
				MyPair pair = new MyPair(heuristic.apply(n1), p.f.cost + cost.apply(n1));
				fringe.add(new Pair<MyPair, Node>(pair, n1));
			}
		} while (!fringe.isEmpty());
		return null;
	}
}
