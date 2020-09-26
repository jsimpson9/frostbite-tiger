
USE frostytiger;

DROP TABLE IF EXISTS resourcetype;

#
# Resource types
#
CREATE TABLE resourcetype (
    resource_id     INT             AUTO_INCREMENT,
    resource_name   VARCHAR(64),
    resource_image  VARCHAR(256),

    CONSTRAINT resourcetype_pk          PRIMARY KEY (resource_id),
    CONSTRAINT resourcetype_name_uniq   UNIQUE (resource_name)
);

DROP TABLE IF EXISTS resource;

#
# Resources
#
CREATE TABLE resource (
    type_id         INT             NOT NULL,
    location_id     INT             NOT NULL,
    amount          INT             NOT NULL,

    CONSTRAINT resource_pk          PRIMARY KEY (type_id, location_id),

    CONSTRAINT resource_type_fk
                FOREIGN KEY (type_id) REFERENCES resourcetype (resource_id),

    CONSTRAINT resource_location_fk
               FOREIGN KEY (location_id) REFERENCES worldmap (coord_id)

) ENGINE=MyISAM;


#
# Default resource types
#
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (1, 'Gold', 'images/resource-gold.png' );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (2, 'Wood', 'images/resource-wood.png' );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (3, "Stone", "images/resource-stone.png" );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (4, "Iron", "images/resource-iron.png" );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (5, "Wheat", "images/resource-wheat.png" );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (6, "Fruit", "images/resource-fruit.png" );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (7, "Fish", "images/resource-fish.png" );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (8, "Livestock", "images/resource-livestock.png" );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (10, 'Emerald', 'images/resource-emerald.png' );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (11, 'Jade', 'images/resource-jade.png' );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (12, 'Ruby', 'images/resource-ruby.png' );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (13, 'Sapphire', 'images/resource-sapphire.png' );
INSERT  INTO    resourcetype    (resource_id, resource_name, resource_image)
        VALUES                  (14, 'Pearl', 'images/resource-pearl.png' );


