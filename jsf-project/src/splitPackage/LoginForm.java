package splitPackage;

import javax.faces.bean.*;

@ManagedBean
public class LoginForm{
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
  
  public String login(){
	  return "home";
  }
  
  public String logout(){
	  return "start-page";
  }
  
}
	