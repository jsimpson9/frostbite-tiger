
/**
 *
 * A template class for a UI component
 *
 */
function TemplateView() { 
    this.divName = "";
};
         
TemplateView.prototype.render = function(id) {
    
    // alert("Rendering menu " + id);

    this.divName = id;

    var _this = this;

    $.get(  "js/frostycomps/templates/template-view.html", function(html) {

        //
        // Set html from template
        //
        $('#'+id).html(html);

        //
        // Configure tab with menu system
        //
        var menuSystem = objectRegistry.get("menu-system");
        menuSystem.configureTab(_this.divName);

       
    });

};

