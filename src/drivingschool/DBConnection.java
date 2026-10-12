package drivingschool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=DrivingSchoolDB;encrypt=true;trustServerCertificate=true";

    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";

    
    public static Connection getConnection() {

        try {

            Connection connection = DriverManager.getConnection(
                    URL,
                    USERNAME,
                    PASSWORD
            );

            System.out.println("Database connected successfully!");

            return connection;

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());

            return null;
        }
    }


    public static void testConnection() {

        Connection connection = getConnection();

        if (connection != null) {
            System.out.println("TEST PASSED!");
        } else {
            System.out.println("TEST FAILED!");
        }
    }

    // This is only for testing the database connection.
    public static void main(String[] args) {
        testConnection();
    }
}