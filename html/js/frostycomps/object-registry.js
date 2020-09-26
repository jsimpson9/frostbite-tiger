
/**
 *
 * An object where components can register themselves in order to be
 * retrirved by other components to make calls into them.
 *
 */
function ObjectRegistry() {
    this.registry = {};
};
         
ObjectRegistry.prototype.register = function(name, obj) {
    // alert("Registering " + name);
    this.registry[name] = obj
};

ObjectRegistry.prototype.get = function(name) {
    // alert("Retrieving " + name);
    return this.registry[name];
};
