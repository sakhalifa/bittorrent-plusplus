import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ReadWriteHandler implements
		CompletionHandler<Integer, Map<String, Object>> {

	private final AsynchronousSocketChannel clientChannel;

	private final StringBuilder messageBuilder;

	public ReadWriteHandler(AsynchronousSocketChannel clientChannel){
		this.clientChannel = clientChannel;
		this.messageBuilder = new StringBuilder();
	}
	@SneakyThrows
	@Override
	public void completed(
			Integer bytesRead, Map<String, Object> actionInfo) {
		if(bytesRead == -1)
			return;
		String action = (String) actionInfo.get("action");


		if ("read".equals(action)) {
			Thread.sleep(10 * 1000);
			System.out.println("Called read handler");
			System.out.println("Read " + bytesRead + " bytes!");
			ByteBuffer buffer = (ByteBuffer) actionInfo.get("buffer");
			buffer.flip();
			messageBuilder.append(StandardCharsets.ISO_8859_1.decode(buffer));
			buffer.flip();
			if(buffer.get(bytesRead - 1) == '\n'){
				// Finished reading a protocol message
				String wholeMessage = messageBuilder.toString();
				System.out.println("Read the message :'" + wholeMessage.trim() + "'");
				actionInfo.put("action", "write");

				clientChannel.write(StandardCharsets.ISO_8859_1.encode("> " + wholeMessage), actionInfo, this);
				buffer.clear();
			}else{
				// Still reading that message...
				actionInfo.put("action", "read");
				actionInfo.put("buffer", buffer);

				clientChannel.read(buffer, actionInfo, this);
			}

		} else if ("write".equals(action)) {
			ByteBuffer buffer = ByteBuffer.allocate(32);
			System.out.println("Called write handler");
			System.out.println("Wrote " + bytesRead + " bytes ?");

			actionInfo.put("action", "read");
			actionInfo.put("buffer", buffer);
			messageBuilder.setLength(0);
			clientChannel.read(buffer, actionInfo, this);
		}
	}

	@Override
	public void failed(Throwable exc, Map<String, Object> attachment) {
		//
	}
}