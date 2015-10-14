package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fountain extends GameObject
{
	private static final long serialVersionUID = 1L;

	public final static int LAST_VISITED_BY_DEFAULT = -1;
	public int lastVisitedBy;
	
	public Fountain(V pos, int lastTouchedBy)
	{
		this.pos = pos;
		this.rep = 'f';
		this.isPassable = true;
		this.isRemovable = false;
		this.stopsRainbow = false;
		this.lastVisitedBy = lastTouchedBy;
	}

	public Fountain(V pos)
	{
		this(pos, LAST_VISITED_BY_DEFAULT);
	}
	
	public Fountain(Fountain m)
	{
		this(new V(m.pos), m.lastVisitedBy);
	}

	@Override
	public String toString()
	{
		return String.format("f(%s, %d)", pos, lastVisitedBy);
	}

	public static Fountain fromString(String s)
	{
		Pattern p = Pattern.compile("f\\(v\\((\\d+),\\ (\\d+)\\), (-\\d+|\\d+)\\)");
		Matcher m = p.matcher(s);
		if (m.matches())
		{
			return new Fountain(
					new V(
						Integer.parseInt(m.group(1)),
						Integer.parseInt(m.group(2))),
						Integer.parseInt(m.group(3)));
		}
		throw new RuntimeException("invalid fountain rep!");
	}
}
