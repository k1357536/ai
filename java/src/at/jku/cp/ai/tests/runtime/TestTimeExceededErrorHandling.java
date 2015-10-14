package at.jku.cp.ai.tests.runtime;

import static at.jku.cp.ai.tests.runtime.RuntimeTestUtils.convert;
import static at.jku.cp.ai.tests.runtime.RuntimeTestUtils.exec;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import at.jku.cp.ai.competition.players.random.RandomWalkPlayer;
import at.jku.cp.ai.competition.runtime.RuntimeSP;
import at.jku.cp.ai.rau.endconditions.PointCollecting;
import at.jku.cp.ai.utils.Constants;

public class TestTimeExceededErrorHandling
{

	@Test
	public void timeExceeded() throws InterruptedException, IOException
	{
		Path log = Files.createTempDirectory("testTimeExceeded");
		
		// "<p0> <p1> <level> <timelimit> <movelimit> [<seed>] [<logdir>]"
		Process runtime = exec(
				RuntimeSP.class,
				TimeExceedingPlayer.class.getCanonicalName(),
				RandomWalkPlayer.class.getCanonicalName(),
				Constants.ASSET_PATH + "/default.lvl",
				"1",		// TimeExceedingPlayer takes 2 seconds in first turn, so it
					 		// loses!
				"1000",		// movelinmit
				"0",		// seed
				log.toString()
				);

		String actualStderr = convert(runtime.getErrorStream());
		String actualStdout = convert(runtime.getInputStream());

		int error = runtime.waitFor();
		
		assertEquals("", actualStderr);
		assertEquals("", actualStdout);
		assertEquals(0, error); // timeout is a normal game event
		
		String expectedStdout = String.format(
				"tick:0\n"
				+ "outcome:%s\n"
				+ "winner:1\n"
				+ "score_p0:0\n"
				+ "score_p1:0\n"
				+ "time_p0:0\n"
				+ "time_p1:1000\n" // 1000 [ms]
				+ "p0:%s\n"
				+ "p1:%s\n"
				+ "level:" + Constants.ASSET_PATH + "/default.lvl\n"
				+ "timelimit:1\n"
				+ "movelimit:1000\n"
				+ "seed:0\n",
				PointCollecting.Outcome.TIMEOUT,
				TimeExceedingPlayer.class.getCanonicalName(),
				RandomWalkPlayer.class.getCanonicalName());

		assertEquals(expectedStdout, convert(Paths.get(log.toString(), "result.yaml")));
	}
}
