package at.jku.cp.ai.tests;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import at.jku.cp.ai.rau.BlankLevel;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.LevelCost;
import at.jku.cp.ai.rau.functions.RandomCost;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.rau.objects.V;

public class TestRandomCost
{
	@Test
	public void persist() throws Exception
	{
		IBoard blank = new BlankLevel(11, 11);
		RandomCost expected = new RandomCost(11, 11, new Random(23L), 10);
		
		String temp = Files.createTempFile(null, null).toString();
		
		Files.write(
				Paths.get(temp), 
				expected.render(blank).getBytes(),
				StandardOpenOption.CREATE);
		
		List<String> costs = Files.readAllLines(Paths.get(temp), StandardCharsets.UTF_8);
		
		LevelCost actual = new LevelCost(costs);
		
		blank.getUnicorns().add(new Unicorn(new V(1, 1), 0));
		for(Path p: blank.getPaths())
		{
			// put the unicorn on each walking path ...
			blank.getUnicorns().get(0).pos = p.pos;
			IBoardNode blankNode = new IBoardNode(blank);
			assertEquals(expected.apply(blankNode), actual.apply(blankNode), 0.0);
		}
	}
}
