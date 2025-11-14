package ConnectDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	
	public static Connection con= null;
	private static ConnectDB instance = new ConnectDB();
	private ConnectDB() { 
		connect();
	}
	public static ConnectDB getInstance() {
		return instance;
	}
	
	public void connect() {
		String url = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=rap";
		String user = "sa";
		String password = "sapassword";
		try {
			con = DriverManager.getConnection(url, user, password);
		}
		catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws SQLException {
		String url = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=rap";
		String user = "sa";
		String password = "sapassword";
        return DriverManager.getConnection(url, user, password);
    }
    
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                con = null;
                System.out.println("Ngắt kết nối database!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
