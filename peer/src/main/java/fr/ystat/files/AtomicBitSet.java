package fr.ystat.files;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicBitSet {
	private final AtomicIntegerArray array;
	@Getter
	private final int length;

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
		return StandardCharsets.ISO_8859_1.decode(toByteBuffer()).toString();
	}
}
