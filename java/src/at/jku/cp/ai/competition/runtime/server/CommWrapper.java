package at.jku.cp.ai.competition.runtime.server;

import java.io.Serializable;

import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.rau.IBoard;

public class CommWrapper implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final CommWrapper CW_END = new CommWrapper(null, null);
	public PlayerInfo info;
	public IBoard board;
	
	public CommWrapper(PlayerInfo info, IBoard board)
	{
		this.info = info;
		this.board = board;
	}
}
