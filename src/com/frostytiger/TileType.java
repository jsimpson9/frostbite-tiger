package com.frostytiger;
 
import javax.json.*;
import java.awt.Color;

public class TileType extends BaseNameableEntity {

    private Color   _color = null;

    public TileType(int id, String name, String file) {
        super(id, name, file);
    }

    public Color    getColor()          { return _color; }
    public void     setColor(Color c)   { _color = c; }

    public boolean equals(Object o) {
        if(o instanceof TileType) {
            TileType other = (TileType)o;
            if(other.getID() == getID()) {
                return true;
            }
        }
        return false;
    }

}

