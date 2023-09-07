package metropolises;

import javax.swing.table.AbstractTableModel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetropolisesProvider extends AbstractTableModel {
	private static final String DATABASE = "metropolises";
	private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE;
	private static final String USERNAME = "saba";
	private static final String PASSWORD = "password";
	private static final String DATATABLE = "metropolises";
	private static final String[] COLUMNS = {"metropolis", "continent", "population"};
	
	private List<List<String>> data;
	private Statement statement;
	private Connection connection;
	
	
	public MetropolisesProvider() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			statement = connection.createStatement();
			data = new ArrayList<>();
			search("", "", -1, true, true); //initial data
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void add(String metropolis, String continent, int population) {
		if (metropolis.isEmpty() || continent.isEmpty() || population < 0) {
			return;
		}
		
		try {
			PreparedStatement statement = getAddStatement(metropolis, continent, population);
			statement.executeUpdate();
			statement.close();
			data.add(Arrays.asList(metropolis, continent, Integer.toString(population)));
			fireTableStructureChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private PreparedStatement getAddStatement(String metropolis, String continent, int population) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO " + DATATABLE + " (metropolis, continent, population) VALUES (?, ?, ?);");
		
		statement.setString(1, metropolis);
		statement.setString(2, continent);
		statement.setInt(3, population);
		return statement;
	}
	
	public void search(String metropolis, String continent, int population, boolean less, boolean partialMatch) {
		data.clear();
		try {
			String query = getSearchQuery(metropolis, continent, population, less, partialMatch);
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				data.add(Arrays.asList(rs.getString(1), rs.getString(2), Integer.toString(rs.getInt(3))));
			}
			fireTableStructureChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getSearchQuery(String metropolis, String continent, int population, boolean less, boolean partialMatch) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM ").append(DATATABLE);
		
		if (!metropolis.isEmpty() || !continent.isEmpty() || population > -1) {
			query.append(" WHERE");
		}
		
		if (!metropolis.isEmpty()) {
			if (partialMatch) {
				query.append(" ").append(COLUMNS[0]).append(" LIKE \"%").append(metropolis).append("%\"");
			} else {
				query.append(" ").append(COLUMNS[0]).append(" = \"").append(metropolis).append("\"");
			}
		}
		
		if (!continent.isEmpty()) {
			if (!metropolis.isEmpty()) {
				query.append(" AND");
			}
			
			if (partialMatch) {
				query.append(" ").append(COLUMNS[1]).append(" LIKE \"%").append(continent).append("%\"");
			} else {
				query.append(" ").append(COLUMNS[1]).append(" = \"").append(continent).append("\"");
			}
		}
		
		if (population >= 0) {
			if (!metropolis.isEmpty() || !continent.isEmpty()) {
				query.append(" AND");
			}
			
			if (less) {
				query.append(" ").append(COLUMNS[2]).append(" <= ").append(population);
			} else {
				query.append(" ").append(COLUMNS[2]).append(" >= ").append(population);
			}
		}
		query.append(";");
		return query.toString();
	}
	
	@Override
	public int getRowCount() {
		return data.size();
	}
	
	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= getRowCount() || columnIndex >= getColumnCount()) {
			return null;
		}
		return data.get(rowIndex).get(columnIndex);
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}
}
