package commands;

import server.Counter;

@CommandAnnotation("hello")
public class HelloCommand implements ICommand{
	@Override
	public String apply(Counter counter) {
		return "hewwo";
	}

	@Override
	public String toString(){
		return "hello";
	}
}
