import server.Server;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerMain {

	private static int PORT_NUMBER = 5697;

	public static void main(String[] args) {
		Executor threadPool = Executors.newCachedThreadPool();
		threadPool.execute(() -> {
			try {
				new Server(threadPool, PORT_NUMBER).serve();
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port " + PORT_NUMBER);
				System.out.println(e.getMessage());
			}
		});
	}
}
