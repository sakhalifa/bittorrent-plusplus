package commands;

import commands.server.CommandParsers;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAnnotation {
    String value();
    CommandParsers parser() default CommandParsers.DEFAULT_PARSER;
}


