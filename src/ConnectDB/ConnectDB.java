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
		String url = "jdbc:sqlserver://localhost:1433;databaseName=rap";
		String user = "sa";
		String password = "StrongP@ssw0rd";
		try {
			con = DriverManager.getConnection(url, user, password);
		}
		catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                instance.connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            instance.connect();
        }
        return con;
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
