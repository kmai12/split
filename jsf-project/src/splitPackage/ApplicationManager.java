package splitPackage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import splitPackageJDBC.JDBCSQLiteConnection;
/**
 * ApplicationManager is the superclass of both UserManager and BillManager.
 * It contain methods that can be used by subclasses. 
 */

public abstract class ApplicationManager {
	/**
	 * Method to check if user input is valid
	 * @param str The user input.
	 * @return a boolean specifying whether or not the user's input is valid.
	 */
	public boolean checkValidInput(String str) {
		boolean valid = false;
		if (str.trim().length() == 0
				|| str.trim().length() != str.length()) {
			valid = true;
		}
		return valid;
	}
	
	/**
	 * Helper method to check if a particular friendship exists in the database
	 * @param user_id the id of the user you are trying to search for 
	 * @param friendUserName the name of the friend you are searching for
	 * @return resultSet containing your search result
	 * @throws SQLException
	 */
	public ResultSet searchForFriendship(int user_id, String friendUserName) throws SQLException {
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		String query = "SELECT * FROM friends WHERE user_id=" + user_id + " AND friend_uname='" + friendUserName + "';";
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
		} catch (SQLException e){
			e.printStackTrace();
		}
		return rs;		
	}
	/**
	 * Helper method to check if a particular user is a recipient of a bill.
	 * @param bill_id the id of a bill
	 * @param recipient_id the id of the recipient
	 * @return resultSet containing your search result
	 * @throws SQLException
	 */
	public ResultSet searchForRecipient(int bill_id, int recipient_id) throws SQLException {
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		String query = "SELECT * FROM bill_recipient WHERE bill_id=" + bill_id + " AND recipient_id=" 
		+ recipient_id + ";";
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
		} catch (SQLException e){
			e.printStackTrace();
		}
		return rs;		
	}

	/**
	 * Helper method that searches tables in the database.
	 * @param table the name of the table 
	 * @param item the item in the table
	 * @param id the value of the item
	 * @return resultSet containing your search result
	 * @throws SQLException
	 */
	public ResultSet searchTable(String table, String item, int id) throws SQLException {
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		String query = "SELECT * FROM " + table + " WHERE "+ item +"= " + id + ";";
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
		} catch (SQLException e){
			e.printStackTrace();
		}
		return rs;		
	}
	
	/**
	 * Helper method that searches tables in the database.
	 * @param table the name of the table 
	 * @param item the item in the table
	 * @param str the value of the item
	 * @return resultSet containing your search result
	 * @throws SQLException
	 */
	public ResultSet searchTable(String table, String item, String str) throws SQLException {
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		String query = "SELECT * FROM " + table + " WHERE "+ item +"= '" + str + "';";
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
		} catch (SQLException e){
			e.printStackTrace();
		}
		return rs;		
	}
	
	
}
