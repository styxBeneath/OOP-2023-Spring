
// Test cases for CharGrid -- a few basic tests are provided.

import junit.framework.TestCase;

import static org.junit.Assert.assertThrows;

public class CharGridTest extends TestCase {
	
	public void testCharArea1() {
		char[][] grid = new char[][]{
				{'a', 'y', ' '},
				{'x', 'a', 'z'},
		};
		
		
		CharGrid cg = new CharGrid(grid);
		
		assertEquals(4, cg.charArea('a'));
		assertEquals(1, cg.charArea('z'));
	}
	
	
	public void testCharArea2() {
		char[][] grid = new char[][]{
				{'c', 'a', ' '},
				{'b', ' ', 'b'},
				{' ', ' ', 'a'}
		};
		
		CharGrid cg = new CharGrid(grid);
		
		assertEquals(6, cg.charArea('a'));
		assertEquals(3, cg.charArea('b'));
		assertEquals(1, cg.charArea('c'));
	}
	
	public void testCountPlus() {
		CharGrid cg = new CharGrid(new char[][]{
				{' ', ' ', 'p', ' ', 'd', ' ', ' ', ' ', 'd'},
				{' ', ' ', 'p', ' ', ' ', ' ', ' ', 'x', ' '},
				{'p', 'p', 'p', 'p', 'p', ' ', 'x', 'x', 'x'},
				{' ', ' ', 'p', ' ', ' ', 'y', ' ', 'x', ' '},
				{' ', ' ', 'p', ' ', 'y', 'y', 'y', 'z', ' '},
				{'z', 'z', 'z', 'z', 'z', 'y', 'z', 'z', 'z'},
				{' ', ' ', 'x', 'x', ' ', 'y', ' ', 'z', ' '}
		});
		assertEquals(3, cg.countPlus());
	}
	
	public void testNullValue() {
		assertThrows(IllegalArgumentException.class, () -> new CharGrid(null));
	}
}
