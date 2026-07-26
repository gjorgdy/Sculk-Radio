package nl.gjorgdy.sculk_radio.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public abstract class SetUtils {

	public static <T> List<T> toList(Set<T> set) {
		return new ArrayList<>(set);
	}

	public static <T> Stream<T> flatten(Set<Set<? extends T>> set) {
		return set.stream().flatMap(Collection::stream);
	}

}
