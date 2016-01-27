package at.jku.cp.ai.learning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.datastructures.Pair;

public class QLearner
{
	public Map<Pair<IBoard, Move>, Double> qmatrix;
	@SuppressWarnings("unused")
	private int numEpisodes;
	@SuppressWarnings("unused")
	private Random random;
	@SuppressWarnings("unused")
	private double discountFactor;
	@SuppressWarnings("unused")
	private boolean verbose;
	@SuppressWarnings("unused")
	private boolean bonus;

	public QLearner(Random random, int numEpisodes, double discountFactor, boolean verbose)
	{
		this(random, numEpisodes, discountFactor, verbose, false);
	}

	/**
	 * 
	 * @param random
	 *            the random number generator to be used
	 * @param numEpisodes
	 *            the number of episodes for learning the model
	 * @param discountFactor
	 *            this determines the importance of future rewards; in our test
	 *            cases this plays a very minor role.
	 */
	public QLearner(Random random, int numEpisodes, double discountFactor, boolean verbose, boolean bonus)
	{
		if (discountFactor < 0d)
		{
			throw new RuntimeException("discountFactor must be greater than 0.0!");
		}

		this.random = random;
		this.numEpisodes = numEpisodes;
		this.discountFactor = discountFactor;
		this.verbose = verbose;
		this.qmatrix = new HashMap<>();
		this.bonus = bonus;
	}

	/**
	 * This is the method that has to learn the qmatrix, which will be used later on!
	 * @param board
	 */
	public void learnQFunction(IBoard board)
	{
		// HINT: adapt algorithm for q-learning from the lecture slides
		// in the following way:
		//
		// for e in (0 .. number_of_episodes):
		//     while not (goal reached):
		//         ...
	}

	/**
	 * this method uses the (learned) qmatrix to determine best move, given the current
	 * situation!
	 * @param board
	 * @return
	 */
	public Move getMove(IBoard board)
	{
		List<Move> moves = board.getPossibleMoves();

		double bestScore = Double.NEGATIVE_INFINITY;
		Move bestMove = Move.STAY;

		for (Move move : moves)
		{
			double score = qmatrix.getOrDefault(new Pair<>(board, move), 0d);
			if (score > bestScore)
			{
				bestScore = score;
				bestMove = move;
			}
		}

		return bestMove;
	}
}