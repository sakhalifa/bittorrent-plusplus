package fr.ystat.tracker.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ISendableCommand;
import fr.ystat.tracker.criterions.ICriterion;
import fr.ystat.util.SerializationUtils;

import java.util.List;

@CommandAnnotation(name = "look")
public class LookCommand implements ISendableCommand {

	private final List<ICriterion> criterions;

	public LookCommand(List<ICriterion> criterions){
		this.criterions = criterions;
	}

	@Override
	public String serialize() {
		return String.format("look %s", SerializationUtils.listToString(criterions));
	}
}
