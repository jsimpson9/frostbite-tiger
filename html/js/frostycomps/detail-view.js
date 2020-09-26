
/**
 *
 * A class for the menu system configuration
 *
 */
function DetailView() {
    this.x = -1;
    this.y = -1;
    this.divName = "";
    this.resourceTypes = {};
};

/**
 *
 * Get the resource type with the specified type id.
 *
 */
DetailView.prototype.getResourceType = function(typeId) {
    return this.resourceTypes[typeId];
}

DetailView.prototype.render = function(id) {
    
    // alert("Rendering menu " + id);

    this.divName = id;
    var _this = this;

    $.get(  "js/frostycomps/templates/detail-view.html", function(html) {

        $('#'+id).html(html);

        var menuSystem = objectRegistry.get("menu-system");
        menuSystem.configureTab(_this.divName, _this.reset);
        menuSystem.setPreferredVisibility(_this.divName, false);

        // console.log("DetailView registered with MenuSystem");
    });

}

DetailView.prototype.isOpen = function() {
    return $('#'+this.divName).is(":visible") && this.x != -1 && this.y != -1;
}

DetailView.prototype.close = function() {
    this.reset();
    $('#'+this.divName).hide();
}

DetailView.prototype.reset = function() {
    this.x = -1;
    this.y = -1;
    $("#detail-view-info").empty();
    $("#detail-view-resources-table").empty();
    $("#detail-view-structures-table").empty();
    var dMap = objectRegistry.get('detail-map');
    dMap.removeAnchoredSelector();
}

DetailView.prototype.getX = function() {
    return this.x;
}

DetailView.prototype.getY  = function() {
    return this.y;
}

DetailView.prototype.showLocation = function(x, y) {

    $('#'+this.divName).show();

    this.x = x;
    this.y = y;

    var dMap = objectRegistry.get("detail-map");
    var tile = dMap.getMapTile(x,y);
    var typeId = tile.type;
    var tileType = dMap.getMapTileType(typeId);

    var structView = objectRegistry.get("construction-view");

    var resources   = tile.resources;
    var structures  = tile.structures;

    $("#detail-view-info").html(
                "Location: " + tile.x + ", " + tile.y + "<br/>" +
                "Type:     " + tileType.name + "<br/>");

    //
    // List resources
    //
    var resTableData = "<tr><td colspan='3'>Resources</td></tr>";
    for(var i = 0; i < resources.length; i++) {
        resTableData += 
            "<tr>       " + 
            "   <td>    " + 
            "       <img width='32' height='32' src='" +
                            this.resourceTypes[ resources[i].typeId ].file + "' />" +
            "   </td>   " +
            "   <td>" +
                    this.resourceTypes[ resources[i].typeId ].name +
            "   </td>   " +
            "   <td>" + 
                    // (Math.floor((Math.random() * 101) + 1) - 1) +
                    resources[i].amount + 
            "   </td>   " +
            "</tr>      ";
    }

    //
    // List structures
    //
    var structTableData = "<tr><td colspan='2'>Structures</td></tr>";
    for(var i = 0; i < structures.length; i++) {
        var structure = structures[i];
        var structType = structView.getStructureType(structure.typeId);
        structTableData += 
            "<tr>       " + 
            "   <td>    " + 
            "       <img width='32' height='32' src='" +
                            structType.file + "' />" +
            "   </td>   " +
            "   <td>" +
                            structType.name +
            "   </td>   " +
            "</tr>      ";
    }

    $("#detail-view-resources-table").html(resTableData);

    $("#detail-view-structures-table").html(structTableData);

}

DetailView.prototype.loadResourceTypes = function() {

    // alert("Loading resource types...");

    loadUtil(   "getResourceTypes",
                this.resourceTypes  );

    // alert("Loaded resource types " + Object.keys(this.resourceTypes).length );

};

