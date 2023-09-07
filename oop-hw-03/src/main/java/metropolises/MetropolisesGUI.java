package metropolises;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MetropolisesGUI extends JFrame {
	private JComboBox<String> populationComboBox;
	private JComboBox<String> matchComboBox;
	private JTextField metropolis;
	private JTextField continent;
	private JTextField population;
	private final MetropolisesProvider provider;
	
	public MetropolisesGUI(String title) {
		super(title);
		setLayout(new BorderLayout());
		provider = new MetropolisesProvider();
		createTable();
		createNorthPanel();
		createEastPanel();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
	}
	
	private void createTable() {
		JTable table = new JTable(provider);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(300, 500));
		add(scroll, BorderLayout.CENTER);
	}
	
	private void createNorthPanel() {
		Box northPanel = Box.createHorizontalBox();
		add(northPanel, BorderLayout.NORTH);
		
		metropolis = createTextField(northPanel, "Metropolis: ");
		continent = createTextField(northPanel, "Continent: ");
		population = createTextField(northPanel, "Population: ");
	}
	
	private JTextField createTextField(Box panel, String label) {
		panel.add(new JLabel(label));
		
		JTextField tf = new JTextField(15);
		panel.add(tf);
		
		panel.add(Box.createHorizontalStrut(50));
		return tf;
	}
	
	private void createEastPanel() {
		Box panel = Box.createVerticalBox();
		add(panel, BorderLayout.EAST);
		
		panel.add(Box.createVerticalStrut(10));
		panel.add(createAddButton());
		
		panel.add(Box.createVerticalStrut(10));
		panel.add(createSearchButton());
		
		panel.add(Box.createVerticalStrut(30));
		panel.add(createComboBoxes());
	}
	
	private JPanel createAddButton() {
		JButton addButton = new JButton("Add");
		JPanel addPanel = new JPanel();
		
		addPanel.setLayout(new BorderLayout());
		addPanel.add(addButton);
		addPanel.setMaximumSize(new Dimension(200, 30));
		
		addButton.addActionListener(e -> {
			String populationTxt = population.getText();
			int pop = populationTxt != null && !populationTxt.isEmpty()
					? Integer.parseInt(populationTxt)
					: -1;
			provider.add(metropolis.getText(), continent.getText(), pop);
		});
		return addPanel;
	}
	
	private JPanel createSearchButton() {
		JButton searchButton = new JButton("Search");
		JPanel searchPanel = new JPanel();
		
		searchPanel.setLayout(new BorderLayout());
		searchPanel.add(searchButton);
		searchPanel.setMaximumSize(new Dimension(200, 30));
		
		searchButton.addActionListener(e -> {
			String populationTxt = population.getText();
			int pop = populationTxt != null && !populationTxt.isEmpty()
					? Integer.parseInt(populationTxt)
					: -1;
			boolean less = populationComboBox.getSelectedIndex() == 0;
			boolean partialMatch = matchComboBox.getSelectedIndex() == 0;
			provider.search(metropolis.getText(), continent.getText(), pop, less, partialMatch);
		});
		
		return searchPanel;
	}
	
	private JPanel createComboBoxes() {
		populationComboBox = new JComboBox<>(new String[]{"Population Less Than", "Population Larger Than"});
		matchComboBox = new JComboBox<>(new String[]{"Partial Match", "Exact Match"});
		
		JPanel searchOptions = new JPanel();
		searchOptions.setMaximumSize(new Dimension(200, 85));
		searchOptions.setLayout(new BoxLayout(searchOptions, BoxLayout.Y_AXIS));
		
		searchOptions.setBorder(new TitledBorder("Search Options"));
		searchOptions.add(populationComboBox);
		
		searchOptions.add(Box.createVerticalStrut(5));
		searchOptions.add(matchComboBox);
		
		return searchOptions;
	}
	
	public static void main(String[] args) {
		MetropolisesGUI gui = new MetropolisesGUI("Metropolis Viewer");
		gui.setVisible(true);
	}
}
