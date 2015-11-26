package at.jku.cp.ai.search.algorithms;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

import at.jku.cp.ai.search.AdversarialSearch;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.datastructures.Pair;

public class MinMaxSearch implements AdversarialSearch {
	private Function<Node, Double> boardEvaluationFunction;
	private BiPredicate<Integer, Node> searchLimitingPredicate;

	/**
	 * To limit the extent of the search, this implementation should honor a
	 * limiting predicate. The predicate returns 'true' as long as we are below
	 * the limit, and 'false', if we exceed the limit.
	 * 
	 * @param searchLimitingPredicate
	 */
	public MinMaxSearch(BiPredicate<Integer, Node> slp) {
		this.searchLimitingPredicate = slp;
	}

	public Pair<Node, Double> search(Node start, Function<Node, Double> evalFunction) {
		boardEvaluationFunction = evalFunction;

		double best = Double.NEGATIVE_INFINITY;
		Node result = start;
		for (Node n : start.adjacent()) {
			double value = minValue(n, 0);
			if (value > best) {
				best = value;
				result = n;
			}
		}
		return new Pair<Node, Double>(result, best);
	}

	private double maxValue(Node current, int level) {
		double best = boardEvaluationFunction.apply(current);
		if (searchLimitingPredicate.test(level, current)) {
			double bestC = Double.NEGATIVE_INFINITY;
			for (Node n : current.adjacent()) {
				double value = minValue(n, level + 1);
				if (value > bestC) {
					best = bestC = value;
				}
			}
		}
		return best;
	}

	private double minValue(Node current, int level) {
		double best = boardEvaluationFunction.apply(current);
		if (searchLimitingPredicate.test(level, current)) {
			double bestC = Double.POSITIVE_INFINITY;
			for (Node n : current.adjacent()) {
				double value = maxValue(n, level + 1);
				if (value < bestC) {
					best = bestC = value;
				}
			}
		}
		return best;
	}
}
