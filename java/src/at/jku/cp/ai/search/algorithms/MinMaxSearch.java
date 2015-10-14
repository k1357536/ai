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

	public MinMaxSearch(BiPredicate<Integer, Node> slp)
	{
		this.searchLimitingPredicate = slp;
	}

	public Pair<Node, Double> search(Node start, Function<Node, Double> evalFunction) {
		return new Pair<Node, Double>(start, 0d);
	}
}
