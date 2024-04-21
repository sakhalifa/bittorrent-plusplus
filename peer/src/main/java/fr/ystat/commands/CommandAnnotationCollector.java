package fr.ystat.commands;

import fr.ystat.parser.exceptions.ParserException;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import static fr.ystat.commands.CommandAnnotationCollector.DefaultCommandParser.getCommandName;

public final class CommandAnnotationCollector {
	private static final HashMap<String, Class<IReceivableCommand>> namesToCommands = initialize();


	/**
	 * Ugly one I agree
	 * Collect all the classes with CommandAnnotation.
	 *
	 * @return HashMap of command names to their corresponding class command.
	 */
	private static HashMap<String, Class<IReceivableCommand>> initialize() {
		HashMap<String, Class<IReceivableCommand>> namesToCommands = new HashMap<>();

		Reflections reflections = new Reflections("fr.ystat", Scanners.TypesAnnotated);
		for (var clazz :  reflections.getTypesAnnotatedWith(CommandAnnotation.class)) {
			try {
//				System.out.println(clazz.getName());
				String commandName = clazz.getAnnotation(CommandAnnotation.class).name();
				if (namesToCommands.containsKey(commandName)) {
					throw new RuntimeException(String.format("Conflicting commands name (%s) in commands %s and %s.",
							commandName, clazz.getName(), namesToCommands.get(commandName).getName()));
				}
				namesToCommands.put(commandName, (Class<IReceivableCommand>) clazz);
			} catch (ClassCastException ignored) {}
		}
		return namesToCommands;
	}

	@SneakyThrows
	private static ICommandParser getCommandParser(String commandName) throws ParserException {
		Class<IReceivableCommand> commandClass = getCommandClass(commandName);
		var ctor = commandClass.getAnnotation(CommandAnnotation.class).parser().getDeclaredConstructor();
		ctor.setAccessible(true);
		return ctor.newInstance();
	}

	private static Class<IReceivableCommand> getCommandClass(String commandName) throws ParserException {
		Class<IReceivableCommand> commandClass = namesToCommands.get(commandName);
		if (commandClass == null) throw new ParserException(String.format("Unable to find command %s", commandName));
		return commandClass;
	}

	public static IReceivableCommand beginParsing(String input) throws ParserException {
		input = input.substring(0, input.length() - 1); // Only remove last \n. No trimming as it tends to break the command "have"
		String commandName = getCommandName(input);
//		System.out.println("Command name : " + commandName);
		return getCommandParser(commandName).parse(input);
	}

	public static class DefaultCommandParser implements ICommandParser {
		@Override
		public IReceivableCommand parse(String input) throws ParserException {
			String commandName = getCommandName(input);
			Class<IReceivableCommand> commandClass = getCommandClass(commandName);
			try {
				Constructor<IReceivableCommand> constructor = commandClass.getConstructor();

				return constructor.newInstance();

			} catch (NoSuchMethodException e) {
				throw new ParserException(String.format("Could not find constructor for command %s.", commandName));
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		static String getCommandName(String input){
			return input.split(" ")[0];
		}
	}
}
