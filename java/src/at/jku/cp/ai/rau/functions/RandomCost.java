package at.jku.cp.ai.rau.functions;

import java.util.Random;
import java.util.function.Function;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.utils.RenderUtils;

public class RandomCost implements Function<Node, Double>
{
	private int[][] costs;

	public RandomCost(int width, int height, Random random, int upperBound)
	{
		costs = new int[width][height];
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				costs[x][y] = 1 + random.nextInt(upperBound - 1);
			}
		}
	}

	@Override
	public Double apply(Node node)
	{
		IBoard board = (IBoard) node.getState();
		V pos = board.getCurrentUnicorn().pos;
		return (double) costs[pos.x][pos.y];
	}
	
	public String render(IBoard board)
	{
		char[][] rep = board.getTextBoard();
		for(Path p: board.getPaths())
		{
			rep[p.pos.x][p.pos.y] = Character.forDigit(costs[p.pos.x][p.pos.y], 10);
		}
		return RenderUtils.asStringNoInfo(rep);
	}
}
