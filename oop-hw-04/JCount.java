// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;

public class JCount extends JPanel {
	public static final int DEFAULT_MAX_VALUE = 100000000;
	private Worker worker;
	
	public JCount() {
		// Set the thecount.JCount to use Box layout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JTextField maxValue = new JTextField(Integer.toString(DEFAULT_MAX_VALUE));
		JLabel currValue = new JLabel(Integer.toString(0));
		
		JButton start = new JButton("Start");
		JButton stop = new JButton("Stop");
		
		add(maxValue);
		add(currValue);
		add(start);
		add(stop);
		
		worker = new Worker(maxValue, currValue);
		
		start.addActionListener(e -> {
			if (!worker.isFinished()) {
				worker.interrupt();
			}
			worker.finish();
			worker = new Worker(maxValue, currValue);
			worker.start();
		});
		
		stop.addActionListener(e -> {
			if (!worker.isFinished()) {
				worker.finish();
			}
		});
		
		add(Box.createVerticalStrut(40));
	}
	
	static public void main(String[] args) {
		// Creates a frame with 4 JCounts in it.
		// (provided)
		JFrame frame = new JFrame("The Count");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private static class Worker extends Thread {
		
		private final JTextField maxValue;
		private final JLabel currValue;
		private volatile boolean finished;
		
		public Worker(JTextField maxValue, JLabel currValue) {
			this.maxValue = maxValue;
			this.currValue = currValue;
			this.finished = false;
		}
		
		public void finish() {
			this.finished = true;
		}
		
		public boolean isFinished() {
			return finished;
		}
		
		@Override
		public void run() {
			int max = Integer.parseInt(maxValue.getText());
			for (int i = 0; i <= max; i++) {
				if (isFinished()) {
					return;
				}
				if (isInterrupted()) {
					int value = i;
					SwingUtilities.invokeLater(() -> currValue.setText(Integer.toString(value)));
					break;
				}
				if (i % 10000 == 0) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ignore) {
					}
					int value = i;
					SwingUtilities.invokeLater(() -> currValue.setText(Integer.toString(value)));
				}
				if (i == max) {
					SwingUtilities.invokeLater(() -> currValue.setText(Integer.toString(max)));
				}
			}
		}
	}
}

