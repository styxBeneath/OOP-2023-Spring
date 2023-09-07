package metropolises;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class MetropolisesProviderTest {
	private static final String DATABASE = "metropolises";
	private static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE;
	private static final String USERNAME = "saba";
	private static final String PASSWORD = "password";
	private static final String DB_CREATION_SCRIPT = "src/main/resources/metropolises.sql";
	
	private Statement statement;
	private Connection connection;
	MetropolisesProvider model;
	
	@BeforeEach
	public void setUp() throws Exception {
		createDB();
		model = new MetropolisesProvider();
	}
	
	private void createDB() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		statement = connection.createStatement();
		StringBuilder queryBuilder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(DB_CREATION_SCRIPT));
		while (true) {
			String str = reader.readLine();
			if (str == null) {
				break;
			}
			queryBuilder.append(str).append("\n");
		}
		reader.close();
		String[] queries = queryBuilder.toString().split(";");
		
		for (String query : queries) {
			if (!query.trim().equals("")) {
				statement.executeUpdate(query);
			}
		}
	}
	
	
	@Test
	public void testSearch() {
		model.search("", "", 20000000, false, false);
		assertEquals(model.getRowCount(), 2);
		
		model.search("", "haaaank", 500000, false, false);
		assertEquals(0, model.getRowCount());
		
		model.search("", "haaank", 500000, false, true);
		assertEquals(0, model.getRowCount());
		
		model.search("", "europ", 500000, false, true);
		assertEquals(3, model.getRowCount());
		
		model.search("rme", "", -1, false, false);
		assertEquals(0, model.getRowCount());
		
		model.search("rome", "", -1, false, true);
		assertEquals(1, model.getRowCount());
		
		model.search("rome", "europe", 50000, true, false);
		assertEquals(0, model.getRowCount());
		
		model.search("londo", "", 10000, false, true);
		assertEquals(1, model.getRowCount());
		
		model.search("rme", "199999", -1, false, true);
		assertEquals(0, model.getRowCount());
		
		model.search("rome", "euro", 500000, false, true);
		assertEquals(1, model.getRowCount());
		
	}
	
	@Test
	public void testAdd() {
		int count = model.getRowCount();
		
		model.add("", "", -1);
		assertEquals(count, model.getRowCount());
		
		model.add("Tbilisi", "", -1);
		assertEquals(count, model.getRowCount());
		
		model.add("Tbilisi", "Europe", -1);
		assertEquals(count, model.getRowCount());
		
		model.add("Tbilisi", "Europe", 1700000);
		assertEquals(count + 1, model.getRowCount());
		assertEquals("population", model.getColumnName(2));
		
		assertNotNull(model.getValueAt(0, 0));
		assertNull(model.getValueAt(2, 20));
		assertNull(model.getValueAt(50, 2));
		
	}
	
}
