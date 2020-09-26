package com.frostytiger;
 
import javax.json.*;

public class Resource implements Jsonable {

    private int     _typeId;
    private int     _locationId;
    private int     _amount;

    public Resource(int typeId, int locationId, int amount) {
        _typeId         = typeId;
        _locationId     = locationId;
        _amount         = amount;
    }

    public int      getTypeID()     { return _typeId;       }
    public int      getLocationID() { return _locationId;   }

    public int      getAmount()         { return _amount;   }
    public void     setAmount(int i)    { _amount = i;      }

    public JsonObject toJSON() {
        JsonObject object = Json.createObjectBuilder()
            .add("typeId",      _typeId)
            .add("locationId",  _locationId)
            .add("amount",      _amount)
            .build();

        return object;
    }

}

