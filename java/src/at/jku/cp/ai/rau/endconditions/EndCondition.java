package at.jku.cp.ai.rau.endconditions;

import java.util.List;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Unicorn;

public interface EndCondition
{
	/**
	 * given a board, and the outcome of the actions in the current tick,
	 * determine whether the game has ended
	 * @param board
	 * @param evaporated
	 * @param sailing
	 * @return the state of the game
	 */
	public boolean hasEnded(IBoard board, List<Cloud> evaporated, List<Unicorn> sailing);
	
	/**
	 * 
	 * @return -1 if draw, id of winning unicorn otherwise
	 */
	public int getWinner();
	
	public boolean equals(Object obj);
	
	public int hashCode();

	public EndCondition copy();

	public String getOutcome();
}
