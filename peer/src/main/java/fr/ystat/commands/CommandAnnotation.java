package fr.ystat.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAnnotation {
    String name();
    Class<? extends ICommandParser> parser() default CommandAnnotationCollector.DefaultCommandParser.class;
}


