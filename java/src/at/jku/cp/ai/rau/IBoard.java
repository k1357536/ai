package at.jku.cp.ai.rau;

import java.util.List;

import at.jku.cp.ai.rau.endconditions.EndCondition;
import at.jku.cp.ai.rau.objects.Cloud;
import at.jku.cp.ai.rau.objects.GameObject;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.rau.objects.Path;
import at.jku.cp.ai.rau.objects.Rainbow;
import at.jku.cp.ai.rau.objects.Seed;
import at.jku.cp.ai.rau.objects.Unicorn;
import at.jku.cp.ai.rau.objects.V;
import at.jku.cp.ai.rau.objects.Wall;

/**
 * This interface defines what a board must be able to do at the minimum
 */
public interface IBoard
{
	/**
	 * This method deep-copies all the <b>active</b> gameobjects:
	 * * EndCondition
	 * * Fountains
	 * * Clouds
	 * * Unicorns
	 * * Seeds
	 * * Rainbows
	 * 
	 * And copies only references to the <b>passive</b> ones:
	 * - Walls
	 * - Paths
	 * @return a partial deep-copy of the board
	 */
	public IBoard copy();
	
	/**
	 * This method deep-copies the complete object
	 * @return a deep-copy of the board
	 */
	public IBoard deepCopy();
	
	/**
	 * This method gives us a List<Move> of possible moves for the
	 * current unicorn / player.
	 * @return a list of possible moves
	 */
	public List<Move> getPossibleMoves();

	/**
	 * This is the only method to change the state of the board, according to the
	 * move specified.
	 * @param move
	 * @return true if the move was executed, false if it couldn't be executed
	 */
	public boolean executeMove(Move move);
	
	/**
	 * Return all the gameobjects at a specified position.
	 * 
	 * @param pos
	 *            the position on the board
	 * @return a list of gameobjects
	 */
	public List<GameObject> at(V pos);

	/**
	 * Checks a position on the board, if it stops rainbows from propagating.
	 * 
	 * @param pos
	 *            the position on the board to check
	 * @return true if the position contains any GameObject that stops Rainbows
	 */
	public boolean isStoppingRainbow(V pos);

	/**
	 * Checks a position on the board, if it is removable
	 *
	 * @param pos
	 *            the position on the board
	 * @return true if all gameobjects at the position are removable
	 */
	public boolean isRemovable(V pos);

	/**
	 * Checks a position on the board, if one or more rainbows are on it
	 *
	 * @param pos
	 *            the position on the board
	 * @return true if there is a rainbow at this position
	 */
	public boolean isRainbowAt(V pos);

	/**
	 * Checks a position on the board, if it is walkable.
	 * 
	 * @param pos
	 *            the position on the board
	 * @return true if all gameobjects at this position can be walked upon
	 */
	public boolean isPassable(V pos);

	/**
	 * Renders a board state as text. Please note that the 'rendering' can be
	 * non-invertible, due to overlapping gameobjects.
	 * 
	 * @return a text-representation of the boardstate.
	 */
	public String toString();

	/**
	 * Get a representation of the boardstate as a 2D character array.
	 * 
	 * @return a 2D array of chars
	 */
	public char[][] getTextBoard();

	/**
	 * Returns the unicorn who may make a move during this tick.
	 * 
	 * @return the unicorn
	 */
	public Unicorn getCurrentUnicorn();

	public int hashCode();

	public boolean equals(Object obj);

	public int getTick();

	public int getWidth();

	public int getHeight();

	public boolean isRunning();

	public EndCondition getEndCondition();

	public void setEndCondition(EndCondition endCondition);

	public List<Wall> getWalls();

	public List<Path> getPaths();

	public List<Fountain> getFountains();

	public List<Cloud> getClouds();

	public List<Unicorn> getUnicorns();

	public List<Seed> getSeeds();

	public List<Rainbow> getRainbows();

	public List<List<? extends GameObject>> getAllObjects();



}