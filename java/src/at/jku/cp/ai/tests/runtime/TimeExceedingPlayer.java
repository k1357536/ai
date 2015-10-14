package at.jku.cp.ai.tests.runtime;

import at.jku.cp.ai.competition.players.Player;
import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;

public class TimeExceedingPlayer implements Player
{
	@Override
	public Move getNextMove(PlayerInfo mappings, IBoard board)
	{
		try
		{
			Thread.sleep(2 * 1000L);
		} catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		
		return Move.STAY;
	}

}
