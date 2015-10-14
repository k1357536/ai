package at.jku.cp.ai.rau.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;

public class OnlyPositionNode implements Node
{
	private OnlyPositionNode parent;
	private IBoard board;
	private Move move;
	private V pos;
	
	public OnlyPositionNode(IBoard board, V pos)
	{
		this.parent = null;
		this.move = null;
		this.board = board;
		this.pos = pos;
	}

	public OnlyPositionNode(OnlyPositionNode parent, Move move, V pos)
	{
		this.parent = parent;
		this.board = parent.board;
		this.move = move;
		this.pos = pos;
	}

	@Override
	public List<Node> adjacent()
	{
		if (!board.isRunning())
			return Collections.emptyList();

		Map<Move, V> mapping = Board.getMoveToDirectionMapping();
		List<Node> successors = new ArrayList<>();
		for (Move move : Move.values())
		{
			V next = V.add(pos, mapping.get(move));
			if(board.isPassable(next))
			{
				successors.add(new OnlyPositionNode(this, move, next));
			}
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
		return (State) pos;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <Action> Action getAction() {
		return (Action) move;
	}

	@Override
	public String toString() {
		return pos.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OnlyPositionNode other = (OnlyPositionNode) obj;
		if (board == null)
		{
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (pos == null)
		{
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		return true;
	}

	
	
}
