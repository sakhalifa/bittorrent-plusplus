package commands;

import lombok.Getter;
import server.Counter;

@Getter
public class IncrCounterCommand implements ICommand {
	private final int value;
	public IncrCounterCommand(int value){
		this.value = value;
	}

	@Override
	public String apply(Counter counter) {
		counter.incrementCounter(value);
		return "OK";
	}

	@Override
	public String toString(){
		return "increment " + value;
	}
}
