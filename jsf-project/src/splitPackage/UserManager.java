package splitPackage;

import javax.faces.bean.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

	public String loginUser(){
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
