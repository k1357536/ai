package at.jku.cp.ai.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;

public class PathUtils
{

	public static List<Node> getPath(Node endNode)
	{
		if (null == endNode)
			return Collections.emptyList();

		List<Node> path = new ArrayList<>();

		Node current = endNode;
		while (!current.isRoot())
		{
			path.add(current);
			current = current.parent();
		}

		path.add(current);

		Collections.reverse(path);

		return path;
	}

	@SuppressWarnings("unchecked")
	public static <A> List<A> getStates(List<Node> nodes)
	{
		List<A> path = new ArrayList<A>();

		for (Node node : nodes)
			path.add((A) node.getState());

		return path;
	}

	@SuppressWarnings("unchecked")
	public static <A> List<A> getActions(List<Node> nodes)
	{
		List<A> actions = new ArrayList<>();

		if (nodes.size() > 0)
		{
			for (Node node : nodes.subList(1, nodes.size()))
				actions.add((A) node.getAction());
		}
		return actions;
	}

	public static double getPathCost(IBoard board, List<Move> moves, Function<Node, Double> f)
	{
		double cumulative = 0.0;
		for (Move move : moves)
		{
			Node node = new IBoardNode(board);
			cumulative += f.apply(node);
			board.executeMove(move);
		}
		return cumulative;
	}

	public static void comparePathCost(IBoard board, List<Move> expectedMoves, List<Move> actualMoves, Function<Node, Double> heuristic, Function<Node, Double> cost)
	{
		int nMoves = Math.min(actualMoves.size(), expectedMoves.size());
		double actualCost = 0.0;
		double expectedCost = 0.0;

		IBoard actualBoard = board.copy();
		IBoard expectedBoard = board.copy();

		for (int i = 0; i < nMoves; i++)
		{
			Node actualNode = new IBoardNode(actualBoard);
			Node expectedNode = new IBoardNode(expectedBoard);
			actualCost = heuristic.apply(actualNode) + cost.apply(actualNode);
			expectedCost = heuristic.apply(expectedNode) + cost.apply(expectedNode);

			Move actualMove = actualMoves.get(i);
			actualBoard.executeMove(actualMove);

			Move expectedMove = expectedMoves.get(i);
			expectedBoard.executeMove(expectedMove);

			System.out.println("---------------------");
			System.out.println(String.format("exmove %5s excost %.1f acmove %5s accost %.1f", expectedMove, expectedCost, actualMove, actualCost));
			System.out.println(RenderUtils.column(heuristic, cost, expectedBoard, actualBoard));

		}
	}

	public static List<V> asList(int... coords)
	{
		List<V> lst = new ArrayList<>();

		if (coords.length % 2 == 1)
		{
			throw new RuntimeException("invalid length! must be a multiple of 2!");
		}

		for (int i = 0; i < coords.length; i += 2)
		{
			lst.add(new V(coords[i], coords[i + 1]));
		}

		return lst;
	}

	public static List<IBoard> movesToIBoards(List<Move> path, IBoard board)
	{
		List<IBoard> nodes = new ArrayList<>();

		IBoard start = board;
		if (path.size() > 0)
		{
			nodes.add(start);

			IBoard current = board.copy();

			for (Move move : path)
			{
				if (!current.executeMove(move))
				{
					throw new RuntimeException("move list is bogus!");
				}
				nodes.add(current.copy());
			}
		}

		return nodes;
	}

	public static List<Move> vsToMoves(List<V> path)
	{
		List<Move> moves = new ArrayList<>();
		for (int i = 1; i < path.size(); i++)
		{
			V previous = path.get(i - 1);
			V current = path.get(i);

			V d = V.sub(current, previous);
			if (Board.getDirectionToMoveMapping().containsKey(d))
			{
				moves.add(Board.getDirectionToMoveMapping().get(d));
			}
			else
			{
				throw new RuntimeException("the path is broken!");
			}
		}
		return moves;
	}

	public static List<V> fromFile(String filename)
	{
		try
		{
			List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
			List<V> path = new ArrayList<>();
			for (String line : lines)
				path.add(V.fromString(line));
			return path;
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void toFile(String filename, List<V> path)
	{
		try
		{
			if (Files.exists(Paths.get(filename)))
				Files.delete(Paths.get(filename));
			List<String> lines = new ArrayList<>();
			for (V v : path)
				lines.add(v.toString());

			Files.write(Paths.get(filename), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
