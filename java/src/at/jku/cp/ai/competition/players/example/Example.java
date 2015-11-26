package at.jku.cp.ai.competition.players.example;

import at.jku.cp.ai.competition.players.Player;
import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.competition.players.example.evalfunctions.ScoreBoard;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.IBoardFunction;
import at.jku.cp.ai.rau.nodes.IBoardNode;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.algorithms.AlphaBetaSearch;
import at.jku.cp.ai.search.datastructures.Pair;

public class Example implements Player {

	/**
	 * The only constructor that will be called by the game framework! It will
	 * only be called *once*, at the start of the game!
	 */
	public Example() {
		// If your bot wants to remember stuff, here would be a good place
		// to initialize the datastructures for remembering.
	}

	/**
	 * Each time it is your bot's turn, this method will get called with the
	 * current game state. The game state consists of two things: the 'info' and
	 * the 'board'. In the 'info' object you will find which player-id you have,
	 * what's the player-id of your opponent, how many moves you have left, how
	 * much time you have left, as well as a source of randomness. Please use
	 * this randomness source, so games are actually repeatable.
	 * 
	 * @param PlayerInfo
	 *            info the current game state for the player
	 * @param IBoard
	 *            board the current board
	 * @return the move that your bot wants to make, given the current game
	 *         state
	 */
	@Override
	public Move getNextMove(PlayerInfo info, IBoard board) {
		// Of course, we want to re-use as much code as we already have, so
		// we'll re-use one of the adverserial search routines
		// with a conservative depth-limit of 8, so as not to lose so much
		// time.
		//
		// The most important thing here next to a working AlphaBeta pruning
		// search,
		// is to have a *good* scoring function.
		//
		// We implemented an example scoring function in 'ScoreBoard'.
		// This scoring function is very rudimentary, it is intended to show you
		// how you would go about writing one yourself - it is highly likely not
		// a very competitive scoring function.
		// Of course, you could choose to implement more than one scoring
		// function, implementing different strategies, depending on the current
		// game state - your creativity is the limiting factor here...

		// We'll use a (non-iterated) pruning search here, limiting the depth at
		// 10.
		AlphaBetaSearch gs = new AlphaBetaSearch((depth, current) -> depth < 10);

		// Notice that we have no time-management whatsoever! It can and will
		// happen, that it is this
		// bot's turn, and it uses up all of its remaining time, and will
		// therefore lose.
		Pair<Node, Double> result = gs.search(new IBoardNode(board.copy()),
				new IBoardFunction(new ScoreBoard(board.copy(), info.unicorn_id, info.opponent_id)));

		// Return the best move found...
		return result.f.getAction();
	}

}
