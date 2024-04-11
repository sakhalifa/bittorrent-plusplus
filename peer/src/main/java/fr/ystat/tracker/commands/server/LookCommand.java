package fr.ystat.tracker.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.server.Counter;
import fr.ystat.tracker.criterions.ICriterion;
import fr.ystat.util.SerializationUtils;

import java.util.List;

@CommandAnnotation(name = "look")
public class LookCommand implements ICommand {

	private final List<ICriterion> criterions;

	public LookCommand(List<ICriterion> criterions){
		this.criterions = criterions;
	}

	@Override
	public String apply(Counter counter) throws CommandException {
		throw new UnsupportedOperationException("Cannot use apply on look command");
	}

	@Override
	public String serialize() {
		return String.format("look %s", SerializationUtils.listToString(criterions));
	}
}
