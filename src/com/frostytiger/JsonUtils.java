package com.frostytiger;

import java.util.*;
import javax.json.*;

public class JsonUtils {

    public static JsonArray toJsonArray(Jsonable[] arr) {

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(Jsonable j: arr) {
            JsonObject object = j.toJSON();
            arrayBuilder.add(object);
        }
        JsonArray array = arrayBuilder.build();

        return array;
    }

}

