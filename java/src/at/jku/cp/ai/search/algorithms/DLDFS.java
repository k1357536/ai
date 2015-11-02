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
	private StackWithFastContains<Node> path;

	private int limit;

	public DLDFS(int limit) {
		this.limit = limit;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		// create empty path
		path = new StackWithFastContains<>();
		// start recursive call with max depth
		return dls(start, endPredicate, limit);
	}

	private Node dls(Node node, Predicate<Node> endPredicate, int depth) {
		path.push(node);
		if (depth >= 0) {
			if (endPredicate.test(node)) {
				return node;
			}

			for (Node n : node.adjacent()) {
				if (!path.contains(n)) {
					// recursively call dls for adjacent nodes (= LIFO)
					// with a lower depth value
					Node result = dls(n, endPredicate, depth - 1);
					if (result != null) {
						return result;
					}
				}
			}
		}
		// limit is reached -> remove node form path
		path.pop();
		return null;
	}
}