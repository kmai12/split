package splitPackage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ActionCaller {
	FileReader fr;
	FileWriter fw;
	User currentUser;

	public ActionCaller() {
		currentUser = null;
	}

	public ActionCaller(User currentUser) {
		this.currentUser = currentUser;
	}
	//Methods for Registration/Login
	public String registerUser(User newUser) throws IOException {
		fw = new FileWriter("C:/Users/Jenny/Desktop/Userdb", true);
		PrintWriter writer = new PrintWriter(fw);
		writer.println(newUser.toString());
		writer.close();
		return "home";
	}

	public String loginUser(User currentUser) throws IOException {
		fr = new FileReader("C:/Users/Jenny/Desktop/Userdb");
		BufferedReader reader = new BufferedReader(fr);
		String line = reader.readLine();
		while (line != null) {
			if (currentUser.toString().equals(line))
				return "home";
			line = reader.readLine();
		}
		return "start-page";

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
