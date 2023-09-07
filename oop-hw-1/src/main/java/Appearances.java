import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Appearances {
	
	/**
	 * Returns the number of elements that appear the same number
	 * of times in both collections. Static method. (see handout).
	 * @return number of same-appearance elements
	 */
	public static <T> int sameCount(@NotNull Collection<T> a, @NotNull Collection<T> b) {
		AtomicInteger res = new AtomicInteger(); // The result is calculated in a stream, so the atomic value is needed
		Map<T, Integer> freqA = new HashMap<>(); // Element frequencies in the first collection
		Map<T, Integer> freqB = new HashMap<>(); // Element frequencies in the second collection
		a.forEach(entity -> freqA.put(entity, freqA.getOrDefault(entity, 0) + 1)); // Fill the first frequency map
		b.forEach(entity -> freqB.put(entity, freqB.getOrDefault(entity, 0) + 1)); // Fill the first frequency map
		freqA.forEach((key, value) -> {
			if (Objects.equals(value, freqB.get(key))) {
				res.getAndIncrement();
			}
		});
		return res.get();
	}
	
}
