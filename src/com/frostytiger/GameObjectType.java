package com.frostytiger;
 
import java.io.*;
import javax.json.*;

public class GameObjectType extends BaseNameableEntity {

    private JsonObject _props = null;

    public GameObjectType(int id, String name, String file, String props) {
        super(id, name, file);

        if(props != null && props.length() > 0) {
            JsonReader jsonReader = Json.createReader(new StringReader(props));
            _props = jsonReader.readObject();
        }

    }

    protected void getBaseJSON(JsonObjectBuilder b) {
        super.getBaseJSON(b);
        b.add("props", _props);
    }

}
