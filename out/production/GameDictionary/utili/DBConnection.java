package utili;

import java.sql.*;

public class DBConnection {

    public static Connection dbConnection = null;
    private static final String USERNAME = "DeadShot";
    private static final String PASSWORD = "deadland123";
    private static final String HOST = "jdbc:mysql://localhost/gamedatabase?useTimezone=true&serverTimezone=UTC";

    public static Connection connectToDB() throws SQLException{

        try {
            dbConnection = DriverManager.getConnection(HOST,USERNAME,PASSWORD);
            System.out.println("Successfully Connected To Database");
            return dbConnection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
