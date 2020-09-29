package com.frostytiger;
 
import java.io.*;
import java.util.*;
import java.sql.*;
import java.util.regex.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MapManager {

    private static final String IMAGE_BASE_DIR = "html";

    private static MapManager _instance = null;
    private static boolean _isInited = false;

    public static synchronized MapManager getInstance() {
        if(_instance == null) {
            _instance = new MapManager();
        }
        return _instance;
    }

    private NameableEntityCache<TileType> tileTypeCache = 
                                    new NameableEntityCache<TileType>();

    private NameableEntityCache<ResourceType> resourceTypeCache = 
                                    new NameableEntityCache<ResourceType>();

    private MapManager() {

    }

    public static void main(String[] args) {

        MapManager mapManager = MapManager.getInstance();

        if(args[0].equals("generate-map")) {

            System.out.println("Start: " + new java.util.Date());

            if(args.length == 1) {

                System.out.println("Generating map with default params...");

                // createMap(   8, 6, 4, 2, 1024, 768, 
                //              "html/images/world-map.png" );

                mapManager.createMap(  9, 6, 4, 2, 2000, 2000,
                                        "html/images/world-map.png" );
 
            } else {

                System.out.println("Generating map with custom params...");

                mapManager.createMap(  
                                Integer.parseInt(args[1]), 
                                Integer.parseInt(args[2]), 
                                Integer.parseInt(args[3]), 
                                Float.parseFloat(args[4]), 
                                Integer.parseInt(args[5]), 
                                Integer.parseInt(args[6]), 
                                args[7] );
            }

            System.out.println("End  : " + new java.util.Date());
        }

        if(args[0].equals("generate-image")) {
            System.out.println("Generating image...");
        }

    }
    
    //
    // Map a tile type to an elevation
    //
    private static class TileTypeElevation {
        TileType    _t;
        float       _elevation;

        public TileTypeElevation(TileType t, float elevation) {
            _t = t;
            _elevation = elevation;
        }

        public TileType getTileType() { return _t; }
        public float    getElevation() { return _elevation; }

    }
 
    //
    // Map default terrain types into values returned by the
    // midpoint displacement (diamond square) algorithm. 
    //
    private static final List<TileTypeElevation> TILE_LIST = 
                                    new ArrayList<TileTypeElevation>();

    //
    // Maps terrain tile type to resources available on that terrain tile type
    //
    private static final Map<TileType,ResourceType[]> RESOURCE_MAP = 
                                    new HashMap<TileType,ResourceType[]>();

   
    //
    // Load tiles from DB and configure map generation params.
    //
    private void init() {
        
        if(_isInited) {
            return;
        }

        TileType[] tileTypes = getTileTypes();
        for(TileType t: tileTypes) {
            tileTypeCache.add(t);
        }

        ResourceType[] resourceTypes = getResourceTypes();
        for(ResourceType t: resourceTypes) {
            resourceTypeCache.add(t);
        }

        //
        // Base tiles
        //
        TileType lava       = tileTypeCache.get("Lava");
        TileType volcanic   = tileTypeCache.get("Volcanic Rock");
        TileType water      = tileTypeCache.get("Water");
        TileType sand       = tileTypeCache.get("Sand");
        TileType grass      = tileTypeCache.get("Grass");
        TileType dirt       = tileTypeCache.get("Dirt");
        TileType forest     = tileTypeCache.get("Forest");
        TileType snow       = tileTypeCache.get("Snow");

        //
        // Temperature modded tiles
        //
        TileType desert  = tileTypeCache.get("Desert");
        TileType jungle  = tileTypeCache.get("Jungle");
        TileType thicket = tileTypeCache.get("Thicket");
       
        //
        // Resources
        //
        ResourceType gold =         resourceTypeCache.get("Gold");
        ResourceType wood =         resourceTypeCache.get("Wood");
        ResourceType stone =        resourceTypeCache.get("Stone");
        ResourceType iron =         resourceTypeCache.get("Iron");
        ResourceType wheat =        resourceTypeCache.get("Wheat");
        ResourceType fruit =        resourceTypeCache.get("Fruit");
        ResourceType fish =         resourceTypeCache.get("Fish");
        ResourceType livestock =    resourceTypeCache.get("Livestock");

        ResourceType emerald =      resourceTypeCache.get("Emerald");
        ResourceType jade =         resourceTypeCache.get("Jade");
        ResourceType ruby =         resourceTypeCache.get("Ruby");
        ResourceType sapphire =     resourceTypeCache.get("Sapphire");
        ResourceType pearl =        resourceTypeCache.get("Pearl");

        RESOURCE_MAP.put(lava,      new ResourceType[] { } );
        RESOURCE_MAP.put(volcanic,  new ResourceType[] { ruby, sapphire, jade } );
        RESOURCE_MAP.put(sand,      new ResourceType[] { emerald } );
        RESOURCE_MAP.put(water,     new ResourceType[] { fish, pearl } );
        RESOURCE_MAP.put(grass,     new ResourceType[] { gold, wood, stone, 
                                                            iron, wheat, fruit,
                                                            livestock } );
        RESOURCE_MAP.put(dirt,      new ResourceType[] { gold, stone, iron } );
        RESOURCE_MAP.put(forest,    new ResourceType[] { wood, wood, wood, 
                                                            gold, fruit, 
                                                            emerald       } );
        RESOURCE_MAP.put(snow,      new ResourceType[] { wood, sapphire } );
        RESOURCE_MAP.put(desert,    new ResourceType[] { stone, } );
        RESOURCE_MAP.put(jungle,    new ResourceType[] { wood, gold, fruit } );
        RESOURCE_MAP.put(thicket,   new ResourceType[] { wood } );

        //
        // For use in map generation
        //
        TILE_LIST.add(new TileTypeElevation(lava,       .05f     ));
        TILE_LIST.add(new TileTypeElevation(volcanic,   .10f     ));
        TILE_LIST.add(new TileTypeElevation(sand,       .12f     ));
        TILE_LIST.add(new TileTypeElevation(water,      .40f     ));
        TILE_LIST.add(new TileTypeElevation(sand,       .42f     ));
        TILE_LIST.add(new TileTypeElevation(grass,      .6f     ));
        TILE_LIST.add(new TileTypeElevation(dirt,       .7f     ));
        TILE_LIST.add(new TileTypeElevation(forest,     .9f     ));
        TILE_LIST.add(new TileTypeElevation(snow,       1f      ));

        _isInited = true;
    }

    private static class ResMapWrapper {
        private int[][] _map;
        private int[][] _amounts;
        public ResMapWrapper(int[][] map, int[][] amounts) {
            _map        = map;
            _amounts    = amounts;
        }
        public int[][] getMap()     { return _map;      }
        public int[][] getAmounts() { return _amounts;  }
    }

    private static final String DELETE_MAP_SQL =
        "DELETE FROM worldmap";

    private static final String DELETE_RESOURCES_SQL =
        "DELETE FROM resource";

    private static final String ADD_COORD_SQL = 
        "INSERT INTO worldmap   (coord_x, coord_y, base_tile_type) " +
        "       VALUES          (?, ?, ?)";

    public void createMap(          int detail, 
                                    int widthMult, 
                                    int heightMult, 
                                    float smoothness, 
                                    int cropWidth, 
                                    int cropHeight,
                                    String filename ) {

        init();

        //
        // Used fixed thresholds for obtaining discrete values
        // to map into indexes for this like determining resource 
        // availability.
        //
        float[] fixedThresholds = { .1f, .2f, .3f, .4f, .5f, 
                                    .6f, .7f, .8f, .9f, 1f      };

        float[] elevationThresholds = new float[TILE_LIST.size()];

        for(int i = 0; i < TILE_LIST.size(); i++) {
            elevationThresholds[i] = TILE_LIST.get(i).getElevation();
        }

        MidpointDisplacement md = 
            new MidpointDisplacement(   detail, 
                                        widthMult, 
                                        heightMult, 
                                        smoothness );

        System.out.println("DEBUG Creating main map...");

        int[][] map = md.getMap(elevationThresholds);

        System.out.println("DEBUG Creating temperature map...");

        int[][] temperatureMap      = md.getMap(fixedThresholds);


        List<ResMapWrapper> resourceMaps = new ArrayList<ResMapWrapper>();    

        for(int i = 0; i < 2; i++) {

            System.out.println("DEBUG Creating resource map..." + i);
            int[][] resourceTypeMap     = md.getMap(fixedThresholds);

            System.out.println("DEBUG Creating resource amount map...");
            int[][] resourceAmountMap   = md.getMap(fixedThresholds);

            resourceMaps.add(
                new ResMapWrapper(resourceTypeMap, resourceAmountMap));
        }

        int genWidth    = md.getWidth();
        int genHeight   = md.getHeight();

        System.out.println(
            "Generated map: width " + genWidth + " height " + genHeight);
        System.out.println(
            "Desired size : width " + cropWidth + " height " + cropHeight);


        //
        // See if we can crop the map ???
        //
        if(cropHeight > genHeight)  { cropHeight = genHeight; }
        if(cropWidth > genWidth)    { cropWidth = genWidth; }

        BufferedImage img = new BufferedImage(  cropWidth, 
                                                cropHeight, 
                                                BufferedImage.TYPE_INT_ARGB);

        try {
           
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = null;

            System.out.println("Performing deletion of existing world map...");

            ps = conn.prepareStatement(DELETE_MAP_SQL);
            ps.executeUpdate();
            ps.close();

            System.out.println("Performing deletion of existing resources...");

            ps = conn.prepareStatement(DELETE_RESOURCES_SQL);
            ps.executeUpdate();
            ps.close();

            System.out.println("Deletion completed...");
            
            //
            // Prepare a statement for batch insertion.
            //
            ps = conn.prepareStatement(ADD_COORD_SQL);

            // File csv = new File(filename + ".csv");
            // FileWriter fw = new FileWriter(csv);

            int count = 0;

            System.out.println("Begin batching inserts...");

            for(int y = 0; y < cropHeight; y++) {
                for(int x = 0; x < cropWidth; x++) {

                    int tileIndex = map[x][y];
                    TileType tileInfo = TILE_LIST.get(tileIndex).getTileType();
        
                    //
                    // TODO somehow make this more configurable 
                    // (like some kind of plugin / callback / hook 
                    // mechanism, since there is a lot of customization
                    // that can happen here and specific knowledge of
                    // tile gen impl. 
                    //
                    TileType lava       = tileTypeCache.get("Lava");
                    TileType volcanic   = tileTypeCache.get("Volcanic Rock");
                    TileType water      = tileTypeCache.get("Water");
                    TileType sand       = tileTypeCache.get("Sand");
                    TileType grass      = tileTypeCache.get("Grass");
                    TileType dirt       = tileTypeCache.get("Dirt");
                    TileType forest     = tileTypeCache.get("Forest");
                    TileType snow       = tileTypeCache.get("Snow");

                    TileType desert  = tileTypeCache.get("Desert");
                    TileType jungle  = tileTypeCache.get("Jungle");
                    TileType thicket = tileTypeCache.get("Thicket");

                    //
                    // Deserts
                    //
                    if( !tileInfo.equals(water) && !tileInfo.equals(sand) &&
                        !tileInfo.equals(forest) && !tileInfo.equals(snow) &&
                        !tileInfo.equals(lava) ) {

                        //
                        // Check remaining tile types for desertification.
                        //
                        int temperature = temperatureMap[x][y];
                        if(temperature > 5) {
                            tileInfo = jungle;
                        }
                        if(temperature > 7) {
                            tileInfo = desert;
                        }
                    }
       
                    //
                    // Expand forests
                    //
                    if( !tileInfo.equals(water) && 
                        !tileInfo.equals(forest) && 
                        !tileInfo.equals(snow) &&
                        !tileInfo.equals(lava) ) {
                        
                        int temperature = temperatureMap[x][y];
                        if(temperature < 1) {
                            tileInfo = thicket;
                        }
                    }

                    // if(x != 0) { fw.write(","); }
                    // fw.write(""+tileType);
                    // System.out.println("INFO adding tile " + tileInfo.getName());


                    Color c = tileInfo.getColor();

                    if(c == null) {
                        //
                        // Determine color of this tile and set it.
                        //
                        File file = new File(   IMAGE_BASE_DIR + "/" + 
                                                tileInfo.getFile() );

                        BufferedImage orig = ImageIO.read(file);
                        BufferedImage scaled = 
                                        new BufferedImage(
                                            1, 1, BufferedImage.TYPE_INT_RGB);

                        Graphics2D g = scaled.createGraphics();
                        g.drawImage(orig, 0, 0, 1, 1, null); 
                        g.dispose();
                        int color = orig.getRGB(0, 0);

                        int red = (color & 0x00ff0000) >> 16;
                        int green = (color & 0x0000ff00) >> 8;
                        int blue = color & 0x000000ff;

                        tileInfo.setColor(new Color(red, green, blue));

                        // 
                        // Debug
                        //
                        System.out.println("Generated color for " + 
                                            tileInfo.getName() + " " + 
                                            red + " " + green + " " + blue);

                    }

                    int a = 255;  // alpha
                    int r = tileInfo.getColor().getRed();       // red
                    int g = tileInfo.getColor().getGreen();     // green
                    int b = tileInfo.getColor().getBlue();      // blue
     
                    int p = (a<<24) | (r<<16) | (g<<8) | b;     // pixel
     
                    img.setRGB(x, y, p);

                    //
                    // Add to sql batch
                    //
                    ps.setInt(1, x);
                    ps.setInt(2, y);
                    ps.setInt(3, tileInfo.getID());
                    ps.addBatch();
                    count++;

                    //
                    // Check for batch readiness.
                    //
                    if( count % 250_000 == 0 || 
                        count == cropWidth * cropHeight     ) {

                        ps.executeBatch();
                    }

                }

                // fw.write("\n");
            }

            System.out.println("Finished batching inserts...");

            ps.close();

            //
            // Populate resources...
            //
            System.out.println("Populating resources...");
            System.out.println("Start: " + new java.util.Date());

            Tile[][] worldMap = getMapSection(0,0,cropWidth,cropHeight);

            //
            // Prepare a new SQL statement. Reset count;
            //
            ps = conn.prepareStatement(ADD_RESOURCE_SQL);
            count = 0;

            for(int y = 0; y < cropHeight; y++) {

                for(int x = 0; x < cropWidth; x++) {

                    Tile tile = worldMap[x][y];
                    TileType tileType = tileTypeCache.get(tile.getType());

                    Map<ResourceType, Resource> countMap = 
                                    new HashMap<ResourceType, Resource>();

                    //
                    // Determine resources for this tile
                    //
                    ResourceType[] resTypes = RESOURCE_MAP.get(tileType);
                    if(resTypes != null && resTypes.length > 0) {
                        for(int i = 0; i < resourceMaps.size(); i++) {
                            ResMapWrapper wrapper = resourceMaps.get(i);
                            int[][] typeMap = wrapper.getMap();
                            int[][] amounts = wrapper.getAmounts();
                            int index = typeMap[x][y];
                            int mod = index % resTypes.length;
                            ResourceType resType = resTypes[mod];
                            int amount = amounts[x][y];
                            Resource r = countMap.get(resType);
                            if(r == null) { 
                                r = new Resource(   resType.getID(), 
                                                    tile.getID(), 
                                                    0 );
                            }
                            r.setAmount( r.getAmount() + (amount+1) );
                            countMap.put(resType, r);
                        }
                    }

                    Set<ResourceType> keys = countMap.keySet();
                    for(ResourceType key: keys) {
                        Resource r = countMap.get(key);
 
                        ps.setInt(1, r.getLocationID());
                        ps.setInt(2, r.getTypeID());
                        ps.setInt(3, r.getAmount());
                        ps.addBatch();
                        count++;

                        if( count % 250_000 == 0 ) {

                            // System.out.println("Executing resource SQL batch...");
                            ps.executeBatch();
                        }

                    }
                }
            }

            ps.executeBatch();
            ps.close();

            System.out.println("End  : " + new java.util.Date());

            System.out.println("Writing image...");

            // fw.close();

            File file = new File(filename);
            ImageIO.write(img, "png", file);

            System.out.println("Wrote image...");

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);

        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Error: " + e);

        }

    }



    private static final String SET_RESOURCE_SQL =
        "UPDATE     resource    " +
        "   SET     amount = ?  " + 
        "   WHERE   location_id = ? AND type_id = ?";


    public void setResource(int tileId, int resTypeId, Resource res) {

        try {
          
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(SET_RESOURCE_SQL);
            ps.setInt(1, res.getAmount());
            ps.setInt(2, res.getLocationID());
            ps.setInt(3, res.getTypeID());
            ps.executeUpdate();
            ps.close();

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);
        }
    }


    private static final String GET_RESOURCE_SQL =
        "SELECT     amount      " + 
        "   FROM    resource    " +
        "   WHERE   location_id = ? AND type_id = ?";

    public Resource getResource(int locationId, int resTypeId) {
        
        Resource ret = null;

        try {

            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_RESOURCE_SQL);
            ps.setInt(1, locationId);
            ps.setInt(2, resTypeId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                int amount = rs.getInt(1);
                ret = new Resource(locationId, resTypeId, amount);
            } 

            rs.close();
            ps.close();

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);
        }
   
        return ret;
    }


    private static final String ADD_RESOURCE_SQL =
        "INSERT INTO resource (location_id, type_id, amount)" + 
        "   VALUES              (?, ?, ?)";

    public void addResource(int tileId, int resTypeId, int amount) {

        try {
         
            Resource res = getResource(tileId, resTypeId);

            if(res == null) {

                Connection conn = DatabaseConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(ADD_RESOURCE_SQL);

                ps.setInt(1, tileId);
                ps.setInt(2, resTypeId);
                ps.setInt(3, amount);
                ps.executeUpdate();

                ps.close();

            } else {
                res.setAmount(res.getAmount()+amount);
                setResource(tileId, resTypeId, res);
            }

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);
        }
    }


    private static final String GET_TILE_TYPES_SQL =
        "SELECT     tile_id, tile_name, tile_image " +
        "   FROM    tiletype";

    public TileType[] getTileTypes() {

        List<TileType> ret = new ArrayList<TileType>();

        try {
           
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_TILE_TYPES_SQL);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int tId         = rs.getInt("tile_id");
                String tName    = rs.getString("tile_name");
                String tFile    = rs.getString("tile_image");
                ret.add(new TileType(tId, tName, tFile));
            } 

            rs.close();
            ps.close();

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);
        }
        return ret.toArray(new TileType[0]);
    }

    private static final String GET_RESOURCE_TYPES_SQL =
        "SELECT     resource_id, resource_name, resource_image " +
        "   FROM    resourcetype";

    public ResourceType[] getResourceTypes() {

        List<ResourceType> ret = new ArrayList<ResourceType>();

        try {
           
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_RESOURCE_TYPES_SQL);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int tId         = rs.getInt("resource_id");
                String tName    = rs.getString("resource_name");
                String tFile    = rs.getString("resource_image");
                ret.add(new ResourceType(tId, tName, tFile));
            } 

            rs.close();
            ps.close();

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);
        }
        return ret.toArray(new ResourceType[0]);
 
    }

    private static final String ADD_STRUCTURE_SQL =
        "INSERT INTO    structure (type_id, location_id) " +
        "       VALUES  (?, ?)";

    public void addStructure(int typeId, int locationId) 
                                            throws ServiceException {

        try {
         
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(ADD_STRUCTURE_SQL);

            ps.setInt(1, typeId);
            ps.setInt(2, locationId);
            ps.executeUpdate();

            ps.close();

        } catch(SQLException sqle){

            sqle.printStackTrace();
            System.out.println("Error: " + sqle);

            //
            // Check for multiple structures on this tile
            // 
            // Match: Duplicate entry 'xxx' for key 'PRIMARY'
            //
            Pattern p = Pattern.compile("Duplicate entry '\\d+' for key 'PRIMARY'");
            Matcher m = p.matcher(sqle.getMessage());
            if(m.matches()) {
                throw new ServiceException("There is already a structure on this tile.", sqle);

            } 

            throw new ServiceException("Unknown error adding structure: " + sqle.getMessage(), sqle);

        }

    }


    private static final String GET_STRUCTURE_TYPES_SQL =
        "SELECT     structuretype_id, structuretype_name, structuretype_image " +
        "   FROM    structuretype";

    public StructureType[] getStructureTypes() {

        List<StructureType> ret = new ArrayList<StructureType>();

        try {
           
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_STRUCTURE_TYPES_SQL);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                int tId         = rs.getInt("structuretype_id");
                String tName    = rs.getString("structuretype_name");
                String tFile    = rs.getString("structuretype_image");
                ret.add(new StructureType(tId, tName, tFile));
            } 

            rs.close();
            ps.close();

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);
        }
        return ret.toArray(new StructureType[0]);
 
    }



    private static final String GET_SECTION_SQL = 
        "SELECT     coord_id, coord_x, coord_y, base_tile_type, " +
        "           resource.type_id, resource.amount, " +
        "           structure.type_id " +
        "   FROM            worldmap " + 
        "   LEFT JOIN       resource " +
        "           ON      worldmap.coord_id = resource.location_id " +
        "   LEFT JOIN       structure " +
        "           ON      worldmap.coord_id = structure.location_id " +
        "   WHERE   coord_x >= ? AND coord_x < ? AND " +
        "           coord_y >= ? AND coord_y < ? ";

    public Tile[][] getMapSection(int x, int y, int width, int height) {
       
        Tile[][] ret = new Tile[width][height];

        try {
           
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(GET_SECTION_SQL);
            ps.setInt(1, x);
            ps.setInt(2, x + width);
            ps.setInt(3, y);
            ps.setInt(4, y + height);
            
            ResultSet rs = ps.executeQuery();

            // int debugCount = 0;
            // System.out.print("Adding tile ");

            while(rs.next()) {

                int rId      = rs.getInt("coord_id");
                int rX       = rs.getInt("coord_x");
                int rY       = rs.getInt("coord_y");
                int rType    = rs.getInt("base_tile_type");

                Tile t = ret[rX - x][rY - y];

                if(t == null) {
                    t = new Tile(rId, rX, rY, rType);
                    ret[rX - x][rY - y] = t;

                    // debugCount++;
                }

                // System.out.print(t.toString());

                //
                // Check for non-null on the right hand side of our
                // left join (i.e. the tile contains resources)
                //
                if(rs.getObject("resource.type_id") != null) {

                    int resTypeId       = rs.getInt("resource.type_id");
                    int resLocationId   = rId;
                    int resAmount       = rs.getInt("resource.amount");

                    t.addResource( 
                        new Resource(resTypeId, resLocationId, resAmount) );
                }

                //
                // Check for non-null on the right hand side of our
                // left join (i.e. the tile contains structure(s?) )
                //
                if(rs.getObject("structure.type_id") != null) {

                    int sTypeId       = rs.getInt("structure.type_id");
                    int sLocationId   = rId;

                    t.addStructure( 
                        new Structure(sTypeId, sLocationId) );
                }


            }

            // System.out.println("");
            
            rs.close();
            ps.close();

        } catch(SQLException sqle){
            sqle.printStackTrace();
            System.out.println("Error: " + sqle);
        }

        return ret;
    }

}
