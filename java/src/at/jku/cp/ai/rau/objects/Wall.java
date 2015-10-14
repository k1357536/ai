package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wall extends GameObject {
	private static final long serialVersionUID = 1L;

	public Wall(V pos)
	{
		this.pos = pos;
		this.rep = '#';
		this.isPassable = false;
		this.isRemovable = false;
		this.stopsRainbow = true;
	}
	
	public Wall(Wall wall)
	{
		this(new V(wall.pos));
	}
	
	@Override
	public String toString()
	{
		return String.format("w(%s)", pos);
	}

	public static Wall fromString(String s)
	{
		Pattern p = Pattern.compile("w\\(v\\((\\d+),\\ (\\d+)\\)\\)");
		Matcher m = p.matcher(s);
		if (m.matches())
		{
			return new Wall(new V(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))));
		}
		throw new RuntimeException("invalid wall rep!");
	}
}
