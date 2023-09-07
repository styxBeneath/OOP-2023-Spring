import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class WebView extends JFrame {
	
	//region static variables
	private static final String LINKS = "links.txt";
	private static final String RUNNING = "Running: ";
	private static final String COMPLETED = "Completed: ";
	private static final String ELAPSED = "Elapsed: ";
	private static final int DEFAULT_WORKERS = 4;
	private static final int STATUS_COL = 1;
	//endregion
	
	//region fields
	private DefaultTableModel model;
	
	private JButton singleFetchButton;
	private JButton concurrentFetchButton;
	private JButton stopButton;
	
	private JLabel runningLabel;
	private JLabel completedLabel;
	private JLabel elapsedLabel;
	
	private int numRunningWorkers;
	private int numCompleted;
	private long startTime;
	
	private JTextField numThreadsField;
	private JProgressBar progressBar;
	
	private final Object countLock;
	private final Object createLock;
	private Semaphore limit;
	
	private Worker worker;
	private final ArrayList<Thread> workers;
	
	private int numLinks;
	//endregion
	
	public WebView(String filename) {
		countLock = new Object();
		createLock = new Object();
		numRunningWorkers = 0;
		numCompleted = 0;
		workers = new ArrayList<>();
		numLinks = 0;
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		createTable(filename);
		createFetchButtons();
		createFieldsAndLabels();
		createProgressBar();
		createStopButton();
		
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void createTable(String file) {
		model = new DefaultTableModel(new String[]{"url", "status"}, 0);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			reader.lines().forEach(line -> {
				model.addRow(new String[]{line, ""});
				numLinks++;
			});
		} catch (Exception ignored) {
		}
		
		JTable table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 300));
		add(scrollPane);
	}
	
	private void createFetchButtons() {
		singleFetchButton = new JButton("Single Thread Fetch");
		singleFetchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		singleFetchButton.addActionListener(e -> startWorkers(1));
		add(singleFetchButton);
		
		concurrentFetchButton = new JButton("Concurrent Fetch");
		concurrentFetchButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		concurrentFetchButton.addActionListener(e -> startWorkers(Math.max(1, Integer.parseInt(numThreadsField.getText()))));
		add(concurrentFetchButton);
	}
	
	private void createFieldsAndLabels() {
		numThreadsField = new JTextField(Integer.toString(DEFAULT_WORKERS));
		numThreadsField.setMaximumSize(new Dimension(100, numThreadsField.getHeight()));
		numThreadsField.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(numThreadsField);
		
		runningLabel = new JLabel(RUNNING);
		runningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(runningLabel);
		
		completedLabel = new JLabel(COMPLETED);
		completedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(completedLabel);
		
		elapsedLabel = new JLabel(ELAPSED);
		elapsedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(elapsedLabel);
	}
	
	private void createProgressBar() {
		progressBar = new JProgressBar(0, numLinks);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		add(progressBar);
	}
	
	private void createStopButton() {
		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		stopButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		stopButton.addActionListener(e -> {
			synchronized (createLock) {
				if (worker != null) {
					worker.interrupt();
					worker = null;
				}
				
				workers.forEach(Thread::interrupt);
			}
		});
		
		add(stopButton);
	}
	
	private void startWorkers(int numWorkers) {
		reset();
		limit = new Semaphore(numWorkers);
		worker = new Worker(this);
		startTime = System.currentTimeMillis();
		worker.start();
		singleFetchButton.setEnabled(false);
		concurrentFetchButton.setEnabled(false);
		stopButton.setEnabled(true);
		numThreadsField.setEnabled(false);
	}
	
	public void changeWorkerCount(int num) {
		synchronized (countLock) {
			numRunningWorkers += num;
			if (num < 0) {
				numCompleted++;
			}
		}
		SwingUtilities.invokeLater(() -> {
			runningLabel.setText(RUNNING + numRunningWorkers);
			completedLabel.setText(COMPLETED + numCompleted);
			if (numRunningWorkers == 0) {
				singleFetchButton.setEnabled(true);
				concurrentFetchButton.setEnabled(true);
				stopButton.setEnabled(false);
				numThreadsField.setEnabled(true);
				elapsedLabel.setText(ELAPSED + (System.currentTimeMillis() - startTime) / 1000.0);
			}
		});
	}
	
	private void reset() {
		numCompleted = 0;
		progressBar.setValue(0);
		progressBar.setMaximum(model.getRowCount());
		runningLabel.setText(RUNNING);
		completedLabel.setText(COMPLETED);
		elapsedLabel.setText(ELAPSED);
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setValueAt("", i, STATUS_COL);
		}
	}
	
	public void sendCompletionNotice() {
		changeWorkerCount(-1);
		limit.release();
		SwingUtilities.invokeLater(() -> progressBar.setValue(progressBar.getValue() + 1));
	}
	
	public void updateRow(final int row, final String msg) {
		SwingUtilities.invokeLater(() -> model.setValueAt(msg, row, STATUS_COL));
	}
	
	public static void main(String[] args) {
		new WebView(LINKS).setVisible(true);
	}
	
	private class Worker extends Thread {
		
		private final WebView view;
		
		public Worker(WebView view) {
			this.view = view;
		}
		
		@Override
		public void run() {
			changeWorkerCount(1);
			int numURLs = model.getRowCount();
			workers.clear();
			for (int i = 0; i < numURLs; i++) {
				try {
					limit.acquire();
				} catch (InterruptedException e) {
					break;
				}
				synchronized (createLock) {
					Thread worker = new WebWorker((String) model.getValueAt(i, 0), i, view);
					workers.add(worker);
					worker.start();
				}
			}
			changeWorkerCount(-1);
		}
	}
}