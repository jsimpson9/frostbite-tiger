package com.frostytiger;
 
import java.io.*;
import java.sql.*;
import javax.json.*;

public class UserManager {

    private static final String GET_USER_SQL = 

        "SELECT     id, username, password, salt, email, joindate " +
        "   FROM    user " +
        "   WHERE   username = ?";


    public static User getUser(String name) {

        User user = null;

        try {

            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_USER_SQL);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                int id          = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                byte[] salt     = rs.getBytes("salt");
                String email    = rs.getString("email");
                Date joindate   = rs.getDate("joindate");

                user = new User(id, username, password, salt, email, joindate);
            }

            rs.close();
            ps.close();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return user;
    }

    private static final String ADD_USER_SQL = 

        "INSERT INTO user " +
        "           (username, password, salt, email, joindate) " +
        "   VALUES  (       ?,        ?,    ?,     ?,        ?) ";


    /**
     *
     * Add a new user.
     *
     */
    public static void addUser(User user) {

        try {

            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(ADD_USER_SQL);
            ps.setString(   1, user.getUsername()   );
            ps.setString(   2, user.getPassword()   );
            ps.setBytes(    3, user.getSalt()       );
            ps.setString(   4, user.getEmail()      );
            ps.setDate(     5, 
                            new java.sql.Date(user.getJoinDate().getTime()) );

            ps.executeUpdate();

            ps.close();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

    private static Object lock = new Object();

    //
    // Can synchronize on this rather than dealing with 
    // db transactions for now. 
    //
    public Object getLock() { return lock; }

    //
    // This belongs in a tools class.
    //
    public static void main(String[] args) {

		String username 	= args[0];
		String password 	= args[1];
		String email		= args[2];

       	byte[] salt     = EncryptUtil.getSalt();
       	String encPass  = EncryptUtil.getSHA1Password(password, salt);

       	User user = new User(   username,
                                encPass,
                                salt,
                                email,
                                new java.util.Date() );

        UserManager.addUser(user);
    }

}
