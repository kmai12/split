package splitPackage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import splitPackageJDBC.JDBCSQLiteConnection;

public abstract class ApplicationManager {
	/**
	 * Helper function to check if user input is valid
	 * @param str The user input.
	 * @return
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
	 * Helper method to search for user in a database
	 * @param user_id the id of the user you are trying to search for 
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
