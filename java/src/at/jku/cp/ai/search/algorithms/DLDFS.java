package at.jku.cp.ai.search.algorithms;

import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.datastructures.StackWithFastContains;

// Depth-Limited Depth-First Search
public class DLDFS implements Search {
	// we need an O(1) datastructure for path-avoidance.
	// 'contains' is O(N) in a stack, where N
	// is the current depth, so we use a stack and a set in parallel
	@SuppressWarnings("unused")
	private StackWithFastContains<Node> path;
	@SuppressWarnings("unused")
	private int limit;

	public DLDFS(int limit) {
		this.limit = limit;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		// TODO, assignment1
		return null;
	}
}