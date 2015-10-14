package at.jku.cp.ai.rau;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.jku.cp.ai.rau.endconditions.EndCondition;
import at.jku.cp.ai.rau.endconditions.PointCollecting;
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
import at.jku.cp.ai.utils.RenderUtils;


public class Board implements IBoard, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * This variable records the passing of time as game-ticks. One Move from
	 * one player increments this variable by one.
	 */
	protected int tick;

	/**
	 * The width and height of the board
	 */
	protected int width;
	protected int height;

	/**
	 * This flag indicates whether the game is still running.
	 */
	protected boolean running;

	/**
	 * This class decides whether the game has ended, given the board state
	 * after the last move has been made.
	 */
	protected EndCondition endCondition;

	/**
	 * Walls and Paths make up the level.
	 */
	protected List<Wall> walls;
	protected List<Path> paths;

	/**
	 * Fountains dispense gold stars. They remember who has
	 * touched them last.
	 */
	protected List<Fountain> fountains;

	/**
	 * The following lists hold the various game pieces.
	 */
	protected List<Cloud> clouds;
	protected List<Unicorn> unicorns;
	protected List<Seed> seeds;
	protected List<Rainbow> rainbows;
	
	/**
	 * This list holds references to all other lists. It's mainly used to be
	 * able to conveniently iterate over all gameobjects, including walls, paths
	 * and fountains.
	 */
	protected List<List<? extends GameObject>> allObjects;

	/**
	 * The basic movements, expressed as vectors, have been given names.
	 */
	public static final V UP = new V(0, -1);
	public static final V DOWN = new V(0, 1);
	public static final V LEFT = new V(-1, 0);
	public static final V RIGHT = new V(1, 0);
	public static final V STAY = new V(0, 0);

	public static final V[] DIRECTIONS = new V[] { UP, DOWN, LEFT, RIGHT };
	public static final Map<V, Move> directionToMoveMapping = Collections
			.unmodifiableMap(new HashMap<V, Move>()
			{
				private static final long serialVersionUID = 1L;
				{
					put(UP, Move.UP);
					put(DOWN, Move.DOWN);
					put(LEFT, Move.LEFT);
					put(RIGHT, Move.RIGHT);
					put(STAY, Move.STAY);
				}
			});

	public static final Map<Move, V> moveToDirectionMapping = Collections
			.unmodifiableMap(new HashMap<Move, V>()
			{
				private static final long serialVersionUID = 1L;
				{
					put(Move.UP, UP);
					put(Move.DOWN, DOWN);
					put(Move.LEFT, LEFT);
					put(Move.RIGHT, RIGHT);
					put(Move.STAY, STAY);
					put(Move.SPAWN, STAY);
				}
			});
	
	/**
	 * This constructor is used to get a completely empty board. You will most
	 * probably never need it.
	 */
	public Board()
	{
		this.walls = new ArrayList<>();
		this.paths = new ArrayList<>();
		this.fountains = new ArrayList<>();

		this.clouds = new ArrayList<>();
		this.unicorns = new ArrayList<>();
		this.seeds = new ArrayList<>();
		this.rainbows = new ArrayList<>();

		this.allObjects = Arrays.asList(
				this.walls,
				this.paths,
				this.fountains,

				this.clouds,
				this.unicorns,
				this.seeds,
				this.rainbows);

		this.tick = 0;
		this.width = 0;
		this.height = 0;
		this.running = true;
		this.endCondition = new PointCollecting();
	}

	/**
	 * This copies the board. Depending on whether copyPassive is true or false,
	 * it'll copy the walls and paths too.
	 * 
	 * @param board
	 * @param deepCopy
	 */
	private Board(Board board, boolean deepCopy)
	{
		// copy all the passive elements only once when each player gets a
		// copy of the master-board; for search it's not important that the
		// level is being copied
		if (deepCopy)
		{
			this.walls = new ArrayList<>();
			this.paths = new ArrayList<>();

			for (Wall w : board.walls)
				this.walls.add(new Wall(w));

			for (Path p : board.paths)
				this.paths.add(new Path(p));

		} else
		{
			this.walls = board.walls;
			this.paths = board.paths;
		}

		// always copy the endcondition, as it may have state
		this.endCondition = board.endCondition.copy();

		// copy the rest of the board
		this.fountains = new ArrayList<>();
		this.clouds = new ArrayList<>();
		this.unicorns = new ArrayList<>();
		this.seeds = new ArrayList<>();
		this.rainbows = new ArrayList<>();

		for (Fountain m : board.fountains)
			this.fountains.add(new Fountain(m));

		for (Cloud c : board.clouds)
			this.clouds.add(new Cloud(c));

		for (Unicorn u : board.unicorns)
			this.unicorns.add(new Unicorn(u));

		for (Seed s : board.seeds)
			this.seeds.add(new Seed(s));

		for (Rainbow r : board.rainbows)
			this.rainbows.add(new Rainbow(r));

		this.allObjects = Arrays.asList(
				this.walls,
				this.paths,
				this.fountains,

				this.clouds,
				this.unicorns,
				this.seeds,
				this.rainbows);

		this.running = board.running;
		this.tick = board.tick;
		this.width = board.width;
		this.height = board.height;
	}

	@Override
	public IBoard copy()
	{
		return new Board(this, false);
	}

	@Override
	public IBoard deepCopy()
	{
		return new Board(this, true);
	}

	/**
	 * Save the current boardstate to file.
	 * 
	 * @param filename
	 * @param board
	 */
	public static void toFile(String filename, IBoard board)
	{
		try
		{
			List<String> lines = toStateRepresentation(board);
			Files.write(Paths.get(filename), lines, StandardCharsets.UTF_8,
					StandardOpenOption.CREATE_NEW);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load a boardstate from a file.
	 * 
	 * @param filename
	 * @return
	 */
	public static IBoard fromFile(String filename)
	{
		try
		{
			List<String> stateRepresentation = Files.readAllLines(
					Paths.get(filename), StandardCharsets.UTF_8);
			return fromStateRepresentation(stateRepresentation);

		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convert the boardstate into a string-representation.
	 * 
	 * @param board
	 * @return
	 */
	public static List<String> toStateRepresentation(IBoard board)
	{
		List<String> lines = new ArrayList<>();
		for (List<? extends GameObject> l : board.getAllObjects())
		{
			for (GameObject g : l)
			{
				lines.add(g.toString());
			}
		}

		lines.add(String.format("t(%d)", board.getTick()));
		return lines;
	}

	/**
	 * Convert a string-representation into a board.
	 * 
	 * @param stateRepresentation
	 * @return
	 */
	public static IBoard fromStateRepresentation(List<String> stateRepresentation)
	{
		Board board = new Board();
		for (String line : stateRepresentation)
		{
			switch (line.charAt(0))
			{
			case 'c':
				board.clouds.add(Cloud.fromString(line));
				break;
			case 'u':
				board.unicorns.add(Unicorn.fromString(line));
				break;
			case 's':
				board.seeds.add(Seed.fromString(line));
				break;
			case 'r':
				board.rainbows.add(Rainbow.fromString(line));
				break;
			case 'w':
				board.walls.add(Wall.fromString(line));
				break;
			case 'p':
				board.paths.add(Path.fromString(line));
				break;
			case 'f':
				board.fountains.add(Fountain.fromString(line));
				break;
			case 't':
				Pattern p = Pattern.compile("t\\((\\d+)\\)");
				Matcher m = p.matcher(line);
				if (m.matches())
				{
					board.tick = Integer.parseInt(m.group(1));
				}
				else
				{
					throw new RuntimeException("invalid tick representation");
				}
				break;
			}
		}

		for (List<? extends GameObject> l : board.allObjects)
		{
			for (GameObject g : l)
			{
				board.width = Math.max(board.width, g.pos.x);
				board.height = Math.max(board.height, g.pos.y);
			}
		}

		// b/c indexing starts with 0!
		board.width++;
		board.height++;

		return board;
	}

	/**
	 * Load a board from a level-file.
	 * 
	 * @param filename
	 * @return
	 */
	public static Board fromLevelFile(String filename)
	{
		try
		{
			return fromLevelRepresentation(Files.readAllLines(
					Paths.get(filename), StandardCharsets.UTF_8));
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Given a level-file as a list of strings, convert it to a board.
	 * 
	 * @param levelRepresentation
	 * @return
	 */
	public static Board fromLevelRepresentation(List<String> levelRepresentation)
	{
		Board board = new Board();

		int next_id = 0;
		int y = 0;
		for (String line : levelRepresentation)
		{
			for (int x = 0; x < line.length(); x++)
			{
				switch (line.charAt(x))
				{
				case '#':
					board.walls.add(new Wall(new V(x, y)));
					break;

				case '.':
					board.paths.add(new Path(new V(x, y)));
					break;

				case 'c':
					board.clouds.add(new Cloud(new V(x, y)));
					board.paths.add(new Path(new V(x, y)));
					break;

				case 'p':
					board.unicorns.add(new Unicorn(new V(x, y), next_id));
					board.paths.add(new Path(new V(x, y)));
					next_id++;
					break;

				case '0':
					board.unicorns.add(new Unicorn(new V(x, y), 0));
					board.paths.add(new Path(new V(x, y)));
					next_id = 1;
					break;

				case '1':
					board.unicorns.add(new Unicorn(new V(x, y), 1));
					board.paths.add(new Path(new V(x, y)));
					next_id = 0;
					break;

				case 'f':
					board.fountains.add(new Fountain(new V(x, y), -1));
					board.paths.add(new Path(new V(x, y)));
					break;

				case '*':
					board.seeds.add(new Seed(
							new V(x, y),
							23,
							Seed.DEFAULT_FUSE,
							Seed.DEFAULT_RANGE));
					board.paths.add(new Path(new V(x, y)));
					break;

				case ' ':
					// ignore this, to allow for non-rectangular level-shapes to
					// be converted
					break;

				default:
					String msg = String.format(
							"illegal level-tile '%c' encountered at pos %s",
							line.charAt(x), new V(x, y));
					throw new RuntimeException(msg);
				}
			}
			y++;
		}

		for (List<? extends GameObject> l : board.allObjects)
		{
			for (GameObject g : l)
			{
				board.width = Math.max(board.width, g.pos.x);
				board.height = Math.max(board.height, g.pos.y);
			}
		}

		// b/c indexing starts with 0!
		board.width++;
		board.height++;

		return board;
	}

	public static V[] getDirections()
	{
		return DIRECTIONS;
	}

	public static Map<V, Move> getDirectionToMoveMapping()
	{
		return directionToMoveMapping;
	}

	public static Map<Move, V> getMoveToDirectionMapping()
	{
		return moveToDirectionMapping;
	}

	/**
	 * Return all the gameobjects at a specified position.
	 * 
	 * @param pos
	 *            the position on the board
	 * @return a list of gameobjects
	 */
	@Override
	public List<GameObject> at(V pos)
	{
		List<GameObject> list = new ArrayList<>();
		for (List<? extends GameObject> l : allObjects)
		{
			for (GameObject g : l)
			{
				if (pos.equals(g.pos))
				{
					list.add(g);
				}
			}
		}
		return list;
	}

	/**
	 * Checks a position on the board, if it stops rainbows from propagating.
	 * 
	 * @param pos
	 *            the position on the board to check
	 * @return true if the position contains any GameObject that stops Rainbows
	 */
	@Override
	public boolean isStoppingRainbow(V pos)
	{
		for (GameObject g : at(pos))
		{
			if (g.pos.equals(pos) && g.stopsRainbow)
				return true;
		}

		return false;
	}

	/**
	 * Checks a position on the board, if it is removable
	 *
	 * @param pos
	 *            the position on the board
	 * @return true if all gameobjects at the position are removable
	 */
	@Override
	public boolean isRemovable(V pos)
	{
		for (GameObject g : at(pos))
		{
			if (g.pos.equals(pos) && g.isRemovable)
				return true;
		}
		return false;
	}

	/**
	 * Checks a position on the board, if one or more rainbows are on it
	 *
	 * @param pos
	 *            the position on the board
	 * @return true if there is a rainbow at this position
	 */
	@Override
	public boolean isRainbowAt(V pos)
	{
		for (GameObject g : at(pos))
		{
			if (g.pos.equals(pos) && g instanceof Rainbow)
				return true;
		}
		return false;
	}

	/**
	 * Checks a position on the board, if it is walkable.
	 * 
	 * @param pos
	 *            the position on the board
	 * @return true if all gameobjects at this position can be walked upon
	 */
	@Override
	public boolean isPassable(V pos)
	{
		List<GameObject> objs = at(pos);
		if (objs.isEmpty())
			return false;

		for (GameObject g : objs)
		{
			if (g.pos.equals(pos) && !g.isPassable)
				return false;
		}
		return true;
	}

	/**
	 * This method changes the state of the board.
	 */
	protected void update()
	{
		if (!running)
		{
			throw new RuntimeException("Game is not running!");
		}

		// the following loop has the effect that as long as seeds were
		// blossoming, the resulting rainbows are updated, until no more seeds
		// are blossoming

		// only decrease the fuse of all seeds once
		decreaseSeedFuse();

		while (updateSeeds())
		{
			// if a seed has blossomed, it is replaced by a rainbow
			// rainbows are updated by the next call, which in turn
			// can lead to another seed blossoming, if it is touched
			// ... by the rainbow :)
		}

		updateRainbows();
		List<Cloud> evaporated = updateClouds();
		List<Unicorn> sailing = updateUnicorns();

		updateFountains();

		if (endCondition.hasEnded(this, evaporated, sailing))
		{
			running = false;
		} else
		{
			tick += 1;
		}
	}

	/**
	 * Decreases the fuse of each seed by one.
	 */
	private void decreaseSeedFuse()
	{
		for (Seed s : seeds)
		{
			s.fuse -= 1;
		}
	}

	/**
	 * Updates all the seeds and checks wether they are blossoming in case of
	 * blossoming seeds this method has to be run again, as other seeds could
	 * have been touched by the rainbow.
	 */
	private boolean updateSeeds()
	{
		List<Seed> blossoming = new ArrayList<>();
		for (Seed s : seeds)
		{
			// if a seed's fuse has run out, or
			// it is touched by another rainbow
			if (s.fuse == 0 || isRainbowAt(s.pos))
			{
				blossoming.add(s);
			}
		}

		for (Seed s : blossoming)
		{
			unicorns.get(s.spawnedBy).seeds++;
			seeds.remove(s);
			blossom(s);
		}

		// return true if any seeds were blossoming
		return blossoming.size() > 0;
	}

	/**
	 * Let one seed blossom
	 */
	private void blossom(Seed s)
	{
		rainbows.add(new Rainbow(s.pos, Rainbow.DEFAULT_DURATION));

		// go in each direction
		for (V d : DIRECTIONS)
		{
			// for as long as the rainbow ranges, and if it is not stopped
			propagateRainbow(s, d);
		}
	}

	/**
	 * Propagate the rainbow in one direction
	 */
	private void propagateRainbow(Seed s, V d)
	{
		for (int i = 1; i < s.range; i++)
		{
			V current = V.add(s.pos, V.emul(d, i));
			if (isPassable(current) || isRemovable(current))
			{
				rainbows.add(new Rainbow(current, Rainbow.DEFAULT_DURATION));
			}

			if (isStoppingRainbow(current))
			{
				return;
			}
		}
	}

	/**
	 * Updates all rainbows, removes rainbows whose duration has run out.
	 */
	private void updateRainbows()
	{
		List<Rainbow> vanishing = new ArrayList<>();
		for (Rainbow r : rainbows)
		{
			r.duration -= 1;
			if (r.duration == 0)
			{
				vanishing.add(r);
			}
		}

		for (Rainbow r : vanishing)
		{
			rainbows.remove(r);
		}

	}

	/**
	 * Updates all clouds. If clouds are touched by rainbows, this removes the
	 * cloud at that position.
	 */
	private List<Cloud> updateClouds()
	{
		List<Cloud> evaporated = new ArrayList<>();
		for (Cloud b : clouds)
		{
			if (isRainbowAt(b.pos))
			{
				evaporated.add(b);
			}
		}

		for (Cloud b : evaporated)
		{
			clouds.remove(b);
		}

		return evaporated;
	}

	/**
	 * Checks wether any unicorn was touched by a rainbow if so, it is sent
	 * sailing. If one or no unicorn remains, the game ends.
	 */
	private List<Unicorn> updateUnicorns()
	{
		List<Unicorn> sailing = new ArrayList<>();
		for (Unicorn u : unicorns)
		{
			// we can go sailing! wheee!
			if (isRainbowAt(u.pos))
			{
				sailing.add(u);
			}
		}

		for (Unicorn u : sailing)
		{
			unicorns.remove(u);
		}

		return sailing;
	}

	/**
	 * Updates each of the fountains at each turn.
	 */
	private void updateFountains()
	{
		for (Fountain m : fountains)
		{
			int unicornCount = 0;
			for (GameObject g : at(m.pos))
			{
				if (g instanceof Unicorn)
				{
					unicornCount++;
					m.lastVisitedBy = ((Unicorn) g).id;
				}
			}

			if (unicornCount > 1)
			{
				m.lastVisitedBy = Fountain.LAST_VISITED_BY_DEFAULT;
			}
		}
	}
	
	@Override
	public List<Move> getPossibleMoves()
	{
		return getPossibleMoves(getCurrentUnicorn());
	}

	/**
	 * Get all possible moves for a unicorn, given the *current* game state
	 * 
	 * @param unicorn
	 *            the Unicorn
	 * @return a list of all possible moves
	 */
	private List<Move> getPossibleMoves(Unicorn unicorn)
	{
		if (!running)
		{
			return Collections.emptyList();
		}

		List<Move> moves = new ArrayList<>();

		// we can always do nothing
		moves.add(Move.STAY);

		// we can stay, and spawn a seed
		if (isPassable(unicorn.pos) && unicorn.seeds > 0)
		{
			moves.add(Move.SPAWN);
		}

		// we can go into a direction
		for (V d : DIRECTIONS)
		{
			V current = V.add(unicorn.pos, d);
			if (isPassable(current))
			{
				moves.add(directionToMoveMapping.get(d));
			}
		}
		return moves;
	}

	@Override
	public boolean executeMove(Move move)
	{	
		if (running && !unicorns.isEmpty())
			return executeMove(getCurrentUnicorn(), move);
		else
			return false;
	}

	/**
	 * Try to execute a move.
	 * 
	 * @param move
	 *            the move to execute
	 * @return true: if move could be executed without invalidating game state
	 *         false: otherwise
	 */
	private boolean executeMove(Unicorn unicorn, Move move)
	{
		// System.out.println("executeMove(" + unicorn + "," + move + ")");
		boolean executed = false;

		// we can always stay
		if (move == Move.STAY)
		{
			executed = true;
		} else if (move == Move.SPAWN && isPassable(unicorn.pos) && unicorn.seeds > 0)
		{
			// place a seed at the old position, if possible
			// (the other unicorn could have placed a seed there
			// already)
			
			unicorn.seeds--;
			seeds.add(new Seed(unicorn.pos,
					unicorn.id,
					Seed.DEFAULT_FUSE,
					Seed.DEFAULT_RANGE));
			executed = true;
		} else if(move != Move.SPAWN)
		{
			// move the unicorn
			V newpos = V.add(unicorn.pos, getMoveToDirectionMapping().get(move));
			if (isPassable(newpos))
			{
				unicorn.pos = newpos;
				executed = true;
			}
		}
		
		update();

		return executed;
	}

	/**
	 * Renders a board state as text. Please note that the 'rendering' can be
	 * non-invertible, due to overlapping gameobjects.
	 * 
	 * @return a text-representation of the boardstate.
	 */
	@Override
	public String toString()
	{
		if (width <= 0 || height <= 0)
		{
			return "completely empty board";
		}

		char[][] rep = getTextBoard();
		StringBuilder sb = new StringBuilder();
		final String separator = System.getProperty("line.separator");
		sb.append(separator).append("tick:").append(tick).append(separator);
		sb.append(RenderUtils.asString(rep));

		return sb.toString();
	}

	/**
	 * Get a representation of the boardstate as a 2D character array.
	 * 
	 * @return
	 */
	@Override
	public char[][] getTextBoard()
	{
		char[][] rep = new char[width][height];
		for (List<? extends GameObject> l : allObjects)
		{
			for (GameObject g : l)
			{
				rep[g.pos.x][g.pos.y] = g.rep;
			}
		}
		return rep;
	}

	/**
	 * Returns the unicorn who may make a move during this tick.
	 * 
	 * @return the unicorn
	 */
	@Override
	public Unicorn getCurrentUnicorn()
	{
		return unicorns.get(tick % unicorns.size());
	}

	@Override
	public final int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allObjects == null) ? 0 : allObjects.hashCode());
		result = prime * result + ((clouds == null) ? 0 : clouds.hashCode());
		result = prime * result + ((endCondition == null) ? 0 : endCondition.hashCode());
		result = prime * result + height;
		result = prime * result + ((fountains == null) ? 0 : fountains.hashCode());
		result = prime * result + ((paths == null) ? 0 : paths.hashCode());
		result = prime * result + ((rainbows == null) ? 0 : rainbows.hashCode());
		result = prime * result + (running ? 1231 : 1237);
		result = prime * result + ((seeds == null) ? 0 : seeds.hashCode());
		result = prime * result + ((unicorns == null) ? 0 : unicorns.hashCode());
		result = prime * result + ((walls == null) ? 0 : walls.hashCode());
		result = prime * result + width;
		return result;
	}

	@Override
	public final boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Board))
			return false;
		Board other = (Board) obj;
		if (allObjects == null)
		{
			if (other.allObjects != null)
				return false;
		} else if (!allObjects.equals(other.allObjects))
			return false;
		if (clouds == null)
		{
			if (other.clouds != null)
				return false;
		} else if (!clouds.equals(other.clouds))
			return false;
		if (endCondition == null)
		{
			if (other.endCondition != null)
				return false;
		} else if (!endCondition.equals(other.endCondition))
			return false;
		if (height != other.height)
			return false;
		if (fountains == null)
		{
			if (other.fountains != null)
				return false;
		} else if (!fountains.equals(other.fountains))
			return false;
		if (paths == null)
		{
			if (other.paths != null)
				return false;
		} else if (!paths.equals(other.paths))
			return false;
		if (rainbows == null)
		{
			if (other.rainbows != null)
				return false;
		} else if (!rainbows.equals(other.rainbows))
			return false;
		if (running != other.running)
			return false;
		if (seeds == null)
		{
			if (other.seeds != null)
				return false;
		} else if (!seeds.equals(other.seeds))
			return false;
		if (unicorns == null)
		{
			if (other.unicorns != null)
				return false;
		} else if (!unicorns.equals(other.unicorns))
			return false;
		if (walls == null)
		{
			if (other.walls != null)
				return false;
		} else if (!walls.equals(other.walls))
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	@Override
	public int getTick()
	{
		return tick;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public boolean isRunning()
	{
		return running;
	}

	@Override
	public EndCondition getEndCondition()
	{
		return endCondition;
	}

	@Override
	public void setEndCondition(EndCondition endCondition)
	{
		this.endCondition = endCondition;
	}

	@Override
	public List<Wall> getWalls()
	{
		return walls;
	}

	@Override
	public List<Path> getPaths()
	{
		return paths;
	}

	@Override
	public List<Fountain> getFountains()
	{
		return fountains;
	}

	@Override
	public List<Cloud> getClouds()
	{
		return clouds;
	}

	@Override
	public List<Unicorn> getUnicorns()
	{
		return unicorns;
	}

	@Override
	public List<Seed> getSeeds()
	{
		return seeds;
	}

	@Override
	public List<Rainbow> getRainbows()
	{
		return rainbows;
	}

	@Override
	public List<List<? extends GameObject>> getAllObjects()
	{
		return allObjects;
	}

}
