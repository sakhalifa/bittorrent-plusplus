package fr.ystat.commands;

import fr.ystat.commands.server.CommandAnnotationCollector;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAnnotation {
    String value();
    Class<? extends ICommandParser> parser() default CommandAnnotationCollector.DefaultCommandParser.class;
}


