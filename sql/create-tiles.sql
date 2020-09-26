
USE frostytiger;

DROP TABLE IF EXISTS tiletype;
CREATE TABLE tiletype (
    tile_id         INT             AUTO_INCREMENT,
    tile_name       VARCHAR(64),
    tile_image      VARCHAR(256),

    CONSTRAINT tiletype_pk          PRIMARY KEY (tile_id),
    CONSTRAINT tiletype_name_uniq   UNIQUE (tile_name)
);

#
# Default terrain types
#
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (1, 'Water',    'images/tile-water-64.png');
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (2, 'Grass',    'images/tile-grass.png');
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (3, 'Forest',   'images/tile-bark-64.png');
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (4, 'Dirt',     'images/tile-dirt-64.png');
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (5, 'Snow',     'images/tile-snow-64.png');
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (6, 'Lava',     'images/tile-lava.png');
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (7, 'Volcanic Rock',     'images/tile-volcanic-rock.png');

INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (8, 'Sand',     'images/tile-sand.png');

INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (9, 'Desert',   'images/tile-sand.png');
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (10, 'Jungle',   'images/tile-jungle.png');
INSERT  INTO    tiletype    (tile_id, tile_name, tile_image)
        VALUES              (11, 'Thicket',   'images/tile-bark-64.png');


