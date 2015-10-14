package at.jku.cp.ai.rau.objects;

import java.io.Serializable;

public abstract class GameObject implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public V pos;
	public boolean isRemovable;
	public boolean isPassable;
	public boolean stopsRainbow;
	public char rep;

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (isPassable ? 1231 : 1237);
		result = prime * result + (isRemovable ? 1231 : 1237);
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + rep;
		result = prime * result + (stopsRainbow ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameObject other = (GameObject) obj;
		if (isPassable != other.isPassable)
			return false;
		if (isRemovable != other.isRemovable)
			return false;
		if (pos == null)
		{
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (rep != other.rep)
			return false;
		if (stopsRainbow != other.stopsRainbow)
			return false;
		return true;
	}
}
