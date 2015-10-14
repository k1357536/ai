package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Cloud extends GameObject
{
	private static final long serialVersionUID = 1L;

	public Cloud(V pos)
	{
		this.pos = pos;
		this.rep = 'c';
		this.isPassable = false;
		this.isRemovable = true;
		this.stopsRainbow = true;
	}

	public Cloud(Cloud c)
	{
		this(new V(c.pos));
	}

	@Override
	public String toString()
	{
		return String.format("c(%s)", pos);
	}

	public static Cloud fromString(String s)
	{
		Pattern p = Pattern.compile("c\\(v\\((\\d+),\\ (\\d+)\\)\\)");
		Matcher m = p.matcher(s);
		if (m.matches())
		{
			return new Cloud(new V(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))));
		}
		throw new RuntimeException("invalid cloud rep!");
	}

}
