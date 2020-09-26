USE     frostytiger;

#
# Structure types
#
DROP TABLE IF EXISTS structuretype;
CREATE TABLE structuretype (
    structuretype_id         INT             AUTO_INCREMENT,
    structuretype_name       VARCHAR(64),
    structuretype_image      VARCHAR(256),

    CONSTRAINT structuretype_pk         PRIMARY KEY (structuretype_id),
    CONSTRAINT structuretype_uniq       UNIQUE (structuretype_name)
);

#
# Structures
#
DROP TABLE IF EXISTS structure;
CREATE TABLE structure (
    type_id         INT             NOT NULL,
    location_id     INT             NOT NULL,

    CONSTRAINT structure_pk          PRIMARY KEY (location_id),

    CONSTRAINT structure_type_fk
                FOREIGN KEY (type_id) REFERENCES structuretype (structuretype_id),

    CONSTRAINT structure_location_fk
               FOREIGN KEY (location_id) REFERENCES worldmap (coord_id)

) ENGINE=MyISAM;


#
# Default structure types
#
INSERT  INTO    structuretype   
                (structuretype_id, structuretype_name, structuretype_image)
        VALUES                  
                (1, 'Castle',   'images/structure-castle.png');

INSERT  INTO    structuretype   
                (structuretype_id, structuretype_name, structuretype_image)
        VALUES                  
                (2, 'Village',  'images/structure-village.png');

INSERT  INTO    structuretype   
                (structuretype_id, structuretype_name, structuretype_image)
        VALUES                  
                (3, 'Church',  'images/structure-church.png');

INSERT  INTO    structuretype   
                (structuretype_id, structuretype_name, structuretype_image)
        VALUES                  
                (4, 'Windmill',  'images/structure-windmill.png');

INSERT  INTO    structuretype   
                (structuretype_id, structuretype_name, structuretype_image)
        VALUES                  
                (5, 'Barracks',  'images/structure-barracks.png');



