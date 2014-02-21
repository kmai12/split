package splitPackageJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class that creates a connection to the database.
 * 
 */
public class JDBCSQLiteConnection {
	// static reference to itself
	private static JDBCSQLiteConnection instance = new JDBCSQLiteConnection();
	public static final String URL = "jdbc:sqlite:C:/work/splitdb.db";
	public static final String DRIVER_CLASS = "org.sqlite.JDBC";

	private JDBCSQLiteConnection() {
		try {
			// Load SQLite Java driver
			Class.forName(DRIVER_CLASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Connection createConnection() {

		Connection connection = null;
		try {
			// Establish Java SQLite connection
			connection = DriverManager.getConnection(URL);
		} catch (SQLException e) {
			System.out.println("ERROR: Unable to Connect to Database.");
		}
		return connection;
	}

	public static Connection getConnection() {
		return instance.createConnection();
	}
}