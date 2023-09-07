// HW1 2-d array Problems
// CharGrid encapsulates a 2-d grid of chars and supports
// a few operations on the grid.

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;

public class CharGrid {
	private char[][] grid;
	
	/**
	 * Constructs a new CharGrid with the given grid.
	 * Does not make a copy.
	 *
	 * @param grid
	 */
	public CharGrid(@NotNull char[][] grid) {
		this.grid = grid;
	}
	
	/**
	 * Returns the area for the given char in the grid. (see handout).
	 *
	 * @param ch char to look for
	 * @return area for given char
	 */
	public int charArea(char ch) {
		if (grid.length == 0 || grid[0].length == 0) {
			return 0;
		}
		
		// Rectangle points
		int minX = grid[0].length;
		int maxX = -1;
		int minY = grid.length;
		int maxY = -1;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == ch) {
					minX = Math.min(j, minX);
					minY = Math.min(i, minY);
					maxX = Math.max(j, maxX);
					maxY = Math.max(i, maxY);
				}
			}
		}
		
		if (maxX == -1) { // The character is not presented in the grid
			return 0;
		}
		
		return (maxX - minX + 1) * (maxY - minY + 1);
	}
	
	//region countPlus
	/**
	 * Returns the count of '+' figures in the grid (see handout).
	 *
	 * @return number of + in grid
	 */
	public int countPlus() {
		if (grid.length == 0 || grid[0].length == 0) {
			return 0;
		}
		
		int res = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (validArms(armLength(i, j, -1, 0),
						armLength(i, j, 1, 0),
						armLength(i, j, 0, -1),
						armLength(i, j, 0, 1))) {
					res++;
				}
			}
		}
		return res;
	}
	
	// Checks whether all arms have the same length greater than 1
	private boolean validArms(int leftArm, int rightArm, int lowerArm, int upperArm) {
		return leftArm > 1
				&& leftArm == rightArm
				&& rightArm == upperArm
				&& upperArm == lowerArm;
	}
	
	// Calculates the arm length
	// x,y - coordinates of the center
	// xDir:-1, yDir:0 -- left arm
	// xDir:1, yDir:0 -- right arm
	// xDir:0, yDir:-1 -- upper arm
	// xDir:0, yDir:1 -- lower arm
	private int armLength(int y, int x, int xDir, int yDir) {
		int res = 1;
		if (yDir == 0) {
			for (int i = 1; x + xDir * i < grid[0].length && x + xDir * i > -1; i++) {
				if (grid[y][x] != grid[y][x + xDir * i]) {
					break;
				}
				res++;
			}
		} else {
			for (int i = 1; y + yDir * i < grid.length && y + yDir * i > -1; i++) {
				if (grid[y][x] != grid[y + yDir * i][x]) {
					break;
				}
				res++;
			}
		}
		return res;
	}
	//endregion
	
}
