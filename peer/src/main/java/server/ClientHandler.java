package server;

import commands.ICommand;
import commands.ICommandParser;
import commands.exceptions.CommandException;
import parser.exceptions.ParserException;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable, Closeable {

	private final Socket clientSocket;
	private final PrintWriter out;
	private final BufferedReader in;
	private final Counter counter;
	private final ICommandParser parser;

	public ClientHandler(Socket clientSocket, Counter counter, ICommandParser parser) throws IOException {
		this.clientSocket = clientSocket;
		this.out =
				new PrintWriter(clientSocket.getOutputStream(), true);
		this.in = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream()));
		this.counter = counter;
		this.parser = parser;
	}

	@Override
	public void run() {
		String inputLine, outputLine;
		while (true) {
			try {
				inputLine = in.readLine();
				if (inputLine == null)
					break;
				ICommand parsedCommand = parser.parse(inputLine);
				outputLine = parsedCommand.apply(counter);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (CommandException e) {
				outputLine = "COMMAND_ERROR " + e.getMessage();
			} catch (ParserException  e){
				outputLine = "PARSER_EXCEPTION " + e.getMessage();
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
