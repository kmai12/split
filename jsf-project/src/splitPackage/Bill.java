package splitPackage;

import javax.faces.bean.*;

import java.util.ArrayList;
import java.util.Date;

@ManagedBean
public class Bill {
	//Attributes
	private int senderId;
	private double amount;
	private String title;
	private ArrayList<User> recipients = new ArrayList<User>();
	private Date timeStamp;
	private String status;
	private ActionCaller action = new ActionCaller();
	
	//Constructors
	public Bill() {
		this.title = null;
		this.amount = 0.0;
		this.timeStamp = new Date();
	}
	public Bill(String title, double amnt) {
		this.title = title;
		this.amount = amnt;
		this.timeStamp = new Date();
	}
	
	//Getters and Setters
	public String getTitle() { return this.title; }
	public void setTitle(String title) { this.title=title; }
	public double getAmount() { return this.amount; }
	public void setAmount(double amnt) { this.amount=amnt; }
	public String getStatus() { return this.status; }
	public void setAmount(String stat) { this.status=stat; }
	public int getSenderId() { return this.senderId; }
	public Date getTimeStamp() { return this.timeStamp; }
	
	//methods
	public void addRecipients(User rec) {
		recipients.add(rec);
	}
	public String create(User currentUser) {
		return action.createBill(this,currentUser);
	}
	public String toString() {
		String result = this.timeStamp + " " + this.title + " " +this.amount 
				+ "\n" + "Recipients: ";
		
		return result;
	}
	
}
