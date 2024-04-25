package fr.ystat.files;

import fr.ystat.util.SerializationUtils;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicBitSet implements Cloneable {
	private final AtomicIntegerArray array;
	@Getter
	private final int length;

	public AtomicBitSet(FileProperties properties){
		this((int) ((properties.getSize() + properties.getPieceSize() - 1) / properties.getPieceSize()));
	}

	public AtomicBitSet(int length) {
		this.length = length;
		int intLength = (length + 31) >>> 5; // unsigned / 32
		array = new AtomicIntegerArray(intLength);
	}

	public AtomicBitSet(ByteBuffer bb, int length) {
		this(length);
		if(bb.limit() != 4*array.length())
			throw new IllegalArgumentException("Invalid ByteBuffer size!");
		int i = 0;
		while(bb.position() != bb.limit())
			array.set(i++, bb.getInt());
	}

	public AtomicBitSet(AtomicBitSet bitSet) {
		this(bitSet.getLength());
		for(int i = 0; i < array.length(); i++)
			array.set(i, bitSet.array.get(i));
	}

	public void fill() {
		for(int i = 0; i < array.length(); i++)
			array.set(i, -1);
	}

	public void empty(){
		for(int i = 0; i < array.length(); i++)
			array.set(i, 0);
	}

	public void set(long n) {
		int bit = 1 << n;
		int idx = (int) (n >>> 5);
		while (true) {
			int num = array.get(idx);
			int num2 = num | bit;
			if (num == num2 || array.compareAndSet(idx, num, num2))
				return;
		}
	}

	public boolean get(long n) {
		int bit = 1 << n;
		int idx = (int) (n >>> 5);
		int num = array.get(idx);
		return (num & bit) != 0;
	}

	public ByteBuffer toByteBuffer() {
		int byteArrLen = array.length() * 4;
		byte[] res = new byte[byteArrLen];
		for (int i = 0; i < array.length(); i++) {
			for (int byteI = 0; byteI < 4; byteI++) {
				res[byteI + i * 4] = (byte) ((array.get(i) >> 8 * (3-byteI)) & 0xFF);
			}
		}
		return ByteBuffer.wrap(res);
	}

	@Override
	public String toString() {
		return SerializationUtils.CHARSET.decode(toByteBuffer()).toString();
	}
}
