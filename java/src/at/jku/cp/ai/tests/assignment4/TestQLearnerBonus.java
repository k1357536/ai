package at.jku.cp.ai.tests.assignment4;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.endconditions.CloudEvaporator;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.learning.QLearner;

public class TestQLearnerBonus
{
	private boolean verbose;

	public TestQLearnerBonus() {
		this.verbose = false;
	}

	////////////////////////////////////////////////////////
	//these are some bonus levels - your QLearning implementation
	//may not be able to solve these with only 20.000 learning
	//episodes ...
	//... the point here is to adapt the QLearner in such a way
	//that it can learn a suitable model with only 20.000 episodes!

	@Test
	public void bonus0()
	{
		doLearningAndTestOnBoard("bonus0.lvl", verbose);
	}

	@Test
	public void bonus1()
	{
		doLearningAndTestOnBoard("bonus1.lvl", verbose);
	}

	@Test
	public void bonus2()
	{
		doLearningAndTestOnBoard("bonus2.lvl", verbose);
	}

	private void doLearningAndTestOnBoard(String filename, boolean verbose)
	{
		IBoard startBoard = Board.fromLevelFile("assets/assignment4/" + filename);
		// we restrict the number of seeds the unicorn can spawn, to make the
		// statespace considerably smaller!
		startBoard.getUnicorns().get(0).seeds = 1;

		// we need a new end-condition for the game as well
		startBoard.setEndCondition(new CloudEvaporator());

		if (verbose)
			System.out.println(startBoard);

		IBoard learnBoard = startBoard.copy();
		IBoard runBoard = startBoard.copy();

		/////////////////
		// 20.000 episodes is enough to learn a successful model for
		// all but the bonus excercises

		// for the bonus exercises, you will have to adapt the QLearner
		// implementation, such that it is able to learn a proper model
		// with 20000 episodes
		QLearner learner = new QLearner(new Random(2468L), 20001, 0.9, verbose, true);

		// learning the qmatrix happens here
		learner.learnQFunction(learnBoard);

		// apply the learned model to the original board
		// if we go sailing, or need more than 100 ticks before
		// we reach the goal, this test will fail
		while (runBoard.isRunning() && runBoard.getTick() < 100)
		{
			// determine the next move based upon the current board
			// state and the learned model
			Move nextMove = learner.getMove(runBoard);
			runBoard.executeMove(nextMove);

			if (verbose)
				System.out.println(runBoard);
		}

		if (verbose)
			System.out.println(runBoard);

		// the learned model should have guided the unicorn to the cloud
		// and the cloud must have been evaporated as well
		assertEquals(0, runBoard.getEndCondition().getWinner());
	}

}
