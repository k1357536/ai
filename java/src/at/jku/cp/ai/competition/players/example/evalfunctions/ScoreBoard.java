package at.jku.cp.ai.competition.players.example.evalfunctions;

import java.util.function.Function;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.endconditions.EndCondition;
import at.jku.cp.ai.rau.endconditions.PointCollecting;

/**
 * This is how a scoring function could look like. It's not a very sophisticated
 * scoring function, but it shows a few basic principles. Remember that this
 * function will only get called for leaf nodes:
 * 
 * - if the search encounters a node in which the game is over, this function
 * gets called.
 * 
 * - if the search reaches its depth-limit, this function gets
 * called!
 */
public class ScoreBoard implements Function<IBoard, Double>
{
	private int unicorn_id;
	private int opponent_id;
	private IBoard start;

	/**
	 * This particular scoring function will get constructed each time a search is run.
	 * It gets passed the starting state of the board, so it can judge wether a sequence
	 * of moves during the search has led to a better state!
	 * @param start
	 * @param unicorn_id
	 * @param opponent_id
	 */
	public ScoreBoard(IBoard start, int unicorn_id, int opponent_id)
	{
		this.start = start;
		this.unicorn_id = unicorn_id;
		this.opponent_id = opponent_id;
	}

	@Override
	public Double apply(IBoard board)
	{
		// nobody won or lost so far
		if (board.isRunning())
		{
			double score = 0d;

			score += scoreFountains(board);
			score += scoreClouds(board);

			return score;
		} else
		{
			EndCondition ec = board.getEndCondition();
			if (ec.getWinner() == unicorn_id)
			{
				return 10000d; // our bot won! (yay!)
			}
			else if (ec.getWinner() == opponent_id)
			{
				return -10000d; // our bot lost! (we don't want that ...)
			}
			else
			{
				return 0d; // it's a draw (boooooring!)
			}
		}
	}

	/**
	 * did we remove any clouds?
	 * 
	 * @param board
	 * @return difference in clouds, counted from the number of clouds from the
	 *         board at the start
	 */
	private double scoreClouds(IBoard board)
	{
		return (start.getClouds().size() - board.getClouds().size()) * 10d;
	}

	/**
	 * do we have more points than the opponent?
	 * 
	 * @param board
	 * @return difference in points
	 */
	private double scoreFountains(IBoard board)
	{
		PointCollecting pc = (PointCollecting) board.getEndCondition();
		return pc.getScore(unicorn_id) - pc.getScore(opponent_id);
	}

}
