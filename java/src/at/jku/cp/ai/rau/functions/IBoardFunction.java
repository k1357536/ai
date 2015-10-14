package at.jku.cp.ai.rau.functions;

import java.util.function.Function;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.search.Node;

public class IBoardFunction implements Function<Node, Double> {
	private Function<IBoard, Double> f;

	public IBoardFunction(Function<IBoard, Double> f) {
		this.f = f;
	}

	@Override
	public Double apply(Node node) {
		IBoard board = (IBoard) node.getState();
		return f.apply(board);
	}
}
