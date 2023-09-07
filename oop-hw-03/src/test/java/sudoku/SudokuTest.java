package sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuTest {
	
	@Test
	void testSolve() {
		Sudoku s = new Sudoku(Sudoku.hardGrid);
		assertEquals(1, s.solve());
		
		Sudoku noSolutions = new Sudoku(Sudoku.stringsToGrid(
				"1 2 0 4 5 6 7 8 9\n",
				"0 3 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n"));
		assertEquals(0, noSolutions.solve());
		assertEquals("", noSolutions.getSolutionText());
		
		Sudoku empty = new Sudoku(Sudoku.stringsToGrid(
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n",
				"0 0 0 0 0 0 0 0 0\n"));
		assertEquals(Sudoku.MAX_SOLUTIONS, empty.solve());
		
		//remove one 7
		Sudoku hardGrid = new Sudoku(Sudoku.stringsToGrid(
				"3 0 0 0 0 0 0 8 0\n",
				"0 0 1 0 9 3 0 0 0\n",
				"0 4 0 7 8 0 0 0 3\n",
				"0 9 3 8 0 0 0 1 2\n",
				"0 0 0 0 4 0 0 0 0\n",
				"5 2 0 0 0 6 7 9 0\n",
				"6 0 0 0 2 1 0 4 0\n",
				"0 0 0 5 3 0 9 0 0\n",
				"0 3 0 0 0 0 0 5 1"));
		assertEquals(6, hardGrid.solve());
		
		//remove two 7s
		Sudoku hard2Grid = new Sudoku(Sudoku.stringsToGrid(
				"3 0 0 0 0 0 0 8 0\n",
				"0 0 1 0 9 3 0 0 0\n",
				"0 4 0 0 8 0 0 0 3\n",
				"0 9 3 8 0 0 0 1 2\n",
				"0 0 0 0 4 0 0 0 0\n",
				"5 2 0 0 0 6 7 9 0\n",
				"6 0 0 0 2 1 0 4 0\n",
				"0 0 0 5 3 0 9 0 0\n",
				"0 3 0 0 0 0 0 5 1"));
		assertEquals(31, hard2Grid.solve());
		
		//remove three 7s
		Sudoku hard3Grid = new Sudoku(Sudoku.stringsToGrid(
				"3 0 0 0 0 0 0 8 0\n",
				"0 0 1 0 9 3 0 0 0\n",
				"0 4 0 0 8 0 0 0 3\n",
				"0 9 3 8 0 0 0 1 2\n",
				"0 0 0 0 4 0 0 0 0\n",
				"5 2 0 0 0 6 0 9 0\n",
				"6 0 0 0 2 1 0 4 0\n",
				"0 0 0 5 3 0 9 0 0\n",
				"0 3 0 0 0 0 0 5 1"));
		assertEquals(Sudoku.MAX_SOLUTIONS, hard3Grid.solve());
		
		Sudoku solved = new Sudoku(Sudoku.stringsToGrid(
				"3 7 5 1 6 2 4 8 9\n",
				"8 6 1 4 9 3 5 2 7\n",
				"2 4 9 7 8 5 1 6 3\n",
				"4 9 3 8 5 7 6 1 2\n",
				"7 1 6 2 4 9 8 3 5\n",
				"5 2 8 3 1 6 7 9 4\n",
				"6 5 7 9 2 1 3 4 8\n",
				"1 8 2 5 3 4 9 7 6\n",
				"9 3 4 6 7 8 2 5 1"));
		assertEquals(1, solved.solve());
	}
	
	@Test
	void testExceptions() {
		assertThrows(RuntimeException.class, () -> new Sudoku(Sudoku.stringsToGrid(
				"3 7 0 0 0 0 0 8 0",
				"0 0 1 0 9 3 0 0 0",
				"0 4 0 7 8 0 0 0 3",
				"0 9 3 8 dfb 0 0 1 2",
				"0 0 0 0 4 0 0 0 0",
				"5 2 0 0 0 6 7 9 0",
				"6 0 0 0 vfb 1 0 4 0",
				"0 0 0 5 3 0 9 0 0",
				"0 3 0 0 0 0 0 5 1")));
		
		assertThrows(RuntimeException.class, () -> new Sudoku(Sudoku.stringsToGrid(
				"3 7 0 0 0 0 0 8 0",
				"0 0 1 0 9 3 0 0 0",
				"0 4 0 7 8 0 0 0 3",
				"0 9 3 8 0 0 0 1 2",
				"0 0 0 0 4 0 0 0 0",
				"5 2 0 0 0 6 7 9 0",
				"6 0 0 0 2 1 0 4 0",
				"0 0 0 5 3 0 9 0 0")));
		
		assertThrows(RuntimeException.class, () -> new Sudoku(Sudoku.stringsToGrid(
				"3 7 0 0 0 0 0 8 0",
				"0 0 1 0 9 3 0 0 0",
				"0 4 0 7 8 0 0 0 3",
				"0 9 3 8 0 0 0 1 2",
				"0 0 0 0 4 0 0 0 0",
				"5 2 0 0 0 6 7 9 0",
				"6 0 0 0 2 1 0 4 0",
				"6 0 0 0 2 1 0 4 0",
				"0 0 0 5 3 0 9 0 0",
				"0 3 0 0 0 0 0 5 1")));
		
	}
	
}
