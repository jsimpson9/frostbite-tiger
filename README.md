# frostbite-tiger

```
#
# Location of war file 
#
The generated war file can be copied to (or symlinked in)
/var/lib/tomcat9/webapps/

#
# Generating world map
#
To generate maps, from the cli, run:
java com.frostytiger.MapManager generate-map 8 6 4 2 1024 768 work/test-map.png
java com.frostytiger.MapManager generate-map 9 6 4 2 2000 2000 work/test-map.png

This is a good set of thresholds for map generation

        TILE_LIST.add(new TileTypeElevation(lava,       .05f     ));
        TILE_LIST.add(new TileTypeElevation(volcanic,   .10f     ));
        TILE_LIST.add(new TileTypeElevation(sand,       .12f     ));
        TILE_LIST.add(new TileTypeElevation(water,      .40f     ));
        TILE_LIST.add(new TileTypeElevation(sand,       .42f     ));
        TILE_LIST.add(new TileTypeElevation(grass,      .6f     ));
        TILE_LIST.add(new TileTypeElevation(dirt,       .7f     ));
        TILE_LIST.add(new TileTypeElevation(forest,     .9f     ));
        TILE_LIST.add(new TileTypeElevation(snow,       1f      ));


#
# Dev login
#
    username: xxx
    password: xxx


#
# Recovering from MySQL issue where root cannot login.
#
    sudo systemctl stop mysql.service
    sudo mkdir -p /var/run/mysqld
    sudo chown mysql:mysql /var/run/mysqld
    sudo mysqld_safe --skip-grant-tables & 
    mysql -u root


#
# Adding a UI view component.
# See an example with template-view.js and template-view.html
#
- Create .js and .html files:
    html/js/frostycomps/something-view.js
    html/js/frostycomps/templates/something-view.html

- In the view .js, define the class SomethingView and the render() method.

- In home.html add a script tag with a src attribute pointing to the new
  view file. E.g.
        <script type="text/javascript"
                src="js/frostycomps/something-view.js">
        </script>

- In home.html, register the view with the object registry:
        objectRegistry.register("something-view", new SomethingView() );

- In home.html, add a td/div for the view and call the render() function:

        <div id='something-view-div' class="outline"> 
        </div>

        <script type="text/javascript">
            objectRegistry.get("something-view").render("something-view-div");
        </script>

- In html/js/frostycomps/templates/menu-system.html, add a link onto the
    menu bar to toggle your view:
           <a id='something-view-div-toggle' href='#'>Something</a> &nbsp;

```


