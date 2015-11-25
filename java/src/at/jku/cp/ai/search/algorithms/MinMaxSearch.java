package at.jku.cp.ai.search.algorithms;

import java.util.function.BiPredicate;
import java.util.function.Function;

import at.jku.cp.ai.search.AdversarialSearch;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.datastructures.Pair;

public class MinMaxSearch implements AdversarialSearch {

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
	 * limiting predicate. The predicate returns 'true' as long as we are below the limit,
	 * and 'false', if we exceed the limit.
	 * 
	 * @param searchLimitingPredicate
	 */
	public MinMaxSearch(BiPredicate<Integer, Node> slp)
	{
		this.searchLimitingPredicate = slp;
	}

	public Pair<Node, Double> search(Node start, Function<Node, Double> evalFunction) {
		// TODO: implement minmax search here
		return new Pair<Node, Double>(start, 0d);
	}
}
