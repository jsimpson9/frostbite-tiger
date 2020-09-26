package com.frostytiger;
 
import java.io.*;
import java.util.*;
import java.sql.*;

public class GameObjectManager {

    private static GameObjectManager _instance = null;
    private static boolean _isInited = false;

    public static synchronized GameObjectManager getInstance() {
        if(_instance == null) {
            _instance = new GameObjectManager();
        }
        return _instance;
    }

    private static final String GET_OBJECT_TYPES_SQL =
        "SELECT     objecttype_id," +
        "           objecttype_name," +
        "           objecttype_image," +
        "           objecttype_props " +
        "   FROM    objecttype";

    public GameObjectType[] getGameObjectTypes() {

        List<GameObjectType> ret = new ArrayList<>();

        try {
           
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_OBJECT_TYPES_SQL);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int tId         = rs.getInt("objecttype_id");
                String tName    = rs.getString("objecttype_name");
                String tFile    = rs.getString("objecttype_image");
                String tProps   = rs.getString("objecttype_props");
                ret.add(new GameObjectType(tId, tName, tFile, tProps));
            } 

            rs.close();
            ps.close();

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);
        }
        return ret.toArray(new GameObjectType[0]);
 
    }

    private static final String ADD_OBJECT_TYPE_SQL =
        "INSERT INTO    objecttype (objecttype_name,    " +
        "                           objecttype_image,   " + 
        "                           objecttype_props)   " +
        "       VALUES  (?, ?, ?)";

    public void addGameObjectType() {

    }

}
