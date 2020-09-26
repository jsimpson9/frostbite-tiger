
/**
 *
 * A class for the world map component
 *
 */
function WorldMap() { 
    this.x = 0;
    this.y = 0;
    this.divName = "";
};
         
WorldMap.prototype.render = function(id) {
    
    // alert("Rendering menu " + id);

    this.divName = id;
    var _this = this;

    $.get(  "js/frostycomps/templates/world-map.html", function(html) {

        //
        // Set html from template
        //
        $('#'+id).html(html);

        //
        // Configure tab with menu system
        //
        var menuSystem = objectRegistry.get("menu-system");
        menuSystem.configureTab(_this.divName);
        menuSystem.setPreferredVisibility(_this.divName, true);

        //
        // Configure event handlers
        //
        $('#worlddiv').mouseout( function(e) {
            $('#worlddivmessage').html("&nbsp;");
        });

        $('#worlddiv').click( function(e) {

            // alert("world-map click()");
            
            if(dragscroll_scrolled) {
                dragscroll_scrolled = false;
                return;
            }

            var coords = _this.getWorldMapCoords(e, false);
            var x = coords[0];
            var y = coords[1];

            var detailMap = objectRegistry.get("detail-map");
            detailMap.loadMap(x - DETAIL_WIDTH/2, y - DETAIL_HEIGHT/2);

        });

        $('#worlddiv').mousemove( function(e) {   
                 
            var coords = _this.getWorldMapCoords(e, false);
 
            $('#worlddivmessage').text((coords[0]) + ", " + (coords[1]) );
        });

        $('#worlddiv').mouseout( function(e) {   
                 
            //
            // Set this to the coords of the current location
            //

            var width   = $("#worlddiv").width();
            var height  = $("#worlddiv").height();
            var x = $("#worlddiv").scrollLeft() + (width/2);
            var y = $("#worlddiv").scrollTop() + (height/2);

            $('#worlddivmessage').text(x.toFixed() + ", " + y.toFixed());
        });

    });

};

/**
 *
 * Scroll the view the be centered at the specified location.
 *
 */
WorldMap.prototype.scrollTo = function(x, y) {
    var width   = $("#worlddiv").width();
    var height  = $("#worlddiv").height();
    $("#worlddiv").scrollLeft( x - (width/2) );
    $("#worlddiv").scrollTop( y - (height/2) );
};

/**
 *
 * Set the indicator icon identifying which portion of the map is
 * currently being viewed in the Detail Map
 *
 */
WorldMap.prototype.setIndicator = function(x, y) {

    this.x = x;
    this.y = y;

    $("#worlddiv-selection-indicator").remove();
    $("#worlddiv").append(
        "<img   id='worlddiv-selection-indicator'   " +
        "       src='images/tile-selector-16.png'   " +
        "       style='position: absolute; left: " + x + "; top: " + y + ";'>");

};

/**
 *
 * Determine the location of the mouse pointer on the map
 * image taking into account div scroll bars etc.
 *
 * @param e     The event passed to the calling click (or other)
 *              function.
 *
 */
WorldMap.prototype.getWorldMapCoords = function(e, debug) {

    var offset_t = $('#worlddiv').offset().top - $(window).scrollTop();
    var offset_l = $('#worlddiv').offset().left - $(window).scrollLeft();
            
    var left    = Math.round( (e.clientX - offset_l) );
    var top     = Math.round( (e.clientY - offset_t) );
          
    if(debug) {
        alert(  "e.clientY " + e.clientY + " " + 
                "offset_t: " + offset_t + " " +
                "top: " + top + " " +
                "scrollTop: " + $('#worlddiv').scrollTop() + " " +
                "total: " + (top + $('#worlddiv').scrollTop()) );
    }

    left    += $('#worlddiv').scrollLeft();
    top     += $('#worlddiv').scrollTop();

    // alert("Left: " + left + " Top: " + top);

    return [ left, top ];

};

