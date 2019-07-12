import java.sql.*;

public class databaseConn {
    Connection conn = null;

    public static Connection db_connection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://212.182.24.236:3306/ak_db5","ak_user5", "tyuiop");
            return conn;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
