package nl.gjorgdy.sculk_radio.utils;

import java.util.*;

public abstract class SetUtils {

	public static <T> List<T> toList(Set<T> set) {
		return new ArrayList<>(set);
	}

	public static <T> Set<T> toSet(List<T> list) {
		return new HashSet<>(list);
	}
}
