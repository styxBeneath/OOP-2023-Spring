// Board.java

import java.util.Arrays;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int[] widths;
	private int[] heights;
	private boolean[][] grid;
	private int maxHeight;
	
	//backups
	private int[] xWidths;
	private int[] xHeights;
	private boolean[][] xGrid;
	private int xMaxHeight;
	
	private boolean DEBUG = true;
	boolean committed;
	
	public static final String INVALID_STATE_MESSAGE = "State of the grid is invalid";
	public static final String INVALID_DROPPING_MESSAGE = "Can't drop out of bounds";
	public static final String PLACE_COMMIT_PROBLEM_MESSAGE = "Place commit problem";
	
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		widths = new int[height];
		heights = new int[width];
		grid = new boolean[width][height];
		xWidths = new int[height];
		xHeights = new int[width];
		xGrid = new boolean[width][height];
		committed = true;
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return grid.length;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return grid[0].length;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {
		return maxHeight;
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			int[] widths = new int[getHeight()];
			int[] heights = new int[getWidth()];
			int maxHeight = 0;
			
			for (int x = 0; x < getWidth(); x++) {
				for (int y = 0; y < getHeight(); y++) {
					if (grid[x][y]) {
						heights[x] = y + 1;
						widths[y]++;
					}
				}
				maxHeight = Math.max(maxHeight, heights[x]);
			}
			
			if (!Arrays.equals(widths, this.widths)
					|| !Arrays.equals(heights, this.heights)
					|| maxHeight != this.maxHeight) {
				throw new RuntimeException(INVALID_STATE_MESSAGE);
			}
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		if (x < 0 || (x + skirt.length) > getWidth()) {
			throw new RuntimeException(INVALID_DROPPING_MESSAGE);
		}
		int result = 0;
		for (int i = 0; i < piece.getWidth(); i++) {
			int possibleHeight = getColumnHeight(x + i) - skirt[i];
			result = Math.max(result, possibleHeight);
		}
		return result;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		 return widths[y];
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		return isOutOfBounds(x,y) || grid[x][y];
	}
	
	private boolean isOutOfBounds(int x, int y) {
		return x < 0 || x >= getWidth() || y < 0 || y >= getHeight();
	}
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException(PLACE_COMMIT_PROBLEM_MESSAGE);
		
		int result = PLACE_OK;
		TPoint[] body = piece.getBody();
		
		for (TPoint point : body) {
			if (getGrid(x + point.x, y + point.y)) {
				result = isOutOfBounds(x + point.x, y + point.y)
						? PLACE_OUT_BOUNDS
						: PLACE_BAD;
				break;
			}
			
			widths[y + point.y]++;
			heights[x + point.x] = Math.max(heights[x + point.x], y + point.y + 1);
			grid[x + point.x][y + point.y] = true;
			maxHeight = Math.max(getMaxHeight(), heights[x + point.x]);
			
			if (getRowWidth(y + point.y) == getWidth()) {
				result = PLACE_ROW_FILLED;
			}
		}
		
		if (result == PLACE_ROW_FILLED || result == PLACE_OK) {
			sanityCheck();
		}
		committed = false;
		return result;
	}
	
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		int rowsCleared = 0;
		for (int y = 0; y < getMaxHeight(); y++) {
			if (widths[y] == getWidth()) {
				moveDown(y);
				y--;
				rowsCleared++;
			}
		}
		if (rowsCleared > 0) {
			committed = false;
		}
		sanityCheck();
		return rowsCleared;
	}
	
	private void moveDown(int i) {
		for (int y = i + 1; y <= getMaxHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				grid[x][y - 1] = y < getHeight() && getGrid(x, y);
			}
			widths[y - 1] = y < getHeight() ? widths[y] : 0;
		}
		
		int mxHeight = 0;
		for (int x = 0; x < getWidth(); x++) {
			heights[x] = 0;
			for (int y = 0; y < getMaxHeight(); y++) {
				if (getGrid(x, y)) {
					heights[x] = y + 1;
					mxHeight = Math.max(mxHeight, y + 1);
				}
			}
		}
		maxHeight = mxHeight;
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if (committed) {
			return;
		}
		swapCurrentAndBackup();
		commit();
		sanityCheck();
	}
	
	private void swapCurrentAndBackup() {
		//swap widths
		int[] widthsTmp = xWidths;
		xWidths = widths;
		widths = widthsTmp;
		
		//swap heights
		int[] heightsTmp = xHeights;
		xHeights = heights;
		heights = heightsTmp;
		
		//swap grids
		boolean[][] gridTmp = xGrid;
		xGrid = grid;
		grid = gridTmp;
		
		//swap maxHeights
		int maxHeightTmp = xMaxHeight;
		xMaxHeight = maxHeight;
		maxHeight = maxHeightTmp;
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		if (committed) {
			return;
		}
		doBackup();
		committed = true;
	}
	
	private void doBackup() {
		System.arraycopy(widths, 0, xWidths, 0, widths.length);
		System.arraycopy(heights, 0, xHeights, 0, heights.length);
		for (int i = 0; i < grid.length; i++) {
			System.arraycopy(grid[i], 0, xGrid[i], 0, grid[i].length);
		}
		xMaxHeight = maxHeight;
	}
	
	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility)
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = getHeight()-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<getWidth(); x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<getWidth()+2; x++) buff.append('-');
		return(buff.toString());
	}
}


