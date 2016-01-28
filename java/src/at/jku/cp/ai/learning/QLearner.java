package at.jku.cp.ai.learning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.search.datastructures.Pair;

public class QLearner
{
	public Map<Pair<IBoard, Move>, Double> qmatrix;

	private int numEpisodes;

	private Random random;

	private double discountFactor;

	private boolean verbose;

	private boolean bonus;
	/** Contains position of the cloud */
	private V cPos = null;
	/** Contains position of spawned seed */
	private V sPos = null;

	/** reward for a good move */
	private static double REWARD_GOOD_MOVE = 100d;
	/** reward for a normal move */
	private static double REWARD_NORMAL_MOVE = 0d;

	public QLearner(Random random, int numEpisodes, double discountFactor, boolean verbose) {
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
	public QLearner(Random random, int numEpisodes, double discountFactor, boolean verbose, boolean bonus) {
		if (discountFactor < 0d) {
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
	public void learnQFunction(IBoard board) {
		// get position of cloud, there is only only cloud per board
		cPos = board.getClouds().get(0).pos;
		for (int e = 0; e < numEpisodes; e++) {
			// start each episode with a fresh board
			IBoard learnBoard = board.copy();
			// reset seed position
			sPos = null;
			// learn as long as goal not reached and the unicorn is still on the board
			while (!goalReached(learnBoard) && !wentSailing(learnBoard)) {
				Move next = chooseMove(learnBoard);
				IBoard prevBoard = learnBoard.copy();
				learnBoard.executeMove(next);
				// do not update qmatrix if unicorn went sailing
				// because getPossibleMoves Method throws exception if no
				// unicorn is on board
				if (!wentSailing(learnBoard)) {
					updateQMatrix(prevBoard, learnBoard, next);
				}
			}
		}
	}

	/**
	 * Checks if unicorn is still on board
	 * @param board
	 * @return
	 */
	private boolean wentSailing(IBoard board) {
		return board.getUnicorns().size() == 0;
	}

	/**
	 * Updates qmatrix
	 * @param prevBoard
	 * @param newBoard
	 * @param move
	 */
	private void updateQMatrix(IBoard prevBoard, IBoard newBoard, Move move) {
		double reward = getReward(prevBoard, move);
		qmatrix.put(new Pair<>(prevBoard, move), reward + discountFactor * getMaxScore(newBoard));
	}

	/**
	 * Returns reward for given state and action
	 * @param board
	 * @param move
	 * @return
	 */
	private double getReward(IBoard board, Move move) {
		if (move == Move.SPAWN && nextToCloud(board)) {
			// spawned seed next to cloud is a good thing -> good move
			sPos = board.getUnicorns().get(0).pos;
			return REWARD_GOOD_MOVE;
		} else if (sPos != null) {
			V uPos = board.getUnicorns().get(0).pos;
			// seed is spawned, if unicorn is save from rainbow it is a good move
			if ((sPos.x != uPos.x && sPos.y != uPos.y) ||	/* not in the same row/column -> save */
					Math.abs(sPos.x - uPos.x) > 3 ||
					Math.abs(sPos.y - uPos.y) > 3) { /* at least 3 tiles away from seed -> save */
				// went away from seed -> good move
				return REWARD_GOOD_MOVE;
			}
		}
		return REWARD_NORMAL_MOVE;
	}

	/**
	 * Checks if unicorn is next to cloud
	 * @param board
	 * @return
	 */
	private boolean nextToCloud(IBoard board) {
		V uPos = board.getUnicorns().get(0).pos;
		return (cPos.x == uPos.x && (cPos.y - 1 == uPos.y || cPos.y + 1 == uPos.y)) ||
				(cPos.y == uPos.y && (cPos.x - 1 == uPos.x || cPos.x + 1 == uPos.x));
	}

	/**
	 * Choose next move for learning
	 * @param board
	 * @return
	 */
	private Move chooseMove(IBoard board) {
		List<Move> moves = board.getPossibleMoves();
		// choose randomly from the possible moves
		return moves.get(random.nextInt(moves.size()));
	}

	/**
	 * Checks if end condition is reached
	 * @param board
	 * @return
	 */
	private boolean goalReached(IBoard board) {
		return board.getEndCondition().getWinner() == 0;
	}

	/**
	 * Returns current max score for a given state
	 * @param board
	 * @return
	 */
	public double getMaxScore(IBoard board) {
		List<Move> moves = board.getPossibleMoves();
		double bestScore = 0;
		for (Move move : moves) {
			double score = qmatrix.getOrDefault(new Pair<>(board, move), 0d);
			if (score > bestScore) {
				bestScore = score;
			}
		}
		return bestScore;
	}

	/**
	 * this method uses the (learned) qmatrix to determine best move, given the current
	 * situation!
	 * @param board
	 * @return
	 */
	public Move getMove(IBoard board) {
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