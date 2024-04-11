package fr.ystat.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerializationUtils {
	public static<T> Stream<T> filterByType(Stream<? super T> stream, Class<T> clazz){
		return stream
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}

	public static String streamToString(Stream<?> stream){
		return String.format("[%s]", stream.map(Object::toString).collect(Collectors.joining(" ")));
	}
}
