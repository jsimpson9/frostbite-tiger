package com.frostytiger;
 
import java.util.*;

public class NameableEntityCache<T extends NameableEntity> {

    private Map<String, T> nameMap = new HashMap<String, T>();
    private Map<Integer, T> idMap = new HashMap<Integer, T>();

    public NameableEntityCache() { }

    public void add(T ne) {
        nameMap.put(ne.getName(), ne);
        idMap.put(ne.getID(), ne);
    }

    public T get(int id)       { 
        return idMap.get(new Integer(id)); 
    }

    public T get(String name)  { 
        return nameMap.get(name); 
    }

}
