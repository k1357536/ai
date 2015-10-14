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

public class TestSpaceExceededErrorHandling
{

	@Test
	public void spaceExceeded() throws InterruptedException, IOException
	{
		// SpaceExceedingPlayer allocates too much memory in first turn, so it
		// loses!
		// <p0> <p1> <level> <timelimit [s]> <movelimit> <seed> <logdir>
		Path log = Files.createTempDirectory("testSpaceExceeded");
		
		Process runtime = exec(
				RuntimeSP.class,
				SpaceExceedingPlayer.class.getCanonicalName(),
				RandomWalkPlayer.class.getCanonicalName(),
				Constants.ASSET_PATH + "/default.lvl",
				"300", // 5 minutes
				"1000", // 1000 moves
				"0", // seed of 0
				log.toString()
		);

		String actualStderr = convert(runtime.getErrorStream());
		String actualStdout = convert(runtime.getInputStream());

		int error = runtime.waitFor();

		assertEquals("", actualStderr); // we expect no error messages ...
		assertEquals("", actualStdout); // ... nor anything on stdout ...
		assertEquals(0, error); // ... and an errorlevel of 0, b/c timeout is a normal game event

		// these are the contents we expect in the log-file
		String expectedLogfileContents = String.format(
				"tick:0\n"
				+ "outcome:%s\n"
				+ "winner:1\n"
				+ "score_p0:0\n"
				+ "score_p1:0\n"
				+ "time_p0:300000\n" // 300000 [ms] -> 5 [min]
				+ "time_p1:300000\n" // 300000 [ms] -> 5 [min]
				+ "p0:%s\n"
				+ "p1:%s\n"
				+ "level:" + Constants.ASSET_PATH + "/default.lvl\n"
				+ "timelimit:300\n"
				+ "movelimit:1000\n"
				+ "seed:0\n",
				PointCollecting.Outcome.MEMOUT.toString(),
				SpaceExceedingPlayer.class.getCanonicalName(),
				RandomWalkPlayer.class.getCanonicalName());

		assertEquals(expectedLogfileContents, convert(Paths.get(log.toString(), "result.yaml")));
	}
}
