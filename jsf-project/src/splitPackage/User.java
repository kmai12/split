package splitPackage;

import javax.faces.bean.*;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

@ManagedBean

public class User{
  private String user;
  private String pw;
  private String email;
  private String first;
  private String last;
  private int id;
  private ArrayList<Bill> bills = new ArrayList<Bill>();
  
  //Constructors
  public User() {
	  user = null;
	  pw = null;
  }
  public User(String user, String pw, String email) {
	  this.user = user;
	  this.pw = pw;
	  this.email = email;
  }
  //Getters and Setters
  public String getUser(){ return (user); }
  public void setUser(String user){ this.user = user.trim(); }
  public String getPw(){ return (pw); }
  public void setPw(String pw){ this.pw = pw.trim(); }
  public int getID() { return this.id; }
  public void setID(int id) { this.id = id; }
  public String getFirst(){ return first;}
  public void setFirst(String f){ this.first = f;};
  public String getLast(){ return last;}
  public void setLast(String l){ this.last = l;};
  public ArrayList<Bill> getBills() { return this.bills; }
  
  
  public String toString() {
	  	return this.user + "	" + this.pw;  
  }
   
  //User Actions
  public void addBill(Bill b) { bills.add(b); }

   public void resetpw(){
	  //stub
  }
  
  public String logout(){
	  return "start-page";
  }
  
  
  
}
	