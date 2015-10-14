package at.jku.cp.ai.rau.functions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.Node;

public class LevelCost implements Function<Node, Double> {

	int[][] costs;

	public LevelCost(List<String> _costs) {
		int width = _costs.get(0).length();
		int height = _costs.size();
		costs = new int[width][height];

		assert costs[0].length == height;
		assert costs.length == width;

		int y = 0;
		for (String line : _costs) {
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				if (Character.isDigit(c))
					costs[x][y] = Character.digit(c, 10);
				else
					costs[x][y] = 0;
			}

			y++;
		}
	}

	@Override
	public Double apply(Node node) {
		IBoard board = (IBoard) node.getState();
		if (board.isRunning()) {
			V playerPosition = board.getCurrentUnicorn().pos;
			return (double) costs[playerPosition.x][playerPosition.y];
		}
		else
		{
			return Double.POSITIVE_INFINITY;
		}
	}

	public static LevelCost fromFile(String filename) {
		try {
			return new LevelCost(Files.readAllLines(Paths.get(filename)));
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
}
