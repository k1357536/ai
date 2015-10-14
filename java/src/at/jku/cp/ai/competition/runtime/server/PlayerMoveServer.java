package at.jku.cp.ai.competition.runtime.server;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import at.jku.cp.ai.competition.players.Player;
import at.jku.cp.ai.rau.objects.Move;
import at.jku.cp.ai.search.datastructures.Pair;

public class PlayerMoveServer
{
	private static final int INVALID_ARGUMENTS = 1;

	public static void main(String[] args) throws Exception
	{
		if (args.length != 2)
		{
			System.exit(INVALID_ARGUMENTS);
		}

		int port = Integer.parseInt(args[0]);
		String classFilename = args[1];

		Player player = (Player) Class.forName(classFilename).newInstance();
		ServerSocket server = new ServerSocket(port);
		Socket socket = server.accept();

		ObjectInput in = new ObjectInputStream(socket.getInputStream());
		ObjectOutput out = new ObjectOutputStream(socket.getOutputStream());

		CommWrapper input = null;
		while (CommWrapper.CW_END != (input = (CommWrapper) in.readObject()))
		{
			// let the player determine its next move, measure time
			// taken
			long start = System.currentTimeMillis();
			Move move = player.getNextMove(input.info, input.board);
			long end = System.currentTimeMillis();
			long timeTaken = end - start;
			
			out.writeObject(new Pair<Move, Long>(move, timeTaken));
			out.flush();
		}

		socket.close();
		server.close();
	}
}
