package at.jku.cp.ai.search.algorithms;

import java.util.function.BiPredicate;
import java.util.function.Function;

import at.jku.cp.ai.search.AdversarialSearch;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.datastructures.Pair;

public class AlphaBetaSearch implements AdversarialSearch {

	@SuppressWarnings("unused")
	private Function<Node, Double> boardEvaluationFunction;
	@SuppressWarnings("unused")
	private BiPredicate<Integer, Node> searchLimitingPredicate;
	@SuppressWarnings("unused")
	private Node best;
	@SuppressWarnings("unused")
	private double bestValue;

	/**
	 * To limit the extent of the search, this implementation should honor a
	 * limiting predicate. The predicate returns 'true' as long as we are below
	 * the limit, and 'false', if we exceed the limit.
	 * 
	 * @param searchLimitingPredicate
	 */
	public AlphaBetaSearch(BiPredicate<Integer, Node> searchLimitingPredicate) {
		this.searchLimitingPredicate = searchLimitingPredicate;
	}

	public Pair<Node, Double> search(Node start, Function<Node, Double> evalFunction) {
		boardEvaluationFunction = evalFunction;

		double best = Double.NEGATIVE_INFINITY;
		Node result = start;
		for (Node n : start.adjacent()) {
			double v;
			if (searchLimitingPredicate.test(0, n))
				v = minValue(n, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			else
				v = boardEvaluationFunction.apply(n);
			if (v > best) {
				best = v;
				result = n;
			}
		}
		return new Pair<Node, Double>(result, best);
	}

	private double maxValue(Node current, int level, double alpha, double beta) {
		double v = Double.NEGATIVE_INFINITY;
		for (Node n : current.adjacent()) {
			if (searchLimitingPredicate.test(level + 1, n))
				v = Math.max(v, minValue(n, level + 1, alpha, beta));
			else
				v = Math.max(v, boardEvaluationFunction.apply(n));
			if (v >= beta)
				return v;
			alpha = Math.max(alpha, v);
		}
		if (v == Double.NEGATIVE_INFINITY)
			return boardEvaluationFunction.apply(current);
		return v;
	}

	private double minValue(Node current, int level, double alpha, double beta) {
		double v = Double.POSITIVE_INFINITY;
		for (Node n : current.adjacent()) {
			if (searchLimitingPredicate.test(level + 1, n))
				v = Math.min(v, maxValue(n, level + 1, alpha, beta));
			else
				v = Math.min(v, boardEvaluationFunction.apply(n));
			if (v <= alpha)
				return v;
			beta = Math.min(beta, v);
		}
		if (v == Double.POSITIVE_INFINITY)
			return boardEvaluationFunction.apply(current);
		return v;
	}
}
