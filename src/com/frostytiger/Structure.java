package com.frostytiger;
 
import javax.json.*;

public class Structure implements Jsonable {

    private int     _typeId;
    private int     _locationId;

    public Structure(int typeId, int locationId) {
        _typeId         = typeId;
        _locationId     = locationId;
    }

    public int      getTypeID()     { return _typeId;       }
    public int      getLocationID() { return _locationId;   }

    public JsonObject toJSON() {
        JsonObject object = Json.createObjectBuilder()
            .add("typeId",      _typeId)
            .add("locationId",  _locationId)
            .build();

        return object;
    }

}

