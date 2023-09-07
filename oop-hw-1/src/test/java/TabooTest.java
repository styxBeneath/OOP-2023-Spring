// TabooTest.java
// Taboo class tests -- nothing provided.

import java.util.*;

import junit.framework.TestCase;

public class TabooTest extends TestCase {
	
	public void testNoFollow() {
		Taboo<Character> taboo = new Taboo<>(Arrays.asList('a', 'c', 'a', 'b'));
		assertEquals(new HashSet<>(Arrays.asList('b', 'c')), taboo.noFollow('a'));
		assertEquals(new HashSet<>(List.of('a')), taboo.noFollow('c'));
		assertEquals(Collections.emptySet(), taboo.noFollow('f'));
	}
	
	public void testNoFollowWithNullElement() {
		Taboo<Character> taboo = new Taboo<>(Arrays.asList('a', 'c', null, 'c', 'a', 'b'));
		assertEquals(Collections.emptySet(), taboo.noFollow('z'));
		assertEquals(new HashSet<>(List.of('a')), taboo.noFollow('c'));
		assertEquals(Collections.emptySet(), taboo.noFollow(null));
	}
	
	public void testReduce() {
		List<Character> list = new ArrayList<>(Arrays.asList('a', 'c', 'b', 'x', 'c', 'a'));
		Taboo<Character> taboo = new Taboo<>(Arrays.asList('a', 'c', 'a', 'b'));
		taboo.reduce(list);
		assertEquals(Arrays.asList('a', 'x', 'c'), list);
	}
	
	public void testReduceWIthNullList() {
		List<Character> list = Collections.emptyList();
		Taboo<Character> taboo = new Taboo<>(Arrays.asList('a', 'c', 'a', 'b'));
		taboo.reduce(list);
		assertEquals(Collections.emptyList(), list);
	}
}
