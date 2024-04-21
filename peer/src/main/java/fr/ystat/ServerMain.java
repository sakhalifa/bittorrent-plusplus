package fr.ystat;

import fr.ystat.peer.Server;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerMain {

	private static final int PORT_NUMBER = 5697;

	public static void main(String[] args) {

		Executor threadPool = Executors.newFixedThreadPool(2);
		System.out.printf("Main thread id %d%n", Thread.currentThread().getId());
		try {
			new Server(threadPool).serve();
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port " + PORT_NUMBER);
			System.out.println(e.getMessage());
		}
	}
}
