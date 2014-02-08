package splitPackageJDBC;

//Use interfaces from java.sql package
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCMySQLConnection {
  //static reference to itself
  private static JDBCMySQLConnection instance = new JDBCMySQLConnection();
  public static final String URL = "jdbc:mysql://localhost/splitdb";
  public static final String USER = "kmai";
  public static final String PASSWORD = "456654";
  //public static final String USER = "YOUR_DATABASE_USERNAME";
  //public static final String PASSWORD = "YOUR_DATABASE_PASSWORD";
  public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 

  //private constructor
  private JDBCMySQLConnection() {
      try {
          //Load MySQL Java driver
          Class.forName(DRIVER_CLASS);
      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      }
  }

  private Connection createConnection() {

      Connection connection = null;
      try {
          //Establish Java MySQL connection
          connection = DriverManager.getConnection(URL, USER, PASSWORD);
      } catch (SQLException e) {
          System.out.println("ERROR: Unable to Connect to Database.");
      }
      return connection;
  }   

  public static Connection getConnection() {
      return instance.createConnection();
  }
}