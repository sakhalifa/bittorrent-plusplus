package commands;

import server.Counter;

public class PrintCounterCommand implements ICommand {
	@Override
	public String apply(Counter counter) {
		return "" + counter.getCounter();
	}

	@Override
	public String toString(){
		return "print";
	}
}
