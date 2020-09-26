
USE frostytiger;

DROP TABLE IF EXISTS objecttype;

#
# Object types
#
CREATE TABLE objecttype (
    objecttype_id       INT             AUTO_INCREMENT,
    objecttype_name     VARCHAR(64),
    objecttype_image    VARCHAR(256),
    objecttype_props    TEXT,

    CONSTRAINT objecttype_pk          PRIMARY KEY (objecttype_id),
    CONSTRAINT objecttype_name_uniq   UNIQUE (objecttype_name)
);

DROP TABLE IF EXISTS object;

#
# Objects
#
CREATE TABLE object (
    type_id         INT                 NOT NULL,
    location_id     INT                 NOT NULL,
    object_props    TEXT,

    CONSTRAINT object_pk          PRIMARY KEY (type_id, location_id),

    CONSTRAINT object_type_fk
                FOREIGN KEY (type_id) REFERENCES objecttype (objecttype_id),

    CONSTRAINT object_location_fk
                FOREIGN KEY (location_id) REFERENCES worldmap (coord_id)

) ENGINE=MyISAM;



