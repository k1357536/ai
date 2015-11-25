package at.jku.cp.ai.tests;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import at.jku.cp.ai.search.datastructures.StackWithFastContains;

public class TestStackWithFastContains {
	@Test
	public void works()
	{
		StackWithFastContains<Integer> swfc = new StackWithFastContains<Integer>();
		Random random = new Random();
		
		swfc.push(0);
		for(int i = 0; i < 100; i++)
		{
			swfc.push(random.nextInt(1000) + 1);
		}
		
		assertTrue(swfc.contains(0));
	}
}
