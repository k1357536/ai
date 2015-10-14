package at.jku.cp.ai.rau.functions;

import java.util.function.Predicate;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.search.Node;

public class IBoardPredicate implements Predicate<Node> {
	private Predicate<IBoard> p;

	public IBoardPredicate(Predicate<IBoard> p) {
		this.p = p;
	}

	@Override
	public boolean test(Node node) {
		IBoard board = (IBoard) node.getState();
		return p.test(board);
	}
}
