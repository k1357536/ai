package at.jku.cp.ai.competition.players;

import java.io.Serializable;
import java.util.Random;

public class PlayerInfo implements Serializable
{

	private static final long serialVersionUID = 1L;
	public int remainingMoves;
	public long remainingTime;
	public int unicorn_id;
	public int opponent_id;
	public Random random;
	
	public PlayerInfo(long remainingTime, int remainingMoves, int unicorn_id, int opponent_id, Random random)
	{
		this.remainingMoves = remainingMoves;
		this.remainingTime = remainingTime;
		this.unicorn_id = unicorn_id;
		this.opponent_id = opponent_id;
		this.random = random;
	}
	
	public PlayerInfo(PlayerInfo other)
	{
		this.remainingMoves = other.remainingMoves;
		this.remainingTime = other.remainingTime;
		this.unicorn_id = other.unicorn_id;
		this.opponent_id = other.opponent_id;
		this.random = other.random;
	}

	@Override
	public String toString()
	{
		return "PlayerInfo [remainingMoves=" + remainingMoves + 
				", remainingTime=" + remainingTime +
				", unicorn_id=" + unicorn_id +
				", opponent_id=" + opponent_id +
				", random=" + random + "]";
	}
}
