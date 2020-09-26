package com.frostytiger;

import java.sql.*;
import java.util.Date; 

import javax.json.*;

public class User {

    public int      _id = -1;
    public String   _username;
    public String   _password;
    public byte[]   _salt;
    public String   _email;
    public Date     _joindate;

    /**
     *
     * A user without ID. Use this only when attempting to create a new
     * user which will be passed to the database for insertion.
     * Otherwise, a valid user (present in the db) should have a valid
     * id.
     *
     */
    public User(String username, String password, 
                byte[] salt, String email, Date joindate) {
        this(-1, username, password, salt, email, joindate);                
    }

    /**
     *
     * Create a User instance.
     *
     */
    public User(int id, String username, String password, 
                byte[] salt, String email, Date joindate) {

        _id         = id;
        _username   = username;
        _password   = password;
        _salt       = salt;
        _email      = email;
        _joindate   = joindate;
    }

    public int getID()          { return _id;       }
    public String getUsername() { return _username; }
    public String getPassword() { return _password; }
    public byte[] getSalt()     { return _salt;     }
    public String getEmail()    { return _email;    }
    public Date getJoinDate()   { return _joindate; }

    public JsonObject toJSON() {
        JsonObject object = Json.createObjectBuilder()
            .add("id",          _id)
            .add("username",    _username)
            .add("email",       _email)
            .add("joindate",    _joindate.toString() )
            .build();

        return object;
    }

}
