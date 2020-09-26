
/**
 *
 * Manage cookies on the client browser
 * 
 *
 */
function CookieManager() {
    // this.cookies = {};
};

/**
 *
 * Set cookie name/value pair.
 *
 */
CookieManager.prototype.set = function(name, value) {

    // alert("Setting cookie " + name + " " + value);

    // this.cookies[name] = value; 

    document.cookie = name + "=" + value + "; path=/";
};

/**
 *
 * Get cookie value given the cookie name.
 *
 */
CookieManager.prototype.get = function(name) {

    // alert("Retrieving cookie " + name);

    // 
    // Taken from http://www.w3schools.com/js/js_cookies.asp
    //
    var name = name + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            // alert("Returning " + c.substring(name.length,c.length));
            return c.substring(name.length,c.length);
        }
    }

    // alert("Returning empty " + name);

    return "";
};

/**
 *
 * Remove cookie with the given name.
 *
 */
CookieManager.prototype.remove = function(name) {

    if(name != "") {
        document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/";
    }

};

/**
 *
 * Remove all cookies.
 *
 */
CookieManager.prototype.deleteAll = function() {

    var cookies = document.cookie.split(";");
    for (var i = 0; i < cookies.length; i++) {
        var name = cookies[i].split("=")[0];

        // alert("Deleting cookie: " + name);
        // console.log("Deleting cookie: " + name);

        this.remove(name);
    }

}

