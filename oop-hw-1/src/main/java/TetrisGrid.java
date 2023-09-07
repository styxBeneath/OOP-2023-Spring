//
// TetrisGrid encapsulates a tetris board and has
// a clearRows() capability.

import org.jetbrains.annotations.NotNull;

public class TetrisGrid {
	private boolean[][] grid;
	
	/**
	 * Constructs a new instance with the given grid.
	 * Does not make a copy.
	 * @param grid
	 */
	public TetrisGrid(@NotNull boolean[][] grid) {
		this.grid = grid;
	}
	
	
	/**
	 * Does row-clearing on the grid (see handout).
	 */
	public void clearRows() {
		boolean[][] newGrid = new boolean[grid.length][grid[0].length]; // default values - false
		int currentIndex = 0;
		for (int col = 0; col < grid[0].length; col++) {
			if (toKeep(col)) {
				for (int row = 0; row < grid.length; row++) {
					newGrid[row][currentIndex] = grid[row][col];
				}
				currentIndex++;
			}
		}
		
		this.grid = newGrid;
	}
	
	private boolean toKeep(int col) {
		for (boolean[] currentRow : grid) {
			if (!currentRow[col]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the internal 2d grid array.
	 * @return 2d grid array
	 */
	boolean[][] getGrid() {
		return this.grid;
	}
}
