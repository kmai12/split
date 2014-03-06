package splitPackage;

import javax.faces.bean.*;
import javax.faces.model.SelectItem;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import splitPackageJDBC.JDBCSQLiteConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class that manages bill actions
 *
 */
/**
 * @author Kevin
 * 
 */
@ManagedBean
public class BillManager extends ApplicationManager implements Serializable {
	private User currentUser;
	private Bill currentBill;
	private List<Bill> bList;
	private String statusMessage;
	private String removeID;
	private String recipientName;
	private List<User> recipientList;
	
	// Constructors
	public BillManager() {
		currentBill = new Bill();
		bList = new ArrayList<Bill>();
		recipientList = new ArrayList<User>();
	}

	public BillManager(User u) {
		this.currentUser = u;
	}

	// Getters and Setters
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public Bill getCurrentBill() {
		return currentBill;
	}

	public void setCurrentBill(Bill currentBill) {
		this.currentBill = currentBill;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String name) {
		recipientName = name;
	}


	public void setRecipientList(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRemoveID() {
		return removeID;
	}

	public void setRemoveID(String removeID) {
		this.removeID = removeID;
	}

	public void setbList(List<Bill> bList) {
		this.bList = bList;
	}
	public List<User> getRecipientList() {
		return this.recipientList;
	}
	public void setRecipientList(List<User> reps) {
		this.recipientList = reps;
	}	
	// Methods
	/**
	 * Creates a bill.
	 * 
	 * @return the name of the web-page to be directed to
	 */
	public String createBill() {
		Connection connection = null;
		ResultSet rs = null;
		Statement statement = null;
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			// check for duplicate bill
			String query = "SELECT * FROM bill WHERE bill_name='"
					+ currentBill.getBill_name().toLowerCase() + "'";
			rs = statement.executeQuery(query);
			if (rs.next()) {
				if ((currentBill.getBill_name().toLowerCase()).equals(rs
						.getString("bill_name").toLowerCase())) {
					statusMessage = currentBill.getBill_name()
							+ " already exists.";
					return "addbill";
				}
			}
			// Checks for correct input format
			if (checkValidInput(currentBill.getBill_name())) {
				statusMessage = "Invalid Bill Name";
				return "addbill";
			}
			// Splits the bill
			currentBill.setCost(split(recipientList.size(),
					currentBill.getTotal()));
			// Inserts a bill into table bill with values
			// bill_id, bill_name, sender_id, recipient_id, cost, total, status,
			// date, comment
			query = "INSERT INTO bill VALUES(null,'"
					+ currentBill.getBill_name() + "'," + currentUser.getID()
					+ "," + "null," + currentBill.getCost() + ","
					+ currentBill.getTotal() + ",'Owed'," + "null," + "null,"
					+ recipientList.size() + ")";
			statement.executeUpdate(query);
			
			query = "SELECT * FROM BILL WHERE bill_name='"
					+ currentBill.getBill_name() + "'";
			rs = statement.executeQuery(query);
			currentBill.setBill_ID(rs.getInt("bill_id"));
			
			// Inserts a bill_recipient for each recipient
			for (User recipient : recipientList) {
				int recipientID = recipient.getID();
				query = "INSERT INTO bill_recipient VALUES("
						+ currentBill.getBill_ID() + "," + recipientID + ")";
				statement.executeUpdate(query);
			}
			

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
		statusMessage = "Success!";
		currentBill = new Bill();
		return "addbill";
	}

	/**
	 * Getter for bList connects to the database and obtains all the bills owned
	 * by the user.
	 * 
	 * @return List of bills.
	 */
	public List<Bill> getbList() {
		List<Bill> bList = new ArrayList<Bill>();
		Connection connection = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Statement statement = null;
		Statement statement2 = null;
		try {
			connection = JDBCSQLiteConnection.getConnection();
			// YOU NEED 2 SEPARATE STATEMENTS FOR 2 CONCURRING QUERY
			// EXECUTIONS!!
			statement = connection.createStatement();
			statement2 = connection.createStatement();
			String query = "SELECT * FROM bill_recipient WHERE recipient_id="
					+ currentUser.getID();

			rs = statement.executeQuery(query);
			while (rs.next()) {
				// System.out.println(rs.getInt("bill_id"));
				String query2 = "SELECT * FROM bill WHERE bill_id="
						+ rs.getInt("bill_id");
				rs2 = statement2.executeQuery(query2);
				while (rs2.next()) {
					Bill bill = new Bill();
					bill.setBill_ID(rs2.getInt("bill_id"));
					bill.setBill_name(rs2.getString("bill_name"));
					bill.setSender_ID(rs2.getInt("sender_id"));
					bill.setRecipient_ID(rs2.getInt("recipient_id"));
					bill.setCost(rs2.getDouble("cost"));
					bill.setTotal(rs2.getDouble("total"));
					bill.setStatus(rs2.getString("status"));
				//	bill.setNumRecipients(rs2.getInt("num_recipients"));
					bList.add(bill);
				}

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
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
		return bList;
	}

	/**
	 * Removes selected bill in the database from the user.
	 * 
	 * @return name of html page to direct to.
	 */
	public String payBill() {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			// check for duplicate bill
			String query = "DELETE FROM bill_recipient WHERE bill_id="
					+ removeID + " AND recipient_id=" + currentUser.getID();
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
		// statusMessage = "Successfully Paid!";
		return "front-page";
	}
	
	public String addRecipient() {
		ResultSet rs = null;
		try {
			rs = searchForUser(recipientName);
			if (rs.next()) {
				User temp = new User();
				temp.setUser(rs.getString("user_name"));
				temp.setID(rs.getInt("user_id"));
				temp.setPw(rs.getString("password"));
				temp.setFirst(rs.getString("first"));
				temp.setLast(rs.getString("last"));
				temp.setEmail(rs.getString("email"));
				//check to see if user is already a recipient
				if(! duplicates(temp)) {
					recipientList.add(temp);
				} else {
					statusMessage = "User " + temp.getUser() + " is already a recipient.";
					return "addbill";
				} 
				rs.close();
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
		return "addbill";
	}

	// Helper Function
	private boolean duplicates(User user) {
		for (int j = 0; j < recipientList.size(); j++) {
				if (recipientList.get(j).getUser().equals(user.getUser()) )
					return true;
		}
		return false;
	}
	
	private boolean duplicates(ArrayList<String> list) {
		for (int j = 0; j < list.size(); j++) {
			for (int k = j + 1; k < list.size(); k++) {
				if (k != j && list.get(k).equals(list.get(j)))
					return true;
			}
		}
		return false;
	}

	private double split(int recipientAmount, double total) {
		double cost = total / recipientAmount;
		return cost;
	}
}
