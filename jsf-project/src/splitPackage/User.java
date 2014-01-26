package splitPackage;

import javax.faces.bean.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@ManagedBean
public class User{
  private String user;
  private String pw;
  
  public String getUser(){
	  return (user);
  }
 
  public String getPw(){
	  return (pw);
  }
  
  public void setUser(String user){
	  this.user = user.trim();
  }
  
  public void setPw(String pw){
	  this.pw = pw.trim();
  }
  
  public String login2(){
	  return "home";
  }
  
  public String logout(){
	  return "start-page";
  }
  
  public String toString() {
	  	return user + " " + pw;  
  }
  
  public String login() throws IOException {
  FileReader file = new FileReader("C:/Users/Jenny/Desktop/Userdb.txt"); 
		BufferedReader reader = new BufferedReader(file);
	  
	  //String txt = "";
	  String line = reader.readLine();
	  while(line != null) {	
		// txt = line;
		 if(this.toString().equals(line)) 
			 return "home";
		 line = reader.readLine();
	  }
	  return "start-page";
	 
  }
  
  
}
	