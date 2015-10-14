package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Unicorn extends GameObject
{
	private static final long serialVersionUID = 1L;
	public static final int MAX_SEEDS = 3;
	
	public int id;
	public int seeds;
	
	public Unicorn(V pos, int id, int seeds)
	{
		this.pos = pos;
		this.id = id;
		this.rep = Character.forDigit(this.id, 10);
		this.isPassable = true;
		this.isRemovable = true;
		this.stopsRainbow = false;
		this.seeds = seeds;
	}

	public Unicorn(V pos, int id)
	{
		this(pos, id, MAX_SEEDS);
	}
	
	public Unicorn(Unicorn u)
	{
		this(new V(u.pos), u.id, u.seeds);
	}

	@Override
	public String toString()
	{
		return String.format("u(%s, %d, %d)", pos, id, seeds);
	}

	public static Unicorn fromString(String s)
	{
		Pattern p = Pattern.compile("u\\(v\\((\\d+),\\ (\\d+)\\),\\ (\\d+),\\ (\\d+)\\)");
		Matcher m = p.matcher(s);
		if (m.matches())
		{
			return new Unicorn(new V(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
					Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
		}
		throw new RuntimeException("invalid unicorn rep!");
	}

}
