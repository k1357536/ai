package at.jku.cp.ai.rau.endconditions;

import java.util.List;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Unicorn;

final public class NoEnd implements EndCondition
{
	@Override
	public boolean hasEnded(IBoard board, List<Cloud> evaporated, List<Unicorn> sailing)
	{
		return false;
	}

	@Override
	public int getWinner()
	{
		return -1;
	}
	
	@Override
	public EndCondition copy()
	{
		return (EndCondition) this;
	}
	
	@Override
	public String getOutcome()
	{
		return "NOEND";
	}
}
