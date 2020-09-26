
/**
 *
 * Object view component
 *
 */
function ObjectView() { 

    console.log("ObjectView constructor.");

    this.divName = "";
};
         
ObjectView.prototype.render = function(id) {
    
    console.log("ObjectView render() " + id);

    this.divName = id;

    var _this = this;

    $.get(  "js/frostycomps/templates/object-view.html", function(html) {

        //
        // Set html from template
        //
        $('#'+id).html(html);

        //
        // Configure tab with menu system
        //
        var menuSystem = objectRegistry.get("menu-system");
    
        console.log("Calling menuSystem.configureTab()");
        menuSystem.configureTab(_this.divName);

       
    });

};

