package at.jku.cp.ai.competition.players;

import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;

public interface Player
{
	public Move getNextMove(PlayerInfo mappings, IBoard board);
}
