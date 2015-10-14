package at.jku.cp.ai.rau.functions;

import java.util.function.Function;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;

public class DistanceHeuristic implements Function<Node, Double> {

	private java.util.function.Function<IBoard, V> start;
	private java.util.function.Function<IBoard, V> goal;
	private java.util.function.BiFunction<V, V, Double> d;

	public DistanceHeuristic(
			java.util.function.Function<IBoard, V> start,
			java.util.function.Function<IBoard, V> goal,
			java.util.function.BiFunction<V, V, Double> distanceMeasure) {
		this.start = start;
		this.goal = goal;
		this.d = distanceMeasure;
	}

	@Override
	public Double apply(Node node) {
		IBoard board = (IBoard) node.getState();
		if (board.isRunning()) {
			V av = start.apply(board);
			V bv = goal.apply(board);
			return d.apply(av, bv);
		}
		else
		{
			return Double.POSITIVE_INFINITY;
		}
	}
}
