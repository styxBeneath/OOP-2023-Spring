package sudoku;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;


public class SudokuFrame extends JFrame {
	private JTextArea source;
	private JTextArea solution;
	private JCheckBox autoCheck;
	
	public SudokuFrame() {
		super("Sudoku Solver");
		
		setLayout(new BorderLayout(4, 4));
		createTextAreas();
		createControls();
		
		// Could do this:
		// setLocationByPlatform(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	private void createControls() {
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JButton checkButton = new JButton("Check");
		checkButton.addActionListener(e -> solve());
		
		autoCheck = new JCheckBox("Auto Check");
		autoCheck.setSelected(true);
		autoCheck.addActionListener(e -> {
			if (((JCheckBox) e.getSource()).isSelected()) {
				solve();
			}
		});
		
		panel.add(checkButton);
		panel.add(autoCheck);
	}
	
	private void createTextAreas() {
		source = new JTextArea(15, 20);
		source.setBorder(new TitledBorder("Puzzle"));
		
		source.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (autoCheck.isSelected()) {
					solve();
				}
			}
			
			public void insertUpdate(DocumentEvent e) {
				if (autoCheck.isSelected()) {
					solve();
				}
			}
			
			public void removeUpdate(DocumentEvent e) {
				if (autoCheck.isSelected()) {
					solve();
				}
			}
		});
		
		solution = new JTextArea(15, 20);
		solution.setBorder(new TitledBorder("Solution"));
		solution.setEditable(false);
		
		add(source, BorderLayout.WEST);
		add(solution, BorderLayout.EAST);
	}
	
	private void solve() {
		try {
			Sudoku sudoku = new Sudoku(Sudoku.textToGrid(source.getText()));
			int numSolutions = sudoku.solve();
			if (numSolutions > 0) {
				solution.setText(sudoku.getSolutionText());
				solution.append("solutions: " + numSolutions + "\n");
				solution.append("elapsed: " + sudoku.getElapsed() + "ms\n");
			} else {
				solution.setText("No solutions found\n");
			}
		} catch (Exception e) {
			solution.setText("Parsing error\n");
		}
	}
	
	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

}
