package com.frostytiger;
 
import javax.json.*;

public class BaseNameableEntity implements Jsonable, NameableEntity {

    protected int     _id;
    protected String  _name;
    protected String  _file;

    public BaseNameableEntity(int id, String name, String file) {
        _id     = id;
        _name   = name;
        _file   = file;
    }

    public int      getID()     { return _id; }
    public String   getName()   { return _name; }
    public String   getFile()   { return _file; }

    protected void getBaseJSON(JsonObjectBuilder b) {
        b.add("id",          _id);
        b.add("name",        _name);
        b.add("file",        _file );
    }

   /**
    *
    * Subclasses can override this method to add data applicable to their
    * specific entity type. 
    *
    *
    */
    public JsonObject toJSON() {
        JsonObjectBuilder b = Json.createObjectBuilder();
        getBaseJSON(b);
        JsonObject object = b.build();
        return object;
    }

}

