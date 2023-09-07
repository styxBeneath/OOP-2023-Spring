import junit.framework.TestCase;

import java.util.*;

import static org.junit.Assert.assertThrows;

public class AppearancesTest extends TestCase {
	// utility -- converts a string to a list with one
	// elem for each char.
	private List<String> stringToList(String s) {
		List<String> list = new ArrayList<String>();
		for (int i=0; i<s.length(); i++) {
			list.add(String.valueOf(s.charAt(i)));
			// note: String.valueOf() converts lots of things to string form
		}
		return list;
	}
	
	public void testSameCount1() {
		List<String> a = stringToList("abbccc");
		List<String> b = stringToList("cccbba");
		assertEquals(3, Appearances.sameCount(a, b));
	}
	
	public void testSameCount2() {
		// basic List<Integer> cases
		List<Integer> a = Arrays.asList(1, 2, 3, 1, 2, 3, 5);
		assertEquals(1, Appearances.sameCount(a, Arrays.asList(1, 9, 9, 1)));
		assertEquals(2, Appearances.sameCount(a, Arrays.asList(1, 3, 3, 1)));
		assertEquals(1, Appearances.sameCount(a, Arrays.asList(1, 3, 3, 1, 1)));
	}
	
	public void testSameCount3() {
		List<Boolean> l = Arrays.asList(true, false, true, true);
		assertEquals(2, Appearances.sameCount(l, Arrays.asList(true, true, true, false)));
		assertEquals(0, Appearances.sameCount(l, Arrays.asList(true, false, false)));
		assertEquals(1, Appearances.sameCount(l, Arrays.asList(true, false)));
		assertEquals(1, Appearances.sameCount(l, Arrays.asList(false, true, true, false, false, true)));
	}
	
	public void testSameCount4() {
		List<Character> l = Arrays.asList('a', 'x', 'c', 'k', 'a', 'U', 'd', 'L', 'a');
		assertEquals(0, Appearances.sameCount(l, Arrays.asList('a', 'a', 'a', 'a', 'a')));
		assertEquals(2, Appearances.sameCount(l, Arrays.asList('a', 'a', 'a', 'b', 'd')));
		assertEquals(3, Appearances.sameCount(l, Arrays.asList('a', 'k', 'b', 'c', 'd')));
		assertEquals(2, Appearances.sameCount(l, Arrays.asList('m', 'k', 'w', 'c', 'n')));
	}
	
	public void testNullValues() {
		assertThrows(IllegalArgumentException.class, () -> Appearances.sameCount(null, new ArrayList<>()));
		assertThrows(IllegalArgumentException.class, () -> Appearances.sameCount(new ArrayList<>(), null));
		assertThrows(IllegalArgumentException.class, () -> Appearances.sameCount(null, null));
	}
	
}
