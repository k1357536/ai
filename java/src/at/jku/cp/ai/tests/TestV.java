package at.jku.cp.ai.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import at.jku.cp.ai.rau.objects.V;

public class TestV
{

	@Test
	public void equalityOfV()
	{
		assertEquals(new V(10932, 138), new V(10932, 138));
		assertNotEquals(new V(0, 1), new V(1, 0));
		assertEquals(new V(0, 0), new V(0, 0));
		assertEquals(new V(1, 3), new V(1, 3));
	}

	@Test
	public void emulOfV()
	{
		V v1 = new V(0, 1);
		int b = 0;

		V result = V.emul(v1, b);
		assertEquals(new V(0, 0), result);

		v1 = new V(-1, 1);
		b = -1;

		result = V.emul(v1, b);
		assertEquals(new V(1, -1), result);
	}

	@Test
	public void equalsOfV()
	{
		V v1 = new V(0, 1);
		V v2 = new V(0, 1);

		assertFalse(v1.equals(null));

		assertFalse(v1.equals(new Object()));

		assertTrue(v1.equals(v1));
		assertTrue(v1.equals(v2));
	}

	@Test
	public void manhattenOfV()
	{
		V v1 = new V(0, 0);
		V v2 = new V(0, 1);
		V v3 = new V(1, 1);
		V v4 = new V(1, 0);

		assertEquals(0, V.manhattan(v1, v1));
		assertEquals(0, V.manhattan(v2, v2));
		assertEquals(0, V.manhattan(v3, v3));
		assertEquals(0, V.manhattan(v4, v4));

		assertEquals(1, V.manhattan(v1, v2));
		assertEquals(1, V.manhattan(v2, v3));
		assertEquals(1, V.manhattan(v3, v4));
		assertEquals(1, V.manhattan(v4, v1));

		assertEquals(1, V.manhattan(v1, v4));
		assertEquals(1, V.manhattan(v4, v3));
		assertEquals(1, V.manhattan(v3, v2));
		assertEquals(1, V.manhattan(v2, v1));

		assertEquals(2, V.manhattan(v1, v3));
		assertEquals(2, V.manhattan(v2, v4));

		assertEquals(2, V.manhattan(v3, v1));
		assertEquals(2, V.manhattan(v4, v2));
	}

	@Test
	public void sameLine()
	{
		Random random = new Random(23L);

		for (int i = 0; i < 100; i++)
		{
			int a = random.nextInt();
			int b = random.nextInt();

			V origin = new V(a, b);
			V v1 = new V(a + random.nextInt(), b);
			V v2 = new V(a - random.nextInt(), b);
			V v3 = new V(a, b + random.nextInt());
			V v4 = new V(a, b - random.nextInt());

			assertTrue(V.sameLine(origin, v1));
			assertTrue(V.sameLine(origin, v2));
			assertTrue(V.sameLine(origin, v3));
			assertTrue(V.sameLine(origin, v4));

		}
	}

	@Test
	public void checkHashCollisions()
	{
		V a = new V(1, -1);
		V b = new V(2, -1);
		V c = new V(3, -1);

		assertNotEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a.hashCode(), c.hashCode());
		assertNotEquals(b.hashCode(), c.hashCode());
	}

	@Test
	public void checkHashCollisionsSmoke()
	{
		Map<V, Integer> counter = new HashMap<V, Integer>();

		int runs = 1000;
		for (int i = -runs; i < runs; i++)
		{
			for (int j = -runs; j < runs; j++)
			{
				V a = new V(i, j);
				if (!counter.containsKey(a))
					counter.put(a, 0);

				counter.put(a, counter.get(a) + 1);
			}
		}

		for (Map.Entry<V, Integer> e : counter.entrySet())
		{
			if (e.getValue() > 1)
			{
				fail("hasherror:" + e);
			}
		}
	}
}
