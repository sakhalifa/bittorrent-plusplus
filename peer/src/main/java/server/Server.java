package server;

import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

public class Server {
	private final ServerSocket serverSocket;
	private final Executor threadExecutor;
	private final Counter counter;

	public Server(Executor threadExecutor, int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
		this.threadExecutor = threadExecutor;
		this.counter = new Counter();
	}

	public void serve() {
		while(true){
			try {
				System.out.println("Waiting for connection");
				Socket clientSocket = serverSocket.accept();
				threadExecutor.execute(new ClientHandler(clientSocket, counter));
			} catch (IOException e) {
				System.out.println("Error listening to a connection");
				e.printStackTrace();
				break;
			}
		}
	}
}
