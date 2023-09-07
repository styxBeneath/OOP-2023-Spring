
/*
 HW1 Taboo problem class.
 Taboo encapsulates some rules about what objects
 may not follow other objects.
 (See handout).
*/

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Taboo<T> {
	private final Map<T, Set<T>> notFollow;
	
	/**
	 * Constructs a new Taboo using the given rules (see handout.)
	 * @param rules rules for new Taboo
	 */
	public Taboo(@NotNull List<T> rules) {
		notFollow = new HashMap<>();
		initNotFollow(rules);
	}
	
	private void initNotFollow(List<T> rules) {
		for (int i = 0; i < rules.size() - 1; i++) {
			T rule1 = rules.get(i);
			T rule2 = rules.get(i + 1);
			if (rule1 != null && rule2 != null) {
				Set<T> rule1NoFollow = notFollow.getOrDefault(rule1, new HashSet<>());
				rule1NoFollow.add(rule2);
				notFollow.put(rule1, rule1NoFollow);
			}
		}
	}
	
	/**
	 * Returns the set of elements which should not follow
	 * the given element.
	 * @param elem
	 * @return elements which should not follow the given element
	 */
	public Set<T> noFollow(T elem) {
		return notFollow.getOrDefault(elem, Collections.emptySet());
	}
	
	/**
	 * Removes elements from the given list that
	 * violate the rules (see handout).
	 * @param list collection to reduce
	 */
	public void reduce(@NotNull List<T> list) {
		for (int i = 1; i < list.size(); i++) {
			T current = list.get(i);
			T previous = list.get(i - 1);
			if (notFollow.containsKey(previous) && notFollow.get(previous).contains(current)) {
				list.remove(i);
				i--;
			}
		}
	}
}
