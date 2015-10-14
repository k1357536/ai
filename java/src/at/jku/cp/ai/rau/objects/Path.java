package at.jku.cp.ai.rau.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path extends GameObject {
	
	private static final long serialVersionUID = 1L;

	public Path(V pos)
	{
		this.pos = pos;
		this.rep = '.';
		this.isPassable = true;
		this.isRemovable = false;
		this.stopsRainbow = false;
	}
	
	public Path(Path path)
	{
		this(new V(path.pos));
	}
	
	@Override
	public String toString()
	{
		return String.format("p(%s)", pos);
	}
	
	public static Path fromString(String s)
	{
		Pattern p = Pattern.compile("p\\(v\\((\\d+),\\ (\\d+)\\)\\)");
		Matcher m = p.matcher(s);
		if (m.matches())
		{
			return new Path(new V(Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))));
		}
		throw new RuntimeException("invalid path rep!");
	}
}
