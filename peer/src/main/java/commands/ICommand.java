package commands;

import server.Counter;

import java.io.Serializable;

public interface ICommand extends Serializable {

	String apply(Counter counter);
}
