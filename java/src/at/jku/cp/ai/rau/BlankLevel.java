package at.jku.cp.ai.rau;

import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.rau.objects.Wall;

public class BlankLevel extends Board
{
	private static final long serialVersionUID = 1L;

	public BlankLevel(int width, int height)
	{
		super();

		this.width = width;
		this.height = height;
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (x == 0
						|| y == 0
						|| x == width - 1
						|| y == height - 1)
				{
					walls.add(new Wall(new V(x, y)));
				} else
				{
					paths.add(new Path(new V(x, y)));
				}
			}
		}
	}
}
