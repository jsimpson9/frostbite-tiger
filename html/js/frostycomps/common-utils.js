/**
 *
 * Common util functions
 *
 */
         
popupUtil = function(message) {
    //
    // Would like to use something nicer than alert() so we can
    // style it to match everything else...
    //
    alert(message);    
};

backendCallUtil = function(data, callback) {

    // alert("Making backend call...");

    var _this = this;

    $.post( 'service', 
            data,
            callback );

 
}

loadUtil = function(op, map) {

    var _this = this;

    $.ajax({
                url : "service",
                type: "POST",
                data: { operation: op },
                success: function(result, textStatus, jqXHR) {

                    var jsonObj = $.parseJSON(result);

                    if(jsonObj.retError) {

                        alert("Error loading resource types " + jsonObj.retMessage);

                    } else {

                        var arr = jsonObj.retObject;

                        for(var i = 0; i < arr.length; i++) {
                            map[ arr[i].id ] = arr[i];
                        }

                        // alert("Loaded " + op + " " + Object.keys(_this.structureTypes).length );

                    }
                },
                async: false
            });
 
};

