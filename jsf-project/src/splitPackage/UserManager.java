package splitPackage;

import javax.faces.bean.*;

import splitPackageJDBC.JDBCSQLiteConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.Serializable;

/**
 * <p>
 * This class is the main controller of the application. It is in charge of
 * account management, creating/paying bills, etc.
 * </p>
 * 
 * @author CS48, W14, G03
 */
@ManagedBean
@SessionScoped
public class UserManager implements Serializable {
	private User currentUser; // Current user logged in
	private BillManager bm;
	private String statusMessage; // message that can be displayed on webpages
									// i.e "Invalid Password!"

	/**
	 * Default no-arg constructor. Set the current User to a new default User.
	 */
	public UserManager() {
		currentUser = new User();
		bm = new BillManager();
	}

	/**
	 * <p>
	 * One argument constructor. Set the current user to the current user
	 * running the program.
	 * </p>
	 * 
	 * @param currentUser
	 *            The current user of our programs
	 */
	public UserManager(User currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * Getter for current user.
	 * 
	 * @return currentUser The current user of the program.
	 */
	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * Setter for current user.
	 * 
	 * @param u
	 *            The user that is currently using the program.
	 */
	public void setCurrentUser(User u) {
		currentUser = u;
	}

	/**
	 * Getter for statusMessage.
	 * 
	 * @return <p>
	 *         statusMessage, A message that displays the status of the program.
	 *         It informs the user of certain problems or errors that need
	 *         attention. (e.g. "Invalid Password" or
	 *         "Please enter a username.")
	 *         </p>
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * Setter for statusMessage.
	 * 
	 * @param <p>
	 *        statusMessage, A message that displays the status of the program.
	 *        It informs the user of certain problems or errors that need
	 *        attention. (e.g. "Invalid Password" or "Please enter a username.")
	 *        </p>
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public BillManager getBm() {
		return bm;
	}

	public void setBm(BillManager bm) {
		this.bm = bm;
	}

	// Methods for Registration/Login
	/**
	 * This method is used to register a new user.
	 * 
	 * @return a string that directs you to the proper page. Either the start
	 *         page or the registration page.
	 */
	public String registerUser() {
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		// query is a SQL statement used to insert elements into user
		String query = "INSERT into user(user_id,user_name,password,first,last,email) "
				+ "values(null,'"
				+ currentUser.getUser()
				+ "','"
				+ currentUser.getPw()
				+ "','"
				+ currentUser.getFirst()
				+ "','"
				+ currentUser.getLast()
				+ "','"
				+ currentUser.getEmail()
				+ "');";

		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			// check for duplicate user
			String checkDuplicateUser = "SELECT * FROM user WHERE user_name='"
					+ currentUser.getUser().toLowerCase() + "'";
			rs = statement.executeQuery(checkDuplicateUser);
			if (rs.next()) {
				if ((currentUser.getUser().toLowerCase()).equals(rs.getString(
						"user_name").toLowerCase())) {
					statusMessage = currentUser.getUser()
							+ " is already in use. Please enter another username.";
					return "register";
				}
			} // end check for duplicate user

			// check if any of the fields are empty or contain leading/trailing
			// spaces
			if (currentUser.getUser().trim().length() == 0
					|| currentUser.getUser().trim().length() != currentUser
							.getUser().length()) {
				statusMessage = "Please enter a valid username.";
				return "register";
			}
			if (currentUser.getPw().trim().length() == 0
					|| currentUser.getPw().trim().length() != currentUser
							.getPw().length()) {
				statusMessage = "Please enter a valid password.";
				return "register";
			}
			if (currentUser.getFirst().trim().length() == 0
					|| currentUser.getFirst().trim().length() != currentUser
							.getFirst().length()) {
				statusMessage = "Please enter a valid first name.";
				return "register";
			}
			if (currentUser.getLast().trim().length() == 0
					|| currentUser.getLast().trim().length() != currentUser
							.getLast().length()) {
				statusMessage = "Please enter a valid last name.";
				return "register";
			}
			if (currentUser.getEmail().trim().length() == 0
					|| currentUser.getEmail().trim().length() != currentUser
							.getEmail().length()) {
				statusMessage = "Please enter a valid email address.";
				return "register";
			} // end check for empty fields and/or leading/trailing white spaces

			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					statement.close();
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return "start-page";
	}

	/**
	 * This method is used to login the a user.
	 * 
	 * @return a string that directs you to the proper page. Either the start
	 *         page or the login page.
	 */
	public String login() {
		
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		String usernameInput = currentUser.getUser();
		String query = "SELECT * FROM user WHERE user_name='" + usernameInput
				+ "'";
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			if (rs.next()) {
				currentUser.setUser(rs.getString("user_name"));
				currentUser.setID(rs.getInt("user_id"));
				if (!(currentUser.getPw().equals(rs.getString("password")))) {
					statusMessage = "Invalid Password!";
					return "start-page";
				}
				currentUser.setPw(rs.getString("password"));
				currentUser.setFirst(rs.getString("first"));
				currentUser.setLast(rs.getString("last"));
				currentUser.setEmail(rs.getString("email"));
			} else {
				statusMessage = "Username " + usernameInput + " not found!";
				return "start-page";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		// Sets BillManager's currentUser to the current user
		User temp = new User(currentUser);
		bm.setCurrentUser(temp);
		// bm.setbList(bm.getbList());

		return "front-page";
	}

}
