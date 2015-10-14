package at.jku.cp.ai.tests.runtime;

import at.jku.cp.ai.competition.players.Player;
import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;

public class SpaceExceedingPlayer implements Player
{
	@Override
	public Move getNextMove(PlayerInfo mappings, IBoard board)
	{
		byte[] tooMuch = new byte[513 * 1024 * 1024];
		
		for(int i = 0; i < tooMuch.length; i++)
		{
			tooMuch[i] = (byte) (i % 256);
		}
		
		return Move.STAY;
	}

}
