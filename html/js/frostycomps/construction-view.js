
/**
 *
 * A class for the world map component
 *
 */
function ConstructionView() { 
    this.divName = "";
    this.structureTypes = {};
};

ConstructionView.prototype.render = function(id) {
    
    // alert("Rendering menu " + id);

    this.divName = id;

    var _this = this;

    $.get(  "js/frostycomps/templates/construction-view.html", function(html) {

        //
        // Set html from template
        //
        $('#'+id).html(html);

        //
        // Configure tab with menu system
        //
        var menuSystem = objectRegistry.get("menu-system");
        menuSystem.configureTab(_this.divName);
        menuSystem.setPreferredVisibility(_this.divName, false);

    });

};

/**
 *
 * Return the StructureType with the specified id structTypeId
 *
 */
ConstructionView.prototype.getStructureType = function(structTypeId) {
    return this.structureTypes[structTypeId];
}

ConstructionView.prototype.loadStructureTypes = function() {

    loadUtil(   "getStructureTypes", 
                this.structureTypes ); 

    // alert("Loaded structure types " + Object.keys(this.structureTypes).length );

    var keys = Object.keys(this.structureTypes);
    var size = keys.length;

    var tabledata = "";

    //
    // Table headers
    //
    tabledata += 
        "<tr>       " + 
        "   <td>Structure   " + 
        "   </td>           " + 
        "   <td>Name        " + 
        "   </td>           " + 
        "   <td>Cost        " + 
        "   </td>           " + 
        "</tr>              ";
        
    for(var i = 0; i < keys.length; i++) {
        tabledata += 
            "<tr>                                                   " + 
            "   <td id='construction-view-table-" + keys[i] + "'    " +
            "       style='cursor: pointer'>                        " +
            "           <img width='32' height='32'                 " +
            "               src='" + this.structureTypes[ keys[i] ].file + "'></img>" +
            "   </td>   " +
            "   <td>"   +
                    this.structureTypes[ keys[i] ].name +
            "   </td>   " +
            "   <td>100" + 
            "   </td>   " +
            "</tr>      ";
    }
                    
    $("#construction-view-table").html(tabledata);

    var _this = this;

    for(var i = 0; i < keys.length; i++) {

        (function(_key, _o) {    // Get a local copy of keys[i]

            $("#construction-view-table-"+_key).click( function() {
                var dMap = objectRegistry.get("detail-map");
                dMap.setMode(   dMap.CONSTRUCT_MODE, 
                                { structure: _o } );
            });

        })(keys[i], _this.structureTypes[ keys[i] ] );

    }

};

