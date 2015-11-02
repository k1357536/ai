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
		// LinkedList for fringe, to get a FIFO data structure
		LinkedList<Node> fringe = new LinkedList<>();
		// HashSet for closed list, for O(1) insert and contains (see theory part)
		HashSet<Node> closedList = new HashSet<>();

		fringe.add(start);
		Node current;
		while(!fringe.isEmpty()) {
			// get first node from fringe -> FIFO
			current = fringe.remove(0);
			if (endPredicate.test(current)) {
				return current;
			}

			// only expand node if not already visited (= in closed list)
			if (!closedList.contains(current)) {
				closedList.add(current);
				fringe.addAll(current.adjacent());
			}
		}
		return null;
	}
}