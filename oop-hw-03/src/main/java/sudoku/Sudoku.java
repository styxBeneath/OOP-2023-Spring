package sudoku;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	//region starter grids
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	//endregion
	
	//region constants
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	//endregion
	
	//region utility methods
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}
	//endregion
	
	//region main
	
	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	//endregion
	
	//region sudoku class
	
	private final List<Spot> sortedSpots;
	private final int[][] board;
	private String solution;
	private long elapsed;
	
	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		this.sortedSpots = getSortedSpots(ints);
		this.board = deepCopy(ints);
		this.solution = "";
	}
	
	public static int[][] deepCopy(int[][] original) {
		int[][] copy = new int[original.length][];
		for (int i = 0; i < original.length; i++) {
			copy[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return copy;
	}
	
	private List<Spot> getSortedSpots(int[][] initialBoard) {
		List<Spot> spots = new ArrayList<>();
		for (int row = 0; row < initialBoard.length; row++) {
			for (int col = 0; col < initialBoard[0].length; col++) {
				if (initialBoard[row][col] != 0) {
					continue;
				}
				Spot spot = new Spot(initialBoard[row][col], row, col);
				spot.setAvailableDigits(getAvailableDigits(initialBoard, row, col));
				spots.add(spot);
			}
		}
		spots.sort(Comparator.comparingInt(s -> s.availableDigits.size()));
		return spots;
	}
	
	private Set<Integer> getAvailableDigits(int[][] initial, int row, int col) {
		Set<Integer> available = new HashSet<>();
		if (initial[row][col] == 0) {
			for (int num = 1; num <= 9; num++) {
				if (isValid(initial, row, col, num)) {
					available.add(num);
				}
			}
		}
		return available;
	}
	
	private static boolean isValid(int[][] board, int row, int col, int num) {
		for (int i = 0; i < 9; i++) {
			if (board[row][i] == num) return false;
			if (board[i][col] == num) return false;
			int boxRow = 3 * (row / 3) + i / 3;
			int boxCol = 3 * (col / 3) + i % 3;
			if (board[boxRow][boxCol] == num) return false;
		}
		return true;
	}
	
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		AtomicInteger numSolutions = new AtomicInteger(0);
		long start = System.currentTimeMillis();
		solveSudoku(0, numSolutions);
		long finish = System.currentTimeMillis();
		this.elapsed = finish - start;
		return numSolutions.get();
	}
	
	private void solveSudoku(int index, AtomicInteger numSolutions) {
		// Base case: all spots have been filled, solution found
		if (index == sortedSpots.size()) {
			if (solution.isEmpty()) {
				this.solution = getSolution(board);
			}
			numSolutions.getAndIncrement();
			return;
		}
		
		if (numSolutions.get() == MAX_SOLUTIONS) {
			return;
		}
		
		Spot spot = sortedSpots.get(index);
		for (int digit : spot.getAvailableDigits()) {
			// Try placing each available digit in the current spot
			int row = spot.getRow();
			int col = spot.getCol();
			if (isValid(board, row, col, digit)) {
				board[row][col] = digit;
				solveSudoku(index + 1, numSolutions);
				board[row][col] = 0; // Backtrack
			}
		}
	}
	
	private String getSolution(int[][] board) {
		StringBuilder solutionBuilder = new StringBuilder();
		for (int[] row : board) {
			for (int val : row) {
				solutionBuilder.append(val).append(" ");
			}
			solutionBuilder.append("\n");
		}
		return solutionBuilder.toString();
	}
	public String getSolutionText() {
		return solution;
	}
	
	public long getElapsed() {
		return this.elapsed;
	}
	
	//endregion
	
	//region spot class
	private static class Spot {
		private int digit;
		private final int row;
		private final int col;
		private Set<Integer> availableDigits;
		
		public Spot(int digit, int row, int col) {
			this.digit = digit;
			this.row = row;
			this.col = col;
			this.availableDigits = new HashSet<>();
		}
		
		public int getDigit() {
			return digit;
		}
		
		public void setDigit(int digit) {
			this.digit = digit;
		}
		
		public int getRow() {
			return row;
		}
		
		public int getCol() {
			return col;
		}
		
		public Set<Integer> getAvailableDigits() {
			return availableDigits;
		}
		
		public void setAvailableDigits(Set<Integer> availableDigits) {
			this.availableDigits = availableDigits;
		}
		
		@Override
		public String toString() {
			return "Spot{" +
					"digit=" + digit +
					", row=" + row +
					", col=" + col +
					", availableDigits=" + availableDigits.size() +
					'}';
		}
	}
	//endregion
}
