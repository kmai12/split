package splitPackage;

import javax.faces.bean.*;

import splitPackageJDBC.JDBCMySQLConnection;

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


@ManagedBean

public class UserManager {
	private User currentUser;
	
	public UserManager() {
		currentUser = new User();
	}

	public UserManager(User currentUser) {
		this.currentUser = currentUser;
	}

	public User getCurrentUser(){
		return currentUser;
	}
	
	public void setCurrentUser(User u){
		currentUser = u;
	}
	
	//Methods for Registration/Login
	public String registerUser(User newUser) throws IOException {
		return "home";
	}

	public String login(){
		ResultSet rs = null;
        Connection connection = null;
        Statement statement = null; 
 
        String query = "SELECT * FROM user WHERE user_name='" + currentUser.getUser() +"'";
        try {
            connection = JDBCMySQLConnection.getConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
 
            if (rs.next()) {
            	currentUser.setID(rs.getInt("user_id"));
            	currentUser.setUser(rs.getString("user_name"));
            	currentUser.setFirst(rs.getString("first"));
            	currentUser.setLast(rs.getString("last"));
            	currentUser.setEmail(rs.getString("email"));
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
