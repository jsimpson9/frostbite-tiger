
/**
 *
 * A class for the menu system configuration
 *
 */
function MenuSystem() {
    this.isInited = false;
    this.queuedComps = [];  // queue components that might try to register 
                            // with us before we are fully initialized.
};
         
MenuSystem.prototype.render = function(id) {
    
    // alert("Rendering menu " + id);

    var _this = this;

    $.get(  "js/frostycomps/templates/menu-system.html", function(html) {
        $('#'+id).html(html);

        _this.isInited = true;

        for(var i = 0; i < _this.queuedComps.length; i++) {
            // Configure any components that were queued before we
            // were fully initialized
            _this.configureTab(this.queuedComps[i]);
        }
    });

};

MenuSystem.prototype.configureTab = function(id, closeFunc) {

    // alert("MenuSystem configuring tab: " + id);

    if(!this.isInited) {
        this.queuedComps.push(id);
        return;
    }

    var toggle  = "#" + id + "-toggle";
    var close   = "#" + id + "-toggle-x";
    var div     = id; 
                   
    (function(_div) {    // get a local copy of the td value

        $(toggle).click(function(){
    
            // alert("Toggling " + _div);
            // console.log("MenuSystem toggling " + _div);
  
            var cookieManager = objectRegistry.get("cookie-manager");

            if( $("#"+_div).is(':visible') ) {

                cookieManager.set(_div + "-visibility", false); 
                $("#"+_div).hide();

            } else {

                cookieManager.set(_div + "-visibility", true); 
                $("#"+_div).show();
            }
        });

    })(div);

    (function(_div, _closeFunc) {    // get a local copy of the parameters

        $(close).click(function(){
            // alert("Hiding " + _div);

            var cookieManager = objectRegistry.get("cookie-manager");
            cookieManager.set(_div + "-visibility", false); 

            $("#"+_div).hide();

            if(!(_closeFunc === undefined)) {
                _closeFunc();
            }
        }); 

    })(div, closeFunc);

};

/**
 *
 * Set the "preferred" visibility (last configured by user in session cookie)
 * @param id        The div ID of the component.
 * @param def       If no session value is available (fresh page load, 
 *                  cookies cleared?) then use this default value.
 *
 *
 */
MenuSystem.prototype.setPreferredVisibility = function(id, def) {

    var cookieManager = objectRegistry.get("cookie-manager");
    var visible = cookieManager.get(id + "-visibility");

    if(visible == "") {
        //
        // No value set yet. Assign a default value for first load
        // for this component
        //
        // alert("Setting default visibility " + id + " " + def);
        visible = def;
    } else {
        visible = (visible === "true");
    }

    // alert(visible);

    if(visible) {
        // alert("Showing " + id);
        $('#'+id).show();
    } else {
        // alert("Hiding " + id);
        $('#'+id).hide();
    }

};

