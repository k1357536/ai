package at.jku.cp.ai.rau.endconditions;

import java.util.List;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Unicorn;

final public class CloudEvaporator implements EndCondition
{
	private int winner;
	
	public CloudEvaporator()
	{
		winner = -1;
	}
	
	public CloudEvaporator(CloudEvaporator other)
	{
		this.winner = other.winner;
	}
	
	@Override
	public boolean hasEnded(IBoard board, List<Cloud> evaporated, List<Unicorn> sailing)
	{
		if(sailing.size() > 0)
		{
			winner = -1;
			return true;
		}
		
		if(evaporated.size() > 0)
		{
			winner = 0;
			return true;
		}
		
		return false;
	}

	@Override
	public int getWinner()
	{
		return winner;
	}
	
	@Override
	public EndCondition copy()
	{
		return (EndCondition) new CloudEvaporator(this);
	}
	
	@Override
	public String getOutcome()
	{
		return "CLOUDEVAPORATOR";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + winner;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CloudEvaporator other = (CloudEvaporator) obj;
		if (winner != other.winner)
			return false;
		return true;
	}
}
