package at.jku.cp.ai.search.algorithms;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;


// Breadth-First search
public class BFS implements Search
{
	@Override
	public Node search(Node start, Predicate<Node> endPredicate)
	{
		LinkedList<Node> fringe = new LinkedList<>();
		HashSet<Node> closedList = new HashSet<>();
		fringe.add(start);

		Node current;
		while(!fringe.isEmpty()) {
			current = fringe.remove(0);
			if (endPredicate.test(current)) {
				return current;
			}

			if (!closedList.contains(current)) {
				closedList.add(current);
				fringe.addAll(current.adjacent());
			}
		}
		return null;
	}
}