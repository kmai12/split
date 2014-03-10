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
 * Class that manages bill actions
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
	 * Creates a bill.
	 * 
	 * @return the name of the web-page to be directed to
	 */
	public String createBill() {
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			// check for duplicate bill
			String query = "";
			rs = searchTable("bill", "bill_name", currentBill.getBill_name()
					.toLowerCase());
			if (rs.next()) {
				if ((currentBill.getBill_name().toLowerCase()).equals(rs
						.getString("bill_name").toLowerCase())) {
					statusMessage = currentBill.getBill_name()
							+ " already exists.";
					return "addbill";
				}
			}
			rs.close();
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
					+ currentBill.getTotal() + ",'Not Paid'," + "null,"
					+ "null," + recipientList.size() + ")";
			statement.executeUpdate(query);

			rs = searchTable("bill", "bill_name", currentBill.getBill_name());
			currentBill.setBill_ID(rs.getInt("bill_id"));
			rs.close();

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
		generateOwedToYouList();
		recipientList = new ArrayList<User>();
		return "addbill";
	}

	/**
	 * This method connects to the database and obtains all the bills that the
	 * user needs to pay off.
	 * 
	 * @return List of bills.
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
	 * @return List<Bill>
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
					rs2.close();
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
			checkBills();
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
		return "addbill";
	}

	public Boolean checkBills() {
		Boolean billAllPaid = false;
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

	// Helper Function
	private boolean duplicates(User user) {
		for (int j = 0; j < recipientList.size(); j++) {
			if (recipientList.get(j).getUser().equals(user.getUser()))
				return true;
		}
		return false;
	}

	private double split(int recipientAmount, double total) {
		double cost = total / recipientAmount;
		return cost;
	}
}
