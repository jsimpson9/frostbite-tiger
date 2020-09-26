package com.frostytiger;

import java.util.*;
import javax.json.*;

public class Tile implements Jsonable {

    private int                 _id;
    private int                 _x;
    private int                 _y;
    private int                 _type;

    private Map<Integer, Resource>  _resMap = new HashMap<Integer, Resource>();
    private Map<Integer, Structure> _structMap = new HashMap<Integer, Structure>();


    public Tile(int id, int x, int y, int type) {
        _id     = id;
        _x      = x;
        _y      = y;
        _type   = type;
    }

    public int getID()      { return _id;   }
    public int getX()       { return _x;    }
    public int getY()       { return _y;    }
    public int getType()    { return _type; }

    public void addResource(Resource r) {
        _resMap.put(r.getTypeID(), r);
    }

    public Resource[] getResources() {
        List<Resource> resources  = new ArrayList<Resource>();
        for(Integer i: _resMap.keySet()) {
            resources.add(_resMap.get(i));
        }
        return (Resource[])resources.toArray(new Resource[0]);
    }

    public void addStructure(Structure s) {
        _structMap.put(s.getTypeID(), s);
    }

    public Structure[] getStructures() {
        List<Structure> structures = new ArrayList<Structure>();
        for(Integer i: _structMap.keySet()) {
            structures.add(_structMap.get(i));
        }
        return (Structure[])structures.toArray(new Structure[0]);
    }

    public JsonObject toJSON() {

        JsonObject object = Json.createObjectBuilder()
            .add("id",          _id)
            .add("x",           _x)
            .add("y",           _y)
            .add("type",        _type)
            .add("resources",   JsonUtils.toJsonArray( getResources() ) )
            .add("structures",  JsonUtils.toJsonArray( getStructures() ) )
            .build();

        return object;
    }

    public String toString() { return toJSON().toString(); }

}

