package server;

public class Counter {
	private volatile int counter;

	public synchronized void incrementCounter(int value){
		counter += value;
	}

	public synchronized int getCounter(){
		return counter;
	}
}
