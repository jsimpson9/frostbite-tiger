/**
 *
 * A class for the detail map component
 *
 */
function DetailMap() { 
    this.divName        = "";

    this.mapTileTypes   = {};
    this.mapTiles       = [];

    this.mode           = this.VIEW_MODE;
    this.modeParams     = {};

    this.dragging       = false;

    //
    // Currently viewed coords (top left)
    //
    this.currentX = -1;
    this.currentY = -1;

};

DetailMap.prototype.VIEW_MODE       = "View";
DetailMap.prototype.CONSTRUCT_MODE  = "Construct";

/*
var down    = false;
var dragged = false;
DetailMap.prototype.mousedown = function(x,y) { down = true; };
DetailMap.prototype.mousemove = function(x,y) { if(down) { dragged = true; } };
DetailMap.prototype.mouseup = function(x,y) { 
    alert("Mouse up " + x + " " + y + " " + " dragged " + dragged);
    down = false; 
    dragged = false; 
};
*/

DetailMap.prototype.render = function(id) {
    
    // alert("Rendering " + id);

    this.divName = id;

    var _this = this;

    $.get(  "js/frostycomps/templates/detail-map.html", function(html) {

        //
        // Set html from template
        //
        $('#'+id).html(html);

        var menuSystem = objectRegistry.get("menu-system");
        menuSystem.configureTab(_this.divName);

        //
        // Configure my sub component tabs
        //
        menuSystem.configureTab('detail-map-nav-div');

        //
        // Configure preferred visibility
        //
        menuSystem.setPreferredVisibility(_this.divName, true);

        //
        // configure my sub component checkboxes
        //
        var filters = [ "resource", "structure" ];
        for(var i = 0; i < filters.length; i++) {
            var filter = filters[i];
            
            (function(_filter) {
                $("#"+"detail-map-cb-"+filter).change( function() {
                    var cls = $("."+"detail-map-td-"+_filter);
                    if(this.checked) {
                        // alert("Displaying " + _filter);
                        cls.css('display', 'inline');
                    } else {
                        // alert("Hiding " + _filter);
                        cls.css('display', 'none');
                    }
                });
            })(filter);

        }

    });

};

/**
 *
 * Set the mode of the Detail Map view.
 *
 */
DetailMap.prototype.setMode = function(m, params) {

    this.mode = m;
    this.modeParams = params;

    // alert("Setting mode " + m + " " + this.mode);

    this.updateModeArea();
};

/**
 *
 * Get the mode of the Detail Map view.
 *
 */
DetailMap.prototype.getMode = function() {
    return this.mode;
}

/**
 *
 * Update message areas (usually called after setMode())
 *
 */
DetailMap.prototype.updateModeArea = function() {

    var _this = this;

    if(this.mode == this.VIEW_MODE) {
        $('#detailmap-mode-area').html("Mode: " + this.mode);
       
        // alert("Setting default selector image.");
        // $('img[class="detail-map-td-selector"]').attr('src', 'images/tile-selector-thick-64.png' );

        $('.detail-map-td-selector').attr('src', 'images/tile-selector-thick-64.png' );

        return;
    }

    if(this.mode == this.CONSTRUCT_MODE) {
        var structure = this.modeParams.structure;

        var html = "";
        
        html += "<table cellpadding='1' cellspacing='0' border='0'>";

        html += "   <tr>";
        html += "       <td ";
        html += "           style=' vertical-align: top;";
        html += "                   font-size: small;";
        html += "                   font-family: courier; '>";

        html += "           Mode: " + this.mode;

        html += "       </td>";
        html += "       <td>";

        html += "           <img width='32' height='32' " +
                "               src='" + structure.file + "' " +
                "               title='" + structure.name + "'></img>";

        html += "       </td>";
        html += "       <td id='detail-map-end-construction' " + 
                "           style='cursor: pointer'>x";
        html += "       </td>";
        html += "   </tr>";

        html += "</table>";

        $('#detailmap-mode-area').html(html);

        $('#detail-map-end-construction').click( function() {
            _this.setMode(_this.VIEW_MODE);
            // _this.updateModeArea();
        });

        //
        // Set the selection widget to some kind of hammer icon or something?
        //
        // $('img[class="detail-map-td-selector"]').attr('src', 'images/cursor-hammer-icon-64.png' );

        //
        // Set selection indicator image to be the selected structure
        //
        $('.detail-map-td-selector').attr('src', structure.file );


        return;
    }

}

/**
 *
 * Handle the go coord box
 *
 */
DetailMap.prototype.goCoords = function(e) {

    var value = $("#detailmap-go-coords").val();

    if (e.keyCode == 13) {

        var coords = value.split(",");
        if(coords.length == 2) {

            var x = parseInt(coords[0]);
            var y = parseInt(coords[1]);

            if( !isNaN(x) && x > -1 && x < WORLD_WIDTH   &&
                !isNaN(y) && y > -1 && y < WORLD_HEIGHT ) {

                this.loadMap(x - DETAIL_WIDTH/2, y - DETAIL_HEIGHT/2);
                           
                // 
                // Scroll the div to the location we just marked.
                // 
                var worldMap = objectRegistry.get("world-map");
                worldMap.scrollTo(x, y);

            }
        }                
    }

};

/**
 *
 * Return the map tile in the detail view at relative coordinates 
 * (relative to the detail map itself. I.e. 0 - 15)
 *
 */
DetailMap.prototype.getMapTile = function(x, y) {
    return this.mapTiles[x][y];
}

DetailMap.prototype.getMapTileType = function(typeId) {
    return this.mapTileTypes[typeId];
}

/**
 *
 * Show the selection indicator on the detail map at position x, y
 *
 */
DetailMap.prototype.showSelectorAt = function(x, y) {
    if(this.dragging) {
        return;
    }
    $("#detail-cell-selector-img-"+x+"-"+y).show();
}

DetailMap.prototype.hideSelectorAt = function(x, y) {
    if(this.dragging) {
        return;
    }
    $("#detail-cell-selector-img-"+x+"-"+y).hide();
}

DetailMap.prototype.removeAnchoredSelector = function() {
    $("#detail-cell-selector-img-anchored").remove();
}

DetailMap.prototype.anchorSelectorAt = function(x, y) {

    this.removeAnchoredSelector();

    var img =
        '       <img id="detail-cell-selector-img-anchored"         ' +
        '               height="32" width="32"                      ' +
        '           src="images/tile-selector-thick-64.png"         ' +
        '           class="detail-map-td-selector-anchored" />                  ';

    $("#detail-cell-" + x + "-" + y).append(img);

}


/**
 *
 * Show the tile location indicator
 *
 */
 /*
DetailMap.prototype.showSelector = function(div) {

    //
    // If we have a tile selected (and detail view is open) do not offer
    // this selection indicator
    //
    // var dView = objectRegistry.get("detail-view");
    // if(dView.isOpen()) {
    //    return;
    // }

    var img = div.getElementsByTagName('img');
    img[img.length-1].style.display = "block";
}
 */

/**
 *
 * Hise the tile location indicator
 *
 */
 /*
DetailMap.prototype.hideSelector = function(div) {

    //
    // If we have a tile selected (and detail view is open) do not offer
    // this selection indicator
    //
    // var dView = objectRegistry.get("detail-view");
    // if(dView.isOpen()) {
    //    return;
    // }

    var img = div.getElementsByTagName('img');
    img[img.length-1].style.display = "none";
}
 */

/**
 *
 * Hide all selector tiles
 *
 */
DetailMap.prototype.hideAllSelectors = function() {
    //
    // Hide all selection indicator images
    //
    $("img[id^=detail-cell-selector-img-]").hide();
}

/**
 *
 * Add a structure to the specified location
 *
 */
DetailMap.prototype.addStructure = function() {
    
}

/**
 *
 * Load the set of all tiles from the server (getTileTypes)
 *
 */
DetailMap.prototype.loadTileTypes = function() {

    // alert("Loading tile types...");

    var _this = this;

    $.ajax({
                url : "service",
                type: "POST",
                data: { operation: "getTileTypes" },
                success: function(result, textStatus, jqXHR) {

                    var jsonObj = $.parseJSON(result);

                    if(jsonObj.retError) {

                        alert("Error loading tiles " + jsonObj.retMessage);
    
                    } else {
                        
                        var arr = jsonObj.retObject;

                        for(var i = 0; i < arr.length; i++) {
                            _this.mapTileTypes[ arr[i].id ] = arr[i];
                        }
                    
                        // alert("Loaded tile types " + Object.keys(_this.mapTileTypes).length );
                    }
                },
                async: false
            });

}

/**
 *
 * Function for reloading the current map
 *
 */
DetailMap.prototype.reloadMap = function() {

    this.loadMap(this.currentX, this.currentY);

};

/**
 *
 * Function for loading a new map section (getMapSection)
 *
 */
DetailMap.prototype.loadMap = function(x, y) {
               
    if(x < 0) { x = 0; }
    if(y < 0) { y = 0; }

    this.currentX = x;
    this.currentY = y;

    // var origX = x;
    // var origY = y;
   
    var centerX;
    var centerY;

    var _this = this;

    //
    // Clean out detail view
    //
    var dView = objectRegistry.get("detail-view");
    dView.reset();

    //
    // Expect to have the detailed map centered on the
    // pixel clicked, rather than the upper left corner
    // reflecting where the user clicked. 
    //
    // if(x >= DETAIL_WIDTH/2)     { x -= DETAIL_WIDTH/2; }
    // if(y >= DETAIL_HEIGHT/2)    { y -= DETAIL_HEIGHT/2; }

    //
    // Ensure we don't attempt to load past the end of the map
    // otherwise the service displays an error to the user.
    //
    if(x > WORLD_WIDTH - DETAIL_WIDTH) { 
        x = WORLD_WIDTH - DETAIL_WIDTH;
    }
    if(y > WORLD_HEIGHT - DETAIL_HEIGHT) { 
        y = WORLD_HEIGHT - DETAIL_HEIGHT;
    }

    /*
    var loadingMessage = 
                    "<tr><td width='512' height='512' align='center'>" +
                    "Loading . . ." + 
                    "</td></tr>";
    */

    // $('#maptable').html(loadingMessage);

    $('#detail-map-loading-message').show();


    $.post( 'service', 
            {   operation: "getMapSection",
                x:          x,
                y:          y,
                width:      16,
                height:     16  
            }, 
            function(result) {

                var jsonObj = $.parseJSON(result);

                if(jsonObj.retError) {

                    var errorMessage =  "<tr><td>" + 
                                        jsonObj.retMessage +
                                        "</td></tr>";
                    $('#maptable').html(errorMessage);
        
                } else {
       
                    var arr = jsonObj.retObject;
                    var tabledata = "";

                    _this.mapTiles = arr;

                    for(var i = 0; i < 16; i++) {
                        tabledata += "<tr>";
                        for(var j = 0; j < 16; j++) {
                            if(i == DETAIL_WIDTH/2 && j == DETAIL_HEIGHT/2) {
                                centerX = arr[j][i].x;
                                centerY = arr[j][i].y;
                            }
                            tabledata += _this.createTileTD(arr[j][i], j, i);
                        }
                        tabledata += "</tr>";
                    }

                    $("#detailmap-go-coords").val(centerX + "," + centerY);

                    $('#maptable').html(tabledata);
                    $('#detail-map-loading-message').hide();

                    //
                    // Support for dragging. Need to re-attach the selector
                    // image after "rejecting" the drag operation.
                    //
                    var dragParent;
                    var dragParentX, dragParentY;
                    var dragTableCells = [];
                    var dragCellContent = [];

                    //
                    // Create references to cells and cell data divs
                    //
                    var rows = $("#maptable tr").length;
                    var cols = $("#maptable tr:first > td").length;
                    for(var i = 0; i < rows; i++) {
                        dragTableCells[i] = [];
                        dragCellContent[i] = [];
                        for(var j = 0; j < cols; j++) {
                            dragTableCells[i][j] = $('#detail-map-td-' + j + '-' + i);
                            dragCellContent[i][j] = $('#detail-cell-' + j + '-' + i);
                        }
                    }

                    //
                    // Now assign click events to the divs in the table.
                    // I think this is safe to do here, just after we have
                    // updated the html of the table? 
                    //
                    for(var i = 0; i < 16; i++) {
                        for(var j = 0; j < 16; j++) {

                            // Make local copies of the i and j value
                            (function(_x, _y) {    

                                // '#detail-cell-selector-img-anchored';
                                // '#detail-map-td-' + _x + '-' + _y;
                                // '#detail-cell-'+_x+'-'+_y;
                                // '#detail-cell-selector-img-'+_x+'-'+_y;

                                var draggable = '#detail-cell-selector-img-'+_x+'-'+_y;

                                var droppable = '#detail-map-td-' + _x + '-' + _y;


                                $(draggable).draggable({
                                    start: function(event, ui) {
                                        dragParent = $("#detail-cell-"+_x+"-"+_y);
                                        dragParentX = _x;
                                        dragParentY = _y;

                                        _this.hideSelectorAt(_x, _y);
                                        _this.dragging = true;

                                        // console.log("Setting parent");
                                        // console.log(parent);
                                    },
                                    drag: function() {
                                    },
                                    stop: function(event, ui) {
                                        // alert("Stopped dragging " + _x + " " + _y); 
                                        _this.dragging = false;
                                        // _this.showSelectorAt(_x, _y);

                                    },
                                    revert: 'invalid'
                                });

                                $(droppable).droppable({
                                    over: function(event, ui) { 

                                        // _this.dragging = true;

                                        // console.log("Drag over " + _x + " " + _y); 

                                        //
                                        // TODO figure out...
                                        // Why does this not work? 
                                        // Something is flipped incorrectly
                                        // somewhere. This shouldn't be the 
                                        // fix...
                                        //
                                        // var offsetX = _x - dragParentX;
                                        // var offsetY = _y - dragParentY;

                                        var offsetY = _x - dragParentX;
                                        var offsetX = _y - dragParentY;
                                       
                                        //
                                        // Clear table
                                        //
                                        for(var i = 0; i < 16; i++) {
                                            for(var j = 0; j < 16; j++) {
                                                dragTableCells[i][j].children().first().detach();
                                            }
                                        }

                                        //
                                        // Replace cells with new offsets
                                        //
                                        for(var i = 0; i < 16; i++) {
                                            for(var j = 0; j < 16; j++) {
                                                var append = "<div style='position: relative'><img height='32' width='32' src='images/tile-black.png'></img></div>";
                                                if( i - offsetX >= 0     &&
                                                    j - offsetY >= 0     &&
                                                    i - offsetX < 16    &&
                                                    j - offsetY < 16    ) {
                                                    append = dragCellContent[i-offsetX][j-offsetY];
                                                }
                                                dragTableCells[i][j].append(append);
                                            }
                                        }
                                    },
                                    accept: function(el) {
                                        return true;
                                    },
                                    drop: function(event, ui) {
                                        // console.log("Drop on " + _x + " " + _y);
                                        var offsetX = _x - dragParentX;
                                        var offsetY = _y - dragParentY;

                                        // alert("Drop on " + _x + " " + _y + 
                                        //    " offsetX " + offsetX + 
                                        //    " offsetY " + offsetY );

                                        var newX = _this.currentX - offsetX;
                                        var newY = _this.currentY - offsetY;

                                        /*
                                        if(newX > DETAIL_WIDTH/2) {
                                            newX = _this.currentX - offsetX + DETAIL_WIDTH/2;   
                                        }
                                        if(newY > DETAIL_HEIGHT/2) {
                                            newY = _this.currentY - offsetY + DETAIL_HEIGHT/2;
                                        }
                                        */

                                        // if(newX < 0) { newX = DETAIL_WIDTH/2; }
                                        // if(newY < 0) { newY = DETAIL_HEIGHT/2; }

                                        _this.loadMap(newX, newY);

                                        var draggable = ui.draggable;
                                        draggable = draggable.detach();

                                        //
                                        // reset these as they get skewed when
                                        // we drag the image.
                                        //
                                        draggable.css("left", "0px");
                                        draggable.css("top", "0px");
                                        dragParent.append(draggable);

                                        // _this.dragging = false;

                                    }
                                });



                                $("#detail-cell-"+_x+"-"+_y).click(
                                    function() {

                                        //
                                        // Handle click based on mode
                                        //

                                        var dMap = objectRegistry.get("detail-map");
                                        var dView = objectRegistry.get("detail-view");

                                        if(dMap.getMode() == dMap.VIEW_MODE) {
                                            

                                            if( dView.getX() == _x &&
                                                dView.getY() == _y && 
                                                dView.isOpen()          ) {
    
                                                //
                                                // If this is already open, and 
                                                // user clicks on the same tile
                                                // we are already displaying, 
                                                // toggle it (i.e. close)
                                                //
    
                                                dView.close();

                                            } else {
                                                dView.showLocation(_x, _y);
                                                dMap.anchorSelectorAt(_x, _y);
                                            }
                                            return;
                                        }

                                        if(dMap.getMode() == dMap.CONSTRUCT_MODE) {
                                            // alert("Adding structure...");

                                            var typeId = _this.modeParams.structure.id;
                                            var locationId = _this.getMapTile(_x, _y).id;
                                            $("#detail-cell-"+_x+"-"+_y).append("<img id='detail-map-construction-icon' width='32' height='32' src='images/cursor-hammer-icon-64.png' class='detail-map-td-selector-anchored'></img>");

                                            backendCallUtil(

                                                {   operation:  "addStructure",
                                                    typeId:     typeId,
                                                    locationId: locationId
                                                },

                                                function(result) {

                                                    var jsonObj = $.parseJSON(result);
                                                    if(jsonObj.retError) {

                                                        // alert("Error add struct.");

                                                        //
                                                        // Display error
                                                        //
                                                        popupUtil(jsonObj.retMessage);
                                                        $('#detail-map-construction-icon').remove();

                                                    } else {
                                                        //
                                                        // Reload map and 
                                                        // exit construction 
                                                        // mode
                                                        //
                                                        _this.setMode(_this.VIEW_MODE);
                                                        _this.reloadMap();

                                                    }

                                                }
                                                
                                            );         
                                        }
                                    }
                                );

                            })(j, i);

                        }
                    }

                    $('#detailmap-message-area').html(
                                            "Top left: " + x + ", " + y);

                    // $('#detailmap-mode-area').html(
                    //                        "Mode: " + _this.mode);

                    _this.updateModeArea();

                }
            }
        );


    // this.currentX = x;
    // this.currentY = y;


    var worldMap = objectRegistry.get("world-map");
    worldMap.setIndicator(x, y);

}

/**
 *
 * Return the string representation of an html "td" tag
 * representing a tile in the Detail Map. 
 *
 * There should be a less ugly was to create these table cells.
 *
 */
DetailMap.prototype.createTileTD = function(tile, x, y) {
 
    var dView = objectRegistry.get("detail-view");
    var cView = objectRegistry.get("construction-view");

    ret = 
    
        '<td id="detail-map-td-' + x + '-' + y + '">    ' +
        '   <div id="detail-cell-' + x + '-' + y + '"   ' +
        '       title="X: ' + tile.x + ', Y: ' + tile.y + ' (' + this.mapTileTypes[tile.type]["name"] + ')"' +
        '       style="position: relative;"         ' +
        '       onmouseover="objectRegistry.get(\'detail-map\').showSelectorAt('+x+','+y+')"    ' +
        '       onmouseout="objectRegistry.get(\'detail-map\').hideSelectorAt('+x+','+y+')"    ' +
        '       >' +

        '       <img height="32" width="32"                 ' +
        '           src="' + this.mapTileTypes[tile.type].file + '"   ' +
        '           class="detail-map-td-tile" />                       ';

    //
    // Add resources
    //
    var resources = tile.resources;
    for(var i = 0; i < resources.length; i++) {
        var resourceType = dView.getResourceType(resources[i].typeId); 
        ret += 
            '       <img height="32" width="32"                 ' +
            '           src="' + resourceType.file + '"         ' +
            '           class="detail-map-td-resource" />';
    }

    //
    // Add structures
    //
    var structures = tile.structures;
    for(var i = 0; i < structures.length; i++) {
        var structureType = cView.getStructureType(structures[i].typeId); 
        ret += 
            '       <img height="32" width="32"                 ' +
            '           src="' + structureType.file + '"        ' +
            '           class="detail-map-td-structure" />';
    }

    //
    // Ensure selector indicator icon is on top of stack.
    //
    ret += this.getSelectorHTML(x,y);

    ret += 
        '   </div>' + 
        '</td>';

    return ret;
}

DetailMap.prototype.getSelectorHTML = function(x, y) {

    return  '       <img id="detail-cell-selector-img-' + x + '-' + y + '"' +
            '               height="32" width="32"                     ' +
            '           src="images/tile-selector-thick-64.png"     ' +
            '           class="detail-map-td-selector" style="display: none" ' +
            '>';
}

