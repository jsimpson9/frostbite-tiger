package com.frostytiger;
 
import java.io.*;
import java.sql.*;
import java.util.*;

public class DatabaseConnection {

    public static Connection _conn = null;

    public static synchronized Connection getConnection() {

        if(_conn != null) {
            return _conn;
        }

        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();

        } catch (Exception ex) {
            // handle the error
            System.out.println("ERROR: " + ex);
            ex.printStackTrace();
        }

        Properties props = new Properties();

        try {

            //
            // Load properties file
            //
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream input = cl.getResourceAsStream("frosty.properties");
            if(input != null) {
                //
                // Support DB connection in servlet context.
                //
                props.load(input);
            } else {
                //
                // Support DB connection in non-servlet context
                //
                FileInputStream fis = new FileInputStream("work/frosty.properties");
                props.load(fis);
            }

        } catch (IOException ex) {
            // handle the error
            System.out.println("ERROR: " + ex);
            ex.printStackTrace();
        }


        String dbHost =     props.getProperty("DB_HOST");
        String dbPort =     props.getProperty("DB_PORT");
        String dbName =     props.getProperty("DB_NAME");
        String dbUsername = props.getProperty("DB_USER");
        String dbPassword = props.getProperty("DB_PASS");

        try {

            String connUrl = 
                "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName +
                "?rewriteBatchedStatements=true" +
                "&autoReconnect=true" +
                "&useSSL=false";

            _conn = DriverManager.getConnection(
                                    connUrl, dbUsername, dbPassword);

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("SQLException: " + ex.getMessage()   + " "
                                                + ex.getSQLState()  + " " 
                                                + ex.getErrorCode());
        }

        return _conn;
    }

}
