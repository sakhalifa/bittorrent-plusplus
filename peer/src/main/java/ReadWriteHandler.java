import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ReadWriteHandler implements
		CompletionHandler<Integer, Map<String, Object>> {

	private final AsynchronousSocketChannel clientChannel;

	private StringBuilder messageBuilder;

	public ReadWriteHandler(AsynchronousSocketChannel clientChannel){
		this.clientChannel = clientChannel;
		this.messageBuilder = new StringBuilder();
	}
	@Override
	public void completed(
			Integer result, Map<String, Object> attachment) {
		Map<String, Object> actionInfo = attachment;
		String action = (String) actionInfo.get("action");


		if ("read".equals(action)) {
			System.out.println("Called read handler");
			System.out.println("Read " + result + " bytes ?");
			ByteBuffer buffer = (ByteBuffer) actionInfo.get("buffer");
			buffer.flip();
			messageBuilder.append(StandardCharsets.ISO_8859_1.decode(buffer));
			buffer.flip();
			if(buffer.get(result - 1) == '\n'){
				// Finished reading a protocol message
				actionInfo.put("action", "write");

				clientChannel.write(StandardCharsets.ISO_8859_1.encode(messageBuilder.toString()), actionInfo, this);
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
			System.out.println("Wrote " + result + " bytes ?");

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