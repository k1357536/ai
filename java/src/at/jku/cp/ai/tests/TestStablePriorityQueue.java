package at.jku.cp.ai.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import at.jku.cp.ai.search.datastructures.Pair;
import at.jku.cp.ai.search.datastructures.StablePriorityQueue;

public class TestStablePriorityQueue
{

	List<Pair<Integer, String>> expected = Arrays.asList(
			new Pair<>(1, "A"),
			new Pair<>(1, "B"),
			new Pair<>(1, "C"),
			new Pair<>(2, "D"),
			new Pair<>(3, "E"),
			new Pair<>(4, "F"));

	@Test
	public void testOrdering1()
	{
		StablePriorityQueue<Integer, String> pq = new StablePriorityQueue<>();

		for (Pair<Integer, String> p : expected)
		{
			pq.add(p);
		}

		for (Pair<Integer, String> p : expected)
		{
			assertEquals(p, pq.poll());
		}
	}

	@Test
	public void testOrdering2()
	{
		StablePriorityQueue<Integer, String> pq = new StablePriorityQueue<>();

		List<Pair<Integer, String>> fixture = Arrays.asList(
				new Pair<>(1, "A"),
				new Pair<>(2, "D"),
				new Pair<>(3, "E"),
				new Pair<>(1, "B"),
				new Pair<>(4, "F"),
				new Pair<>(1, "C"));

		for (Pair<Integer, String> p : fixture)
		{
			pq.add(p);
		}

		for (Pair<Integer, String> p : expected)
		{
			assertEquals(p, pq.poll());
		}
	}

	@Test
	public void testOrdering3()
	{
		StablePriorityQueue<Integer, String> pq = new StablePriorityQueue<>();

		List<Pair<Integer, String>> fixture = Arrays.asList(
				new Pair<>(4, "F"),
				new Pair<>(2, "D"),
				new Pair<>(1, "A"),
				new Pair<>(1, "B"),
				new Pair<>(3, "E"),
				new Pair<>(1, "C"));

		for (Pair<Integer, String> p : fixture)
		{
			pq.add(p);
		}

		for (Pair<Integer, String> p : expected)
		{
			assertEquals(p, pq.poll());
		}
	}

	@Test
	public void testOrderingViaAddAll()
	{
		StablePriorityQueue<Integer, String> pq = new StablePriorityQueue<>();

		List<Pair<Integer, String>> fixture = Arrays.asList(
				new Pair<>(4, "F"),
				new Pair<>(1, "A"),
				new Pair<>(2, "D"),
				new Pair<>(1, "B"),
				new Pair<>(3, "E"),
				new Pair<>(1, "C"));

		pq.addAll(fixture);

		for (Pair<Integer, String> p : expected)
		{
			assertEquals(p, pq.poll());
		}
	}
}
