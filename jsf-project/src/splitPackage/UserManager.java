package splitPackage;

import javax.faces.bean.*;
import javax.faces.model.SelectItem;

import splitPackageJDBC.JDBCSQLiteConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * <p>
 * This class is the main controller of the application. It is in charge of
 * account management, using BillManager to manage bills, etc.
 * </p>
 */
@ManagedBean
@SessionScoped
public class UserManager extends ApplicationManager implements Serializable {
	private User currentUser; // Current user logged in
	private BillManager bm;
	private String statusMessage; // message that can be displayed on webpages
									// i.e "Invalid Password!"
	private String friendUserName;
	private String recipientName;
	private ArrayList<SelectItem> currentUserFriends; 

	
	/**
	 * Default no-arg constructor. Set the current User to a new default User.
	 */
	public UserManager() {
		currentUser = new User();
		bm = new BillManager();
		currentUserFriends = new ArrayList<SelectItem>();
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
	 *            statusMessage, A message that displays the status of the
	 *            program. It informs the user of certain problems or errors
	 *            that need attention. (e.g. "Invalid Password" or
	 *            "Please enter a username.") </p>
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

	public String getFriendUserName() {
		return this.friendUserName;
	}
	public void setFriendUserName(String uname) {
		friendUserName = uname;
	}
	
	// Methods for Registration/Login
	/**
	 * This method is used to register a new user.
	 * 
	 * @return a string that directs you to the proper page. Either the start
	 *         page or the registration page.
	 */
	public String registerUser() {
		try {
			ResultSet rs = searchForUser(currentUser.getUser().toLowerCase());
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
			if (checkValidInput(currentUser.getUser())) {
				statusMessage = "Please enter a valid username.";
				return "register";
			}
			if (checkValidInput(currentUser.getPw())) {
				statusMessage = "Please enter a valid password.";
				return "register";
			}
			if (checkValidInput(currentUser.getFirst())) {
				statusMessage = "Please enter a valid first name.";
				return "register";
			}
			if (checkValidInput(currentUser.getLast())) {
				statusMessage = "Please enter a valid last name.";
				return "register";
			}
			if (checkValidInput(currentUser.getEmail())) {
				statusMessage = "Please enter a valid email address.";
				return "register";
			} // end check for empty fields and/or leading/trailing white spaces

			insertIntoTable("user",currentUser);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		statusMessage=null;
		return "start-page";
	}
	

	/**
	 * This method is used to login the a user.
	 * 
	 * @return a string that directs you to the proper page. Either the start
	 *         page or the login page.
	 */
	public String login() {
		ResultSet rs = null;
		try {
			rs = searchForUser(currentUser.getUser());
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
				rs.close();
			} else {
				statusMessage = "Username " + currentUser.getUser() + " not found!";
				return "start-page";
			}
		} catch (SQLException e) {
			e.printStackTrace(); 
		}finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		// Sets BillManager's currentUser to the current user
		User temp = new User(currentUser);
		bm.setCurrentUser(temp);
		statusMessage = null;
		return "front-page";
	}
	
	public String logout() {
		currentUser = new User();
		bm = new BillManager();
		statusMessage = ""; 
		friendUserName = "";
		recipientName = "";
		currentUserFriends = new ArrayList<SelectItem>();
		return "start-page";
	}	
	
	/**
	 * This method is used to help the user add a new friend.
	 * @return a string that directs you to the proper page
	 */
	
	public String addFriend() {
		ResultSet rs = null;
		ResultSet myFriends = null;
		//check to see if user left the field blank
		if(checkValidInput(friendUserName)) {
			statusMessage = "Please enter a username.";
		}
		
		//check to see if user exist
		try {
			rs = searchForUser(friendUserName);
			if(rs.next()) {
				User friend = new User();
				friend.setUser(rs.getString("user_name"));
				friend.setID(rs.getInt("user_id"));
				rs.close();
				//check to see if already friends
				myFriends = searchForFriendship(currentUser.getID(),friendUserName);
				if(myFriends != null) {
					if(myFriends.next()) {
						statusMessage = "You are already friends with " + friendUserName + ".";
						return "addnewfriend";
					}
				}
				myFriends.close();
				//if all pass all checks, add friend.
				insertIntoTable("friends",friend);
			} else {
				statusMessage = "User " + friendUserName + " does not exist.";
				return "addnewfriend";
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					myFriends.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		//check to see if already friends with this user
		//if pass all checks add friends
		statusMessage = null;
		return "front-page";
	
	}
	public String addBill() {
		findCurrentUserFriends();
		return "addbill";
	}
	
	/**
	 * Function that returns a list of the current user's friends
	 */
	public List<SelectItem> getCurrentUserFriends() {
		return currentUserFriends;
	}
	
	public List<SelectItem> findCurrentUserFriends() {

		ResultSet rs = null;
		try {
			rs = searchForFriends(currentUser.getID());
			while (rs.next()) {
				String s = rs.getString("friend_uname");
				currentUserFriends.add(new SelectItem(s));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return currentUserFriends;
	}
	public String addBillGoBack() {
		this.currentUserFriends = new ArrayList<SelectItem>();
		bm.setRecipientList(new ArrayList<User>());
		bm.getCurrentBill().setBill_name("");
		bm.getCurrentBill().setTotal(0);
		bm.setStatusMessage("");
		
		return "front-page";
	}

	// HELPER FUNCTIONS
	/**
	 * This method is a helper method that inserts information to data tables in the database
	 * @param tableName the name of the table that you would like to add information to
	 * @param user the user that holds the information you would like to add to the database
	 */
	
	public void insertIntoTable(String tableName, User user) {
		Connection connection = null;
		Statement statement = null;
		String query = "";
		
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			
			switch (tableName) {
				case "user" :
					query = "INSERT into user(user_id,user_name,password,first,last,email) "
							+ "values(null,'"
							+ user.getUser()
							+ "','"
							+ user.getPw()
							+ "','"
							+ user.getFirst()
							+ "','"
							+ user.getLast()
							+ "','"
							+ user.getEmail()
							+ "');";
					statement.executeUpdate(query);
					break;
				case "friends" :
					query = "INSERT INTO friends(user_id, friend_id, friend_uname) " 
							+ "values("
							+ currentUser.getID()
							+ ","
							+ user.getID()
							+ ",'"
							+ user.getUser()
							+ "');";
					statement.executeUpdate(query);
					break;
				default:
					System.out.println("table not found");
				
			}
		} catch(SQLException e) {
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
		
		
	} 
}
