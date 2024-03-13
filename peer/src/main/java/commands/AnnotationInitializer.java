package commands;


import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;

public class AnnotationInitializer {

    public static void main(String[] args){
        HashMap<String, Class<ICommand>> commandsMap =  AnnotationInitializer.initialize();
        commandsMap.forEach((it, ignored) -> System.out.println(it));
    }
    public static HashMap<String, Class<ICommand>> initialize() {
        HashMap<String, Class<ICommand>> namesToCommands = new HashMap<>();

        Reflections reflections = new Reflections("commands", Scanners.TypesAnnotated);
        for (var clazz :  reflections.getTypesAnnotatedWith(CommandAnnotation.class)) {
            try {
                String commandName = clazz.getAnnotation(CommandAnnotation.class).value();
                if (namesToCommands.containsKey(commandName)) {
                    throw new RuntimeException(String.format("Conflicting commands names ! (%s)", namesToCommands));
                }
                namesToCommands.put(commandName, (Class<ICommand>) clazz);
            } catch (ClassCastException ignored) {}
        }

        return namesToCommands;
    }
}
