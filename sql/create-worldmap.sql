
USE frostytiger;

DROP TABLE IF EXISTS worldmap;

#
# worldmap
#
# Note: try using the MyISAM engine to improve deletion
# performance of this large table.
#
CREATE TABLE worldmap (
    coord_id        INT             AUTO_INCREMENT,
    coord_x         INT,
    coord_y         INT,
    base_tile_type  INT,

    CONSTRAINT coord_pk         PRIMARY KEY (coord_id),
    CONSTRAINT tiletype_fk      FOREIGN KEY (base_tile_type) REFERENCES tiletype(tile_id),
    CONSTRAINT coord_uniq       UNIQUE (coord_x, coord_y)

) ENGINE=MyISAM;

