package fr.ystat.server;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
	private final AtomicInteger counter;

	public Counter(){
		counter = new AtomicInteger(0);
	}

	public void incrementCounter(int value){
		counter.addAndGet(value);
	}

	public int getCounter(){
		return counter.get();
	}
}
