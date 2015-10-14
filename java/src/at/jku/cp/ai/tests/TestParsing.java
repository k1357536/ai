package at.jku.cp.ai.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.Rainbow;
import at.jku.cp.ai.rau.objects.Seed;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.rau.objects.Wall;

public class TestParsing
{
	@Test
	public void roundtripV()
	{
		V v = new V(2348, 12098);
		assertEquals(v, V.fromString(v.toString()));
	}
	
	@Test
	public void roundtripCloud()
	{
		Cloud c = new Cloud(new V(334098, 11029));
		assertEquals(c, Cloud.fromString(c.toString()));
	}
	
	@Test
	public void roundtripRainbow()
	{
		Rainbow r = new Rainbow(new V(23238, 0), 12);
		assertEquals(r, Rainbow.fromString(r.toString()));
	}
	
	@Test
	public void roundtripSeed()
	{
		Seed s = new Seed(new V(3094, 1230), 293838, 1, 1);
		assertEquals(s, Seed.fromString(s.toString()));
	}
	
	@Test
	public void roundtripUnicorn()
	{
		Unicorn u = new Unicorn(new V(3908 , 9094), 9);
		assertEquals(u, Unicorn.fromString(u.toString()));
	}
	
	@Test
	public void roundtripWall()
	{
		Wall w = new Wall(new V(30294, 98098));
		assertEquals(w, Wall.fromString(w.toString()));
	}
	
	@Test
	public void roundtripPath()
	{
		Path p = new Path(new V(30294, 9898));
		assertEquals(p, Path.fromString(p.toString()));
	}
}
