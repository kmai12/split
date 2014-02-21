package splitPackage;

import javax.faces.bean.*;


@ManagedBean
public class Bill {
	public Bill(){
	}

	public Bill(int bill_id, int sender_id, int recipient_id, String bill_name,
			double total, double cost, String date, String status,
			String comment, int numRecipients) {
		this.bill_id = bill_id;
		this.sender_id = sender_id;
		this.recipient_id = recipient_id;
		this.bill_name = bill_name;
		this.total = total;
		this.cost = cost;
		this.date = date;
		this.status = status;
		this.comment = comment;
		this.numRecipients = numRecipients;
	}

	//Attributes
	private int bill_id;
	private int sender_id;
	private int recipient_id;
	private String bill_name;
	private double total;
	private double cost;
	private String date;
	private String status;
	private String comment;
	private int numRecipients;

	//Getters and Setters
	public int getBill_ID() {return bill_id; }
	public void setBill_ID(int bill_ID) { this.bill_id = bill_ID;}
	public int getSender_ID() {	return sender_id;}
	public void setSender_ID(int sender_ID) {	this.sender_id = sender_ID;}
	public int getRecipient_ID() {return recipient_id;}
	public void setRecipient_ID(int recipient_ID) {	this.recipient_id = recipient_ID;}
	public String getBill_name() {	return bill_name;}
	public void setBill_name(String bill_name) {	this.bill_name = bill_name;}
	public double getTotal() {	return total;}
	public void setTotal(double total) {this.total = total;}
	public double getCost() {return cost;}
	public void setCost(double cost) {this.cost = cost;}
	public String getDate() {return date;}
	public void setDate(String date) {this.date = date;}
	public String getStatus() {return status;}
	public void setStatus(String status) {this.status = status;}
	public String getComment() {return comment;}
	public void setComment(String comment) {this.comment = comment;}
	public int getNumRecipients(){return this.numRecipients;}
	public void setNumRecipients(int r){ this.numRecipients = r;};

}
