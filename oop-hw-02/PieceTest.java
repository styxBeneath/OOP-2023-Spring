import junit.framework.TestCase;

import java.util.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest extends TestCase {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;

	protected void setUp() throws Exception {
		super.setUp();
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
	}
	
	// Here are some sample tests to get you started
	
	public void testSampleSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());
		
		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
	}
	
	
	// Test the skirt returned by a few pieces
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
	}
	
	public void testFastRotation() {
		Piece[] pieces = Piece.getPieces();
		assertEquals(7, pieces.length);
	
		Random rd = new Random();
		Piece rdPiece = pieces[rd.nextInt(pieces.length)];
		
		Piece normalRotated = rdPiece.computeNextRotation();
		Piece fastRotated = rdPiece.fastRotation();
		
		assertTrue(fastRotated.equals(normalRotated));
		
	}
	
	public void testWrongPieceFormat() {
		String[] pieces = new String[] {"1 2 3 4 4",
				"1 2 3 2 1 2 343 213 13",
				"1 2 free misha dg we 441 2 6",
				"sad 312 21b21 hkb a 2"};
		int exceptionNum = 0;
		for (int i = 0; i < pieces.length; i++) {
			try {
				new Piece(pieces[i]);
			} catch (RuntimeException e) {
				exceptionNum++;
			}
		}
		
		assertEquals(pieces.length, exceptionNum);
	}
	
	public void testEquals() {
		String piece1 = "0 1	1 1  1 0  2 0";
		String piece2 = "1 1  2 0  0 1  1 0";
		assertEquals(new Piece(piece1), new Piece(piece2));
		
		piece1 = "0 0	0 1  0 2  0 3";
		piece2 = "0 3  0 2  0 1  0 0";
		assertEquals(new Piece(piece1), new Piece(piece2));
		
		piece1 = "0 1	1 1  1 0  2 0";
		piece2 = "1 1  2 0  0 1";
		assertFalse(new Piece(piece1).equals(new Piece(piece2)));
		
		piece1 = "0 1	1 1  1 0  2 0";
		piece2 = "1 1  2 0  0 1  3 0";
		assertFalse(new Piece(piece1).equals(new Piece(piece2)));
		
		Piece l = new Piece(Piece.STICK_STR);
		Piece lTurn1 = l.computeNextRotation();
		Piece lTurn2 = lTurn1.computeNextRotation();
		assertFalse(l.equals(lTurn1));
		assertEquals(l, lTurn2);
		
	}
	
}
