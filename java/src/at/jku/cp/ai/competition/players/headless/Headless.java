package at.jku.cp.ai.competition.players.headless;

import java.util.List;
import java.util.function.Predicate;

import at.jku.cp.ai.competition.players.Player;
import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.OnlyPositionNode;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.GameObject;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.Seed;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.algorithms.BFS;
import at.jku.cp.ai.utils.PathUtils;

public class Headless implements Player
{
	private enum State
	{
		SEARCH, RUN, SPAWN
	};

	private State state;

	public Headless()
	{
		state = State.SEARCH;
	}

	@Override
	public Move getNextMove(PlayerInfo mappings, IBoard board)
	{

		Move move = Move.STAY;
		Unicorn me = board.getCurrentUnicorn();

		switch (state)
		{
		case SEARCH:
			move = searchNextCloud(me, board);
			if (move == Move.STAY)
			{
				state = State.SPAWN;
			}
			break;

		case SPAWN:
			move = Move.SPAWN;
			state = State.RUN;
			break;

		case RUN:
			move = searchSafeSpot(me, board);
			if (move == Move.STAY)
			{
				state = State.SEARCH;
			}
			break;
		}

		return move;
	}

	class NextToCloud implements Predicate<Node>
	{
		private IBoard board;

		public NextToCloud(IBoard board)
		{
			this.board = board;
		}

		@Override
		public boolean test(Node currentNode)
		{
			V currentUnicornPos = (V) currentNode.getState();
			for (V d : Board.getDirections())
			{
				for (GameObject obj : board.at(V.add(currentUnicornPos, d)))
				{
					if (obj instanceof Cloud)
					{
						return true;
					}
				}
			}

			return false;
		}
	}

	private Move searchNextCloud(Unicorn me, IBoard board)
	{
		BFS bfs = new BFS();
		Node end = bfs.search(new OnlyPositionNode(board, me.pos), new NextToCloud(board));
		List<Node> path = PathUtils.getPath(end);
		
		if (path.size() <= 1)
			return Move.STAY;

		V nextPos = (V) path.get(1).getState();
		V direction = V.sub(nextPos, me.pos);
		return Board.getDirectionToMoveMapping().get(direction);
	}

	class SafeFromSeed implements Predicate<Node>
	{
		private IBoard board;

		public SafeFromSeed(IBoard board)
		{
			this.board = board;
		}

		@Override
		public boolean test(Node node)
		{
			V pos = (V) node.getState();
			// for all seeds
			for (Seed s : board.getSeeds())
			{
				// are we on the same (axis parallel) line ?
				if (V.sameLine(pos, s.pos))
				{
					// are we far enough from it ?
					if (V.manhattan(pos, s.pos) < s.range)
					{
						return false;
					}
				}
			}
			return true;
		}
	}

	private Move searchSafeSpot(Unicorn me, IBoard board)
	{
		BFS bfs = new BFS();
		Node end = bfs.search(new OnlyPositionNode(board, me.pos), new SafeFromSeed(board));
		List<Node> path = PathUtils.getPath(end);

		if (path.size() <= 1)
			return Move.STAY;

		V nextPos = (V) path.get(1).getState();
		V direction = V.sub(nextPos, me.pos);
		return Board.getDirectionToMoveMapping().get(direction);
	}

	class NearestFountain implements Predicate<Node>
	{
		private Unicorn me;
		private IBoard board;

		public NearestFountain(Unicorn me, IBoard board)
		{
			this.me = me;
			this.board = board;
		}

		@Override
		public boolean test(Node node)
		{
			V pos = (V) node.getState();
			for (GameObject go : board.at(pos))
			{
				if (go instanceof Fountain)
				{
					if (((Fountain) go).lastVisitedBy != me.id)
						return true;
				}
			}

			return false;
		}
	}
}
