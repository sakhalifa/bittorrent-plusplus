package fr.ystat.commands;

public interface ISendableCommand {
	default String serialize() {
		return getClass().getAnnotation(CommandAnnotation.class).name();
	}
}
