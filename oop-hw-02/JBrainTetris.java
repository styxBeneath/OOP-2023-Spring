import javax.swing.*;
import java.awt.*;

public class JBrainTetris extends JTetris {
	private final Brain brain;
	private Brain.Move brainMove;
	private boolean brainMoveCalculated;
	
	private JCheckBox brainActive;
	private JCheckBox animateFall;
	private JLabel okLabel;
	private JSlider adversary;
	
	/**
	 * Creates a new JTetris where each tetris square
	 * is drawn with the given number of pixels.
	 *
	 * @param pixels
	 */
	JBrainTetris(int pixels) {
		super(pixels);
		brain = new DefaultBrain();
	}
	
	@Override
	public void tick(int verb) {
		if (!gameOn) return;
		
		if (currentPiece != null) {
			board.undo();	// remove the piece from its old position
		}
		
		if (brainActive.isSelected()) {
			calculateBrainMove();
		}
		
		// Sets the newXXX ivars
		computeNewPosition(verb);
		
		// try out the new position (rolls back if it doesn't work)
		int result = setCurrent(newPiece, newX, newY);
		
		// if row clearing is going to happen, draw the
		// whole board so the green row shows up
		if (result ==  Board.PLACE_ROW_FILLED) {
			repaint();
		}
		
		
		boolean failed = (result >= Board.PLACE_OUT_BOUNDS);
		
		// if it didn't work, put it back the way it was
		if (failed) {
			if (currentPiece != null) board.place(currentPiece, currentX, currentY);
			repaintPiece(currentPiece, currentX, currentY);
		}
		
		/*
		 How to detect when a piece has landed:
		 if this move hits something on its DOWN verb,
		 and the previous verb was also DOWN (i.e. the player was not
		 still moving it),	then the previous position must be the correct
		 "landed" position, so we're done with the falling of this piece.
		*/
		if (failed && verb==DOWN && !moved) {	// it's landed
			brainMoveCalculated = false;
			int cleared = board.clearRows();
			if (cleared > 0) {
				// score goes up by 5, 10, 20, 40 for row clearing
				// clearing 4 gets you a beep!
				switch (cleared) {
					case 1: score += 5;	 break;
					case 2: score += 10;  break;
					case 3: score += 20;  break;
					case 4: score += 40; Toolkit.getDefaultToolkit().beep(); break;
					default: score += 50;  // could happen with non-standard pieces
				}
				updateCounters();
				repaint();	// repaint to show the result of the row clearing
			}
			
			
			// if the board is too tall, we've lost
			if (board.getMaxHeight() > board.getHeight() - TOP_SPACE) {
				stopGame();
			}
			// Otherwise add a new piece and keep playing
			else {
				addNewPiece();
			}
		}
		
		// Note if the player made a successful non-DOWN move --
		// used to detect if the piece has landed on the next tick()
		moved = (!failed && verb!=DOWN);
	}
	
	private void calculateBrainMove() {
		if (!brainMoveCalculated) {
			brainMoveCalculated = true;
			brainMove = brain.bestMove(board, currentPiece, board.getHeight() - currentPiece.getHeight(), null);
		}
		
		board.undo();
		if (brainMove == null) {
			stopGame();
			return;
		}
		if (!animateFall.isSelected()) {
			currentX = brainMove.x;
			currentY = brainMove.y;
			currentPiece = brainMove.piece;
			return;
		}
		if (currentPiece != brainMove.piece) {
			currentPiece = currentPiece.fastRotation();
			return;
		}
		if (currentX < brainMove.x) {
			currentX++;
			return;
		}
		if (currentX > brainMove.x) {
			currentX--;
		}
	}
	
	private void updateCounters() {
		countLabel.setText("Pieces " + count);
		scoreLabel.setText("Score " + score);
	}
	
	@Override
	public JComponent createControlPanel() {
		JPanel panel = (JPanel) super.createControlPanel();
		addBrainComponents(panel);
		addAdversaryComponents(panel);
		return panel;
	}
	
	private void addBrainComponents(JPanel panel) {
		panel.add(Box.createVerticalStrut(50));
		panel.add(new JLabel("Brain:"));
		brainActive = new JCheckBox("Brain active");
		animateFall = new JCheckBox("Animate Fall");
		animateFall.setSelected(true);
		panel.add(brainActive);
		panel.add(animateFall);
	}
	
	private void addAdversaryComponents(JPanel panel) {
		panel.add(Box.createVerticalStrut(50));
		JPanel little = new JPanel();
		little.add(new JLabel("Adversary:"));
		adversary = new JSlider(0, 100, 0);
		adversary.setPreferredSize(new Dimension(100, 15));
		little.add(adversary);
		okLabel = new JLabel("");
		little.add(okLabel);
		panel.add(little);
	}
	
	@Override
	public Piece pickNextPiece() {
		int rand = random.nextInt(100);
		if (rand >= adversary.getValue()) {
			okLabel.setText("ok");
			return super.pickNextPiece();
		}
		
		int index = 0;
		double maxScore = 0;
		Brain.Move brainMove;
		for (int i = 0; i < pieces.length; i++) {
			brainMove = brain.bestMove(board, pieces[i], board.getHeight(), null);
			if(brainMove.score > maxScore) {
				maxScore = brainMove.score;
				index = i;
			}
		}
		
		okLabel.setText("*ok*");
		return pieces[index];
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		JTetris tetris = new JBrainTetris(16);
		JFrame frame = JBrainTetris.createFrame(tetris);
		frame.setVisible(true);
	}
	
}