package server;

import commands.CommandParser;
import commands.ICommand;
import commands.exceptions.CommandException;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable, Closeable {

	private final Socket clientSocket;
	private final PrintWriter out;
	private final BufferedReader in;
	private final Counter counter;

	public ClientHandler(Socket clientSocket, Counter counter) throws IOException {
		this.clientSocket = clientSocket;
		this.out =
				new PrintWriter(clientSocket.getOutputStream(), true);
		this.in = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream()));
		this.counter = counter;
	}

	@Override
	public void run() {
		String inputLine, outputLine;
		while (true) {
			try {
				inputLine = in.readLine();
				if (inputLine == null)
					break;
				ICommand parsedCommand = CommandParser.parseInput(inputLine);
				outputLine = parsedCommand.apply(counter);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (CommandException e) {
				outputLine = "ERROR " + e.getMessage();
			}
			out.println(outputLine);
		}
		try {
			this.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}
}
