package at.jku.cp.ai.competition.runtime.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import at.jku.cp.ai.competition.players.Player;
import at.jku.cp.ai.competition.players.PlayerInfo;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.datastructures.Pair;

public class JVMPlayer implements Player
{
	private Process process;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private long timeTaken;

	public JVMPlayer(int port, String playerClass) throws IOException, InterruptedException
	{
		this.process = exec(PlayerMoveServer.class, port, playerClass);
		Thread.sleep(1000);
		socket = new Socket("localhost", port);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		timeTaken = 0L;
	}

	public long getTimeTaken()
	{
		return timeTaken;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Move getNextMove(PlayerInfo info, IBoard board)
	{
		try
		{
			out.writeObject(new CommWrapper(info, board));
			out.flush();
			Pair<Move, Long> moveAndTime = (Pair<Move, Long>) in.readObject();
			timeTaken = moveAndTime.s;
			return (Move) moveAndTime.f;
		} catch (ClassNotFoundException | IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void destroy()
	{
		try
		{
			out.close();
			in.close();
			socket.close();
			process.destroy();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void close()
	{
		try
		{
			out.writeObject(CommWrapper.CW_END);
			out.flush();
			out.close();
			in.close();
			socket.close();
			process.waitFor();
		} catch (InterruptedException | IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private Process exec(
			Class<?> mainClass,
			int port,
			String playerClass)
	{
		try
		{
			String javaHome = System.getProperty("java.home");
			String javaBin = javaHome +
					File.separator + "bin" +
					File.separator + "java";
			String classpath = System.getProperty("java.class.path");
			String mainClassName = mainClass.getCanonicalName();

			List<String> all = Arrays.asList(
					javaBin,
					"-Xms512M",
					"-Xmx512M",
					"-cp", classpath,
					mainClassName,
					Integer.toString(port),
					playerClass);

			ProcessBuilder builder = new ProcessBuilder(all);
			return builder.start();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
