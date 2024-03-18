import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EchoServer {
	public static void main(String[] args) throws IOException {
		try (var serverChannel = AsynchronousServerSocketChannel.open()) {
			InetSocketAddress hostAddress = new InetSocketAddress("localhost", 4999);
			serverChannel.bind(hostAddress);
			Executor threadPool = Executors.newFixedThreadPool(2);
			while (true) {
				serverChannel.accept(
						null, new CompletionHandler<>() {

							@Override
							public void completed(
									AsynchronousSocketChannel clientChannel, Object attachment) {
								if (serverChannel.isOpen()) {
									serverChannel.accept(null, this);
								}

								if ((clientChannel != null) && (clientChannel.isOpen())) {
									threadPool.execute(() -> {
										ReadWriteHandler handler = new ReadWriteHandler(clientChannel);
										ByteBuffer buffer = ByteBuffer.allocate(32);

										Map<String, Object> readInfo = new HashMap<>();
										readInfo.put("action", "read");
										readInfo.put("buffer", buffer);

										clientChannel.read(buffer, readInfo, handler);

									});
								}
							}

							@Override
							public void failed(Throwable exc, Object attachment) {
								// process error
							}
						});
				System.in.read();
			}
		}
	}
}
