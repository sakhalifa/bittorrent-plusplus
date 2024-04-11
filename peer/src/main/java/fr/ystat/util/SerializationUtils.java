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

	public static String listToString(List<?> lst){ // TODO: Benchmark to see if it's really necessary with streamToString
		StringBuilder sb = new StringBuilder("[");
		for(var e : lst){
			sb.append(e.toString());
			sb.append(" ");
		}
		if(sb.length() > 1) // edge case if it's empty
			sb.setLength(sb.length() - 1); // remove last space
		sb.append("]");
		return sb.toString();
	}

	public static String streamToString(Stream<?> stream){
		return String.format("[%s]", stream.map(Object::toString).collect(Collectors.joining(" ")));
	}
}
