package splitPackage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import splitPackageJDBC.JDBCSQLiteConnection;

public class ApplicationManager {
	/**
	 * Helper method to search for user in a database
	 * @param usernameInput the the username you would like to search for 
	 * @return resultSet containing your search result
	 * @throws SQLException
	 */
	public ResultSet searchForUser(String usernameInput) throws SQLException {
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		String query = "SELECT * FROM user WHERE user_name='" + usernameInput
				+ "'";
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
	
	/**
	 * Helper method to search for user in a database
	 * @param user_id the id of the user you are trying to search for 
	 * @return resultSet containing your search result
	 * @throws SQLException
	 */
	public ResultSet searchForFriends(int user_id) throws SQLException {
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		String query = "SELECT * FROM friends WHERE user_id=" + user_id + ";";
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
