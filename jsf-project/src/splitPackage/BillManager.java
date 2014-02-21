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

@ManagedBean
public class BillManager implements Serializable{
	User currentUser;
	Bill currentBill;
	List<Bill> bList;
	String statusMessage;
	String recipientList;

	//Constructors
	public BillManager() {
		currentBill = new Bill();
		bList = new ArrayList<Bill>();
	}

	public BillManager(User u){
		this.currentUser = u;
	}

	//Getters and Setters
	public User getCurrentUser() {return currentUser;}
	public void setCurrentUser(User currentUser) {this.currentUser = currentUser;}
	public Bill getCurrentBill() {return currentBill;}
	public void setCurrentBill(Bill currentBill) {this.currentBill = currentBill;}
	public String getStatusMessage() {return statusMessage;}
	public void setStatusMessage(String statusMessage) {this.statusMessage = statusMessage;}
	public String getRecipientList() {return recipientList;}
	public void setRecipientList(String recipientList) {this.recipientList = recipientList;}

	//Methods
	public String createBill() {
		Connection connection=null;
		ResultSet rs=null;
		Statement statement=null;
		try {
			connection = JDBCSQLiteConnection.getConnection();
			statement = connection.createStatement();
			// check for duplicate bill
			String query = "SELECT * FROM bill WHERE bill_name='"
					+ currentBill.getBill_name().toLowerCase() + "'";
			rs = statement.executeQuery(query);
			if (rs.next()) {
				if ((currentBill.getBill_name().toLowerCase()).equals(rs.getString("bill_name").toLowerCase())) {
					statusMessage = currentBill.getBill_name() + " already exists.";
					return "addbill";
				}
			}	
			//Checks for correct input format
			if (currentBill.getBill_name().trim().length() == 0 ||
					currentBill.getBill_name().trim().length() != currentBill.getBill_name().length()) {
				statusMessage = "Invalid Bill Name";
				return "addbill";
			}
			//Checks if spaces or new lines were inserted before the recipients
			String[] preList = recipientList.split("\\s+");
			ArrayList<String> list = new ArrayList<String>();
			if(preList[0].equals("")){
				for(int i = 1; i < preList.length; ++i){
					list.add(preList[i]);
				}
			}
			else{
				for(int i = 0; i < preList.length; ++i){
					list.add(preList[i]);
				}
			}
			//Checks if any recipients repeat
			if(duplicates(list) == true){
				statusMessage = "error: duplicate entry!";
				return "addbill";
			}
			//Checks if recipients exists
			for(String i: list){
				query = "SELECT * FROM user WHERE user_name='"
						+ i + "'";
				rs = statement.executeQuery(query);
				if(!rs.next()){
					statusMessage = i + " does not exist!";
					statement.close();
					connection.close();
					return "addbill";
				}
			}
			currentBill.setNumRecipients(list.size());
			//Splits the bill
			currentBill.setCost(split(currentBill.getNumRecipients(), currentBill.getTotal()));
			//Inserts a bill into table bill with values
			//bill_id, bill_name, sender_id, recipient_id, cost, total, status, date, comment
			query = "INSERT INTO bill VALUES(null,'" + currentBill.getBill_name() +"',"
					+ currentUser.getID() + "," + "null," + currentBill.getCost() + ","
					+ currentBill.getTotal() + ",'Owed'," + "null," + "null,"
					+ currentBill.getNumRecipients() + ")";
			statement.executeUpdate(query);
			query = "SELECT * FROM BILL WHERE bill_name='" + currentBill.getBill_name() +"'";
			rs = statement.executeQuery(query);
			currentBill.setBill_ID(rs.getInt("bill_id"));
			//Inserts a bill_recipient for each recipient
			for(String recipient: list){
				int recipientID;
				query = "SELECT * FROM USER WHERE user_name='" + recipient + "'";
				rs = statement.executeQuery(query);
				recipientID = rs.getInt("user_id");
				query = "INSERT INTO bill_recipient VALUES(" + currentBill.getBill_ID() + ","
						+ recipientID + ")";
				statement.executeUpdate(query);
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
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
		return "addbill";
	}


	/**Helper Function
	 * @param list
	 * @return true if duplicate exists, else returns false
	 */
	private boolean duplicates(ArrayList<String> list){
		for (int j=0;j<list.size();j++){
			for (int k=j+1;k<list.size();k++){
				if (k!=j && list.get(k).equals(list.get(j)))
					return true;
			}
		}
		return false;
	}

	private double split(int recipientAmount, double total){
		double cost = total/recipientAmount;
		return cost;
	}

	public void setbList(List<Bill> bList) {
		this.bList = bList;
	}

	public List<Bill> getbList(){
		List<Bill> bList = new ArrayList<Bill>();
		Connection connection=null;
		ResultSet rs=null;
		ResultSet rs2 = null;
		Statement statement=null;
		Statement statement2=null;
		try {
			connection = JDBCSQLiteConnection.getConnection();
			//YOU NEED 2 SEPARATE STATEMENTS FOR 2 CONCURRING QUERY EXECUTIONS!!
			statement = connection.createStatement();
			statement2 = connection.createStatement();
			String query = "SELECT * FROM bill_recipient WHERE recipient_id="
					+ currentUser.getID();
		
			rs = statement.executeQuery(query);
			while(rs.next()){
				//System.out.println(rs.getInt("bill_id"));
				String query2 = "SELECT * FROM bill WHERE bill_id=" + rs.getInt("bill_id");
				rs2 = statement2.executeQuery(query2);
				while(rs2.next()){
					Bill bill = new Bill();
					bill.setBill_ID(rs2.getInt("bill_id"));
					bill.setBill_name(rs2.getString("bill_name"));
					bill.setSender_ID(rs2.getInt("sender_id"));
					bill.setRecipient_ID(rs2.getInt("recipient_id"));
					bill.setCost(rs2.getDouble("cost"));
					bill.setTotal(rs2.getDouble("total"));
					bill.setStatus(rs2.getString("status"));
					bill.setNumRecipients(rs2.getInt("num_recipients"));
					bList.add(bill);	
				}

			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
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
}

