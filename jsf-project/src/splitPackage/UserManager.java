package splitPackage;

import javax.faces.bean.*;

import splitPackageJDBC.JDBCSQLiteConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;


/**
 * @author splitIt
 * The class that drives the app.
 * Is in charge of account management, creating/paying bills, etc.
 */
@ManagedBean

public class UserManager {
	private User currentUser;			//Current user logged in
	private String statusMessage;		//message that can be displayed on webpages i.e "Invalid Password!"
	
	public UserManager() {
		currentUser = new User();
	}

	public UserManager(User currentUser) {this.currentUser = currentUser;}

	public User getCurrentUser(){return currentUser;}
	
	public void setCurrentUser(User u){ currentUser = u;}
	
	public String getStatusMessage(){ return statusMessage;}
	
	public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage;}
	
	//Methods for Registration/Login
	public String registerUser(){
		
		System.out.println(currentUser.getLast());
		return "start-page";
	}

	public String login(){
		ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
        String usernameInput = currentUser.getUser();
        
        String query = "SELECT * FROM user WHERE user_name='" + usernameInput +"'";
        try {
            connection = JDBCSQLiteConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            if (rs.next()) {
            	currentUser.setUser(rs.getString("user_name"));
            	currentUser.setID(rs.getInt("user_id"));
            	if(!(currentUser.getPw().equals(rs.getString("password")))){
            		statusMessage = "Invalid Password!";
            		return "start-page";
            	}
            	currentUser.setPw(rs.getString("password"));
            	currentUser.setFirst(rs.getString("first"));
            	currentUser.setLast(rs.getString("last"));
            	currentUser.setEmail(rs.getString("email"));
            }
            else{
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
		return "home";
	}

	// after creating a bill return the user home
	public String createBill(Bill b, User currentUser) {
		currentUser.addBill(b);
		return "home";
	}

	public String payBill() {
		return "stub";
	}

	public String removeBill() {
		return "stub";
	}

}
