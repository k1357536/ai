package at.jku.cp.ai.search.algorithms;

import java.util.function.Predicate;

import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;

// Iterative Deepening Search
public class IDS implements Search
{
	private int limit;

	public IDS(int limit)
	{
		this.limit = limit;
	}

	@Override
	public Node search(Node start, Predicate<Node> endPredicate) {
		DLDFS searcher;
		for(int i = 0; i <= limit; i++) {
			searcher = new DLDFS(i);
			Node result =  searcher.search(start, endPredicate);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

}
