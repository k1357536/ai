package at.jku.cp.ai.rau.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.Node;

public class OnlySafeMoveNode implements Node
{
	private OnlySafeMoveNode parent;
	private IBoard board;
	private Move move;

	public OnlySafeMoveNode(IBoard board)
	{
		this(null, null, board);
	}

	public OnlySafeMoveNode(OnlySafeMoveNode parent, Move move, IBoard board)
	{
		this.parent = parent;
		this.move = move;
		this.board = board;
		
	}

	@Override
	public List<Node> adjacent()
	{
		if (!board.isRunning())
			return Collections.emptyList();

		List<Move> possible = board.getPossibleMoves();

		possible.remove(Move.SPAWN);
		
		List<Node> successors = new ArrayList<>(possible.size());
		for (Move move : possible)
		{
			IBoard next = board.copy();
			next.executeMove(move);
			successors.add(new OnlySafeMoveNode(this, move, next));
		}

		return successors;
	}

	@Override
	public Node parent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		return !board.isRunning();
	}
	
	@Override
	public boolean isRoot() {
		return parent == null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <State> State getState() {
		return (State) board;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <Action> Action getAction() {
		return (Action) move;
	}

	@Override
	public String toString() {
		return board.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OnlySafeMoveNode other = (OnlySafeMoveNode) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		return true;
	}
	
}
