package fr.ystat.files;

import fr.ystat.util.SerializationUtils;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicBitSet {
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

	public ExistingBitSetIterator existingIterator(){
		return new ExistingBitSetIterator(this);
	}

	@Override
	public String toString() {
		return SerializationUtils.CHARSET.decode(toByteBuffer()).toString();
	}

	public NotExistingBitSetIterator notExistingIterator() {
		return new NotExistingBitSetIterator(this);
	}

	public static class ExistingBitSetIterator implements Iterator<Long>, Iterable<Long>{
		private long cursorPosition;
		private final AtomicBitSet bs;


		private void moveToFirstExisting(){
			int arrayIdx = (int) (cursorPosition / 32);
			while(arrayIdx < bs.array.length() && bs.array.get(arrayIdx) == 0){
				arrayIdx++;
			}
			long cursor = Math.max(arrayIdx * 32L, cursorPosition + 1);
			while(cursor < bs.getLength() && !bs.get(cursor))
				cursor++;
			cursorPosition = cursor;
		}

		public ExistingBitSetIterator(AtomicBitSet bs){
			cursorPosition = 0;
			this.bs = bs;
			this.moveToFirstExisting();
		}

		@Override
		public boolean hasNext() {
			return cursorPosition < bs.getLength();
		}

		@Override
		public Long next() {
			long cursor = cursorPosition++;
			this.moveToFirstExisting();
			return cursor;
		}

		@Override
		public Iterator<Long> iterator() {
			return this;
		}
	}

	public static class NotExistingBitSetIterator implements Iterator<Long>, Iterable<Long>{
		private long cursorPosition;
		private final AtomicBitSet bs;


		private void moveToFirstNotExisting(){
			int arrayIdx = (int) (cursorPosition / 32);
			while(arrayIdx < bs.array.length() && bs.array.get(arrayIdx) == -1){
				arrayIdx++;
			}
			long cursor = Math.max(arrayIdx * 32L, cursorPosition + 1);
			while(cursor < bs.getLength() && bs.get(cursor))
				cursor++;
			cursorPosition = cursor;
		}

		public NotExistingBitSetIterator(AtomicBitSet bs){
			cursorPosition = 0;
			this.bs = bs;
			this.moveToFirstNotExisting();
		}

		@Override
		public boolean hasNext() {
			return cursorPosition < bs.getLength();
		}

		@Override
		public Long next() {
			long cursor = cursorPosition;
			this.moveToFirstNotExisting();
			return cursor;
		}

		@Override
		public Iterator<Long> iterator() {
			return this;
		}
	}
}
