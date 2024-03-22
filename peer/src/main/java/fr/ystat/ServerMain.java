package fr.ystat;

import fr.ystat.config.DummyConfigurationManager;
import fr.ystat.config.IConfigurationManager;
import fr.ystat.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerMain {

	private static final int PORT_NUMBER = 5697;

	public static void main(String[] args) {
		Executor threadPool = Executors.newFixedThreadPool(2);
		try {
			// TODO Change it to an actual config manager
			new Server(threadPool, new DummyConfigurationManager()).serve();
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port " + PORT_NUMBER);
			System.out.println(e.getMessage());
		}
	}
}
