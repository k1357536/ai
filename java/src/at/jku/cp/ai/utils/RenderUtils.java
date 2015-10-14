package at.jku.cp.ai.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.imageio.ImageIO;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.GameObject;
import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.Rainbow;
import at.jku.cp.ai.rau.objects.Seed;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.rau.objects.Wall;
import at.jku.cp.ai.search.Node;

public class RenderUtils {
	// render several boards next to each other
	public static String column(IBoard... boards)
	{
		StringBuilder sb = new StringBuilder();

		int maxH = 0;
		for (IBoard board : boards)
		{
			maxH = Math.max(maxH, board.getHeight());
		}

		for (int y = 0; y < maxH; y++)
		{
			for (IBoard board : boards)
			{
				char[][] rep = board.getTextBoard();

				if (board.getHeight() <= maxH)
				{
					for (int x = 0; x < board.getWidth(); x++)
					{
						sb.append(rep[x][y]);
						sb.append(' ');
					}
				}
				else
				{
					for (int x = 0; x < board.getWidth(); x++)
					{
						sb.append("  ");
					}
				}
				sb.append(' ');
			}
			sb.append('\n');
		}

		return sb.toString();
	}

	// render several boards next to each other
	public static String column(Function<Node, Double> heuristic, Function<Node, Double> cost, IBoard... boards)
	{
		StringBuilder sb = new StringBuilder();

		int maxH = 0;
		for (IBoard board : boards)
		{
			maxH = Math.max(maxH, board.getHeight());
		}

		for (int y = 0; y < maxH; y++)
		{
			for (IBoard board : boards)
			{
				Map<V, String> costs = new HashMap<>();
				Node current = new IBoardNode(board);
				List<Node> adjacent = current.adjacent();
				for(Node next: adjacent)
				{
					IBoard nextBoard = (IBoard) next.getState();
					V pos = nextBoard.getCurrentUnicorn().pos;
					int realCost = (int) ((double) heuristic.apply(next) + (double) cost.apply(next));
					costs.put(pos, Integer.toString(realCost));
				}
				
				char[][] rep = board.getTextBoard();

				if (board.getHeight() <= maxH)
				{
					for (int x = 0; x < board.getWidth(); x++)
					{
						String cell = "";
						
						if(costs.containsKey(new V(x, y)))
						{
							cell = costs.get(new V(x, y));
						}
						else
						{
							cell = Character.toString(rep[x][y]);
						}
						
						sb.append(String.format("%2s", cell));
					}
				}
				else
				{
					for (int x = 0; x < board.getWidth(); x++)
					{
						sb.append("  ");
					}
				}
				sb.append(' ');
			}
			sb.append('\n');
		}

		return sb.toString();
	}

	public static String asString(char[][] rep)
	{
		if (rep.length == 0)
		{
			return "completely empty board";
		}
	
		StringBuilder sb = new StringBuilder();
	
		final String separator = System.getProperty("line.separator");
		sb.append(separator);
		for (int x = 0; x < rep.length; x++)
		{
			sb.append(String.format("%-2d", x));
		}
		sb.append(separator);
	
		for (int y = 0; y < rep[0].length; y++)
		{
			for (int x = 0; x < rep.length - 1; x++)
			{
				sb.append(rep[x][y]);
				sb.append(" ");
			}
			sb.append(rep[rep.length - 1][y]);
			sb.append(" ");
			sb.append(y);
			sb.append(separator);
		}
		return sb.toString();
	}

	public static String asStringNoInfo(char[][] rep)
	{
		if (rep.length == 0)
		{
			return "completely empty board";
		}
	
		StringBuilder sb = new StringBuilder();
	
		final String separator = System.getProperty("line.separator");
		for (int y = 0; y < rep[0].length; y++)
		{
			for (int x = 0; x < rep.length; x++)
			{
				sb.append(rep[x][y]);
			}
			sb.append(separator);
		}
		return sb.toString();
	}

	public static String visualizePath(IBoard board, List<Node> edges)
	{
		char[][] canvas = board.getTextBoard();
		for (Node node : edges)
		{
			V pos = ((IBoard) node.getState()).getCurrentUnicorn().pos;
			canvas[pos.x][pos.y] = '@';
		}
	
		return asString(canvas);
	}

	public final static int hexblue = 0x19aeff;
	public final static int hexred = 0xff4141;
	public final static Color blue = new Color(hexblue);
	public final static Color red = new Color(hexred);
	public final static Color purple = new Color(0xba00ff);
	public final static Color lightpurple = new Color(0xd76cff);
	public final static Color darkgray = new Color(0x2d2d2d);
	public final static Color medgray = new Color(0x666666);
	public final static Color lightgray = new Color(0xcccccc);
	public final static Color white = new Color(0xffffff);
	public final static Color yellow = new Color(0xffff3e);
	public static void draw(Graphics2D gfx, GameObject g, int w)
	{
		if (g instanceof Wall)
		{
			gfx.setColor(darkgray);
		}
		else if (g instanceof Path)
		{
			gfx.setColor(medgray);
		}
		else if (g instanceof Cloud)
		{
			gfx.setColor(lightgray);
		}
		else if (g instanceof Seed)
		{
			gfx.setColor(purple);
		}
		else if (g instanceof Rainbow)
		{
			gfx.setColor(lightpurple);
		}
		else if (g instanceof Unicorn)
		{
			if (((Unicorn) g).id == 0)
			{
				gfx.setColor(blue);
			}
			else
			{
				gfx.setColor(red);
			}
		}
		else if (g instanceof Fountain)
		{
			gfx.setColor(yellow);
		}
		else
		{
			throw new RuntimeException("unknown?");
		}
	
		gfx.setBackground(gfx.getColor());
	
		if (g instanceof Fountain)
		{
			if (((Fountain) g).lastVisitedBy == 0)
			{
				gfx.setBackground(blue);
			}
			else if (((Fountain) g).lastVisitedBy == 1)
			{
				gfx.setBackground(red);
			}
			else
			{
				gfx.setBackground(yellow);
			}
		}
	
		gfx.clearRect(g.pos.x * w, g.pos.y * w, w, w);
	
		gfx.drawRect(g.pos.x * w, g.pos.y * w, w, w);
		gfx.drawRect(g.pos.x * w + 1, g.pos.y * w + 1, w - 2, w - 2);
	}
	
	public static void writeBoardAsPNG(String filename, IBoard board)
	{
		int w = 11;
		try
		{
			BufferedImage image = new BufferedImage(
					board.getWidth() * w,
					board.getHeight() * w,
					BufferedImage.TYPE_INT_RGB);

			Graphics2D gfx = image.createGraphics();
			for (List<? extends GameObject> objs : board.getAllObjects())
			{
				for (GameObject g : objs)
				{
					draw(gfx, g, w);
				}
			}

			ImageIO.write(image, "png", new File(filename));
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
