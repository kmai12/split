package splitPackage;

import javax.faces.bean.*;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import splitPackageJDBC.JDBCSQLiteConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * BillManager manages all the bill events such as creating a new bill and
 * deleting a bill.
 */
@ManagedBean
public class BillManager extends ApplicationManager implements Serializable {
	private User currentUser;
	private Bill currentBill;
	private List<Bill> billsList;
	private List<Bill> owedToYouList;
	private String totalPay;
	private String totalReceive;
	private String statusMessage;
	private String removeID;
	private String recipientName;
	private List<User> recipientList;

	// Constructors
	public BillManager() {
		currentBill = new Bill();
		billsList = new ArrayList<Bill>();
		owedToYouList = new ArrayList<Bill>();
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

	public List<Bill> getBillsList() {
		return this.generateBillsList();
	}

	public List<Bill> getOwedToYouList() {
		return this.generateOwedToYouList();
	}

	public void setbList(List<Bill> bList) {
		this.billsList = bList;
	}

	public void setbOwedList(List<Bill> bOwedList) {
		this.owedToYouList = bOwedList;
	}

	public List<User> getRecipientList() {
		return this.recipientList;
	}

	public void setRecipientList(List<User> reps) {
		this.recipientList = reps;
	}

	public String getTotalPay() {
		return this.totalPay;
	}

	public void setTotalPay(String amount) {
		this.totalPay = amount;
	}

	public String getTotalReceive() {
		return this.totalReceive;
	}

	public void setTotalReceive(String amount) {
		this.totalReceive = amount;
	}

	// Methods
	/**
	 * Creates a new bill and adds it to the database.
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
					+ currentBill.getBill_name() + "'";
			rs = statement.executeQuery(query);
			if (rs.next()) {
				if ((currentBill.getBill_name()).equals(rs
						.getString("bill_name"))) {
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
			// Checks for non-empty recipient list
			if (recipientList.size() == 0) {
				statusMessage = "Error: no recipients listed!";
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
	 * This method connects to the database and obtains all the bills that the
	 * user needs to pay off.
	 * 
	 * @return A list of bills that the user needs to pay off.
	 */
	public List<Bill> generateBillsList() {
		List<Bill> billsList = new ArrayList<Bill>();
		ResultSet rs = null;
		ResultSet rs2 = null;
		double totalPay = 0;
		try {
			rs = searchTable("bill_recipient", "recipient_id",
					currentUser.getID());
			while (rs.next()) {
				rs2 = searchTable("bill", "bill_id", rs.getInt("bill_id"));
				while (rs2.next()) {
					Bill bill = new Bill();
					bill.setBill_ID(rs2.getInt("bill_id"));
					bill.setBill_name(rs2.getString("bill_name"));
					bill.setSender_ID(rs2.getInt("sender_id"));
					bill.setRecipient_ID(rs2.getInt("recipient_id"));
					bill.setCost(rs2.getDouble("cost"));
					bill.setTotal(rs2.getDouble("total"));
					bill.setStatus(rs2.getString("status"));
					totalPay += bill.getCost();
					billsList.add(bill);
					rs2.close();
				}
			}
			this.totalPay = "$" + String.format("%.2f", totalPay);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null && rs2 != null) {
				try {
					rs.close();
					rs2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return billsList;
	}

	/**
	 * This method connects to the database and obtains all the bills that the
	 * user is awaiting payment from.
	 * 
	 * @return A list of bills that the user is still awaiting payment from.
	 */
	public List<Bill> generateOwedToYouList() {
		List<Bill> owedToYouList = new ArrayList<Bill>();
		ResultSet rs = null;
		ResultSet rs2 = null;
		double totalReceive = 0.0;
		try {
			rs = searchTable("bill", "sender_id", currentUser.getID());
			while (rs.next()) {
				rs2 = searchTable("bill_recipient", "bill_id",
						rs.getInt("bill_id"));
				while (rs2.next()) {
					Bill bill = new Bill();
					bill.setBill_ID(rs2.getInt("bill_id"));
					bill.setBill_name(rs.getString("bill_name"));
					bill.setRecipient_ID(rs2.getInt("recipient_id"));
					bill.setCost(rs.getDouble("cost"));
					bill.setSender_ID(rs.getInt("sender_id"));
					bill.setTotal(rs.getDouble("total"));
					totalReceive += bill.getCost();
					owedToYouList.add(bill);
				}

			}
			this.totalReceive = "$" + String.format("%.2f", totalReceive);
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null && rs2 != null) {
				try {
					rs.close();
					rs2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return owedToYouList;
	}

	/**
	 * This method completely removes the bill from the database.
	 * 
	 * @return a string that directs you to the billspeopleoweyou page
	 */
	public String deleteBill() {
		Connection connection = null;
		Statement statement = null;
		List<Bill> owedToYou = generateOwedToYouList();
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			if (removeID.trim().equals("")) {
				statusMessage = "Please enter a bill id.";
				return "billspeopleoweyou";
			}
			boolean billExists = false;
			for (Bill i : owedToYou) {
				if (i.getBill_ID() == Integer.parseInt(removeID)) {
					billExists = true;
					break;
				}
			}
			if (!billExists) {
				statusMessage = "Bill does not exist!";
				return "billspeopleoweyou";
			}
			String query = "DELETE FROM bill_recipient WHERE bill_id="
					+ removeID;
			statement.executeUpdate(query);
			query = "DELETE FROM bill where bill_id=" + removeID;
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException ex) {
			statusMessage = "Enter a valid bill id";
			return "billspeopleoweyou";
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
		statusMessage = "";
		this.refresh();
		this.setRemoveID("");
		return "billspeopleoweyou";
	}

	/**
	 * This method removes the bill that the recipient believed they have paid
	 * off.
	 * 
	 * @return a string that directs you to the billsyouowe page
	 */
	public String payBill() {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			// check for duplicate bill
			if (removeID.trim().equals(""))
				return "billsyouowe";
			String query = "DELETE FROM bill_recipient WHERE bill_id="
					+ removeID + " AND recipient_id=" + currentUser.getID();
			statement.executeUpdate(query);
			checkBills();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			catch (NumberFormatException ex){
				statusMessage = "Please enter in a correct bill_id";
				return "billsyouowe";
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
		statusMessage = "";
		this.refresh();
		this.setRemoveID("");
		return "billsyouowe";
	}

	/**
	 * This method is used to add a new recipient to the bill.
	 * 
	 * @return a string that directs you to the addbill page.
	 */
	public String addRecipient() {
		ResultSet rs = null;
		// String query;
		try {
			rs = searchTable("user", "user_name", recipientName);
			if (rs.next()) {
				User temp = new User();
				temp.setUser(rs.getString("user_name"));
				temp.setID(rs.getInt("user_id"));
				temp.setPw(rs.getString("password"));
				temp.setFirst(rs.getString("first"));
				temp.setLast(rs.getString("last"));
				temp.setEmail(rs.getString("email"));
				// check to see if user is already a recipient
				if (!duplicates(temp)) {
					recipientList.add(temp);
				} else {
					statusMessage = "User " + temp.getUser()
							+ " is already a recipient.";
					return "addbill";
				}
				;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		statusMessage = "";
		return "addbill";
	}

	/**
	 * This method checks to see if all recipients have paid the bill. If they
	 * did, the bill will be removed from the database.
	 * 
	 * @return a boolean that specifies whether or not all recipients have paid
	 *         the bill.
	 */
	public boolean checkBills() {
		boolean billAllPaid = false;
		Connection connection = null;
		ResultSet rs = null;
		try {
			// check for duplicate bill
			connection = JDBCSQLiteConnection.getConnection();
			rs = searchTable("bill_recipient", "bill_id",
					Integer.parseInt(removeID));
			if (!rs.next()) {
				Statement statement = connection.createStatement();
				String query = "DELETE FROM bill WHERE bill_id=" + removeID;
				statement.executeUpdate(query);
				billAllPaid = true;
				statement.close();
			}
			rs.close();
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
		return billAllPaid;
	}

	/**
	 * This method updates the total amount that the user owes others and the
	 * total amount that others owe the user.
	 */
	public void refresh() {
		generateOwedToYouList();
		generateBillsList();
	}

	// Helper Function
	/**
	 * This method is a helper function that checks if there are duplicates in
	 * the recipients list.
	 * 
	 * @param user
	 *            The user that is selected to be added to the bill.
	 * @return a boolean that specifies if there is a duplicate.
	 */
	private boolean duplicates(User user) {
		for (int j = 0; j < recipientList.size(); j++) {
			if (recipientList.get(j).getUser().equals(user.getUser()))
				return true;
		}
		return false;
	}

	/**
	 * This method is a helper function that splits the total cost of the bill.
	 * 
	 * @param recipientAmount
	 *            the number of recipients of this bill.
	 * @param total
	 *            the total cost of the bill.
	 * @return an amount that each recipient should pay.
	 */
	private double split(int recipientAmount, double total) {
		double cost = total / recipientAmount;
		return cost;
	}
}
