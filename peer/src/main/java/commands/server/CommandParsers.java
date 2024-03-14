package commands.server;

import commands.CommandAnnotation;
import commands.ICommand;
import commands.ICommandParser;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import parser.exceptions.InvalidInputException;
import parser.exceptions.ParserException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public enum CommandParsers implements ICommandParser {

	//<editor-fold desc="DEFAULT_PARSER" defaultstate="collapsed">
	DEFAULT_PARSER {
		@Override
		public ICommand parse(String input) throws ParserException {
			String commandName = getCommandName(input);
			Class<ICommand> commandClass = getCommandClass(commandName);
			try {
				Constructor<ICommand> constructor = commandClass.getConstructor();

				return constructor.newInstance();

			} catch (NoSuchMethodException e) {
				throw new ParserException(String.format("Could not find constructor for command %s.", commandName));
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}


	},

	//</editor-fold>
	//<editor-fold desc="INCREMENT" defaultstate="collapsed">
	INCREMENT {
		@Override
		public ICommand parse(String input) throws ParserException {
			String[] splitted = input.split(" ");
			if (splitted.length < 2) {
				throw new InvalidInputException(input);
			}
			try {
				int val = Integer.parseInt(splitted[1]);
				return new IncrCounterCommand(val);
			} catch (NumberFormatException ex) {
				throw new InvalidInputException(String.format("%s (inside increment parser)", input));
			}
		}
	}
	//</editor-fold>
	;

	public static ICommand beginParsing(String input) throws ParserException {
		String commandName = getCommandName(input);
		return getCommandParser(commandName).parse(input);
	}

	private static final HashMap<String, Class<ICommand>> namesToCommands = initialize();

	/**
	 * Ugly one I agree
	 * Collect all the classes with CommandAnnotation.
	 *
	 * @return HashMap of command names to their corresponding class command.
	 */
	private static HashMap<String, Class<ICommand>> initialize() {
		HashMap<String, Class<ICommand>> namesToCommands = new HashMap<>();

		Reflections reflections = new Reflections("commands", Scanners.TypesAnnotated);
		for (var clazz :  reflections.getTypesAnnotatedWith(CommandAnnotation.class)) {
			try {
				String commandName = clazz.getAnnotation(CommandAnnotation.class).value();
				if (namesToCommands.containsKey(commandName)) {
					throw new RuntimeException(String.format("Conflicting commands name (%s) in commands %s and %s.",
							commandName, clazz.getName(), namesToCommands.get(commandName).getName()));
				}
				namesToCommands.put(commandName, (Class<ICommand>) clazz);
			} catch (ClassCastException ignored) {}
		}
		return namesToCommands;
	}

	protected static String getCommandName(String input){
		return input.split(" ")[0];
	}

	protected static Class<ICommand> getCommandClass(String commandName) throws ParserException {
		Class<ICommand> commandClass = namesToCommands.get(commandName);
		if (commandClass == null) throw new ParserException(String.format("Unable to find command %s", commandName));
		return commandClass;
	}

	protected static ICommandParser getCommandParser(String commandName) throws ParserException {
		Class<ICommand> commandClass = getCommandClass(commandName);
		return commandClass.getAnnotation(CommandAnnotation.class).parser();
	}
}
