(function() {
  var Application, ApplicationCollection;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  Application = (function() {
    __extends(Application, Backbone.Model);
    function Application() {
      this.initialize = __bind(this.initialize, this);
      Application.__super__.constructor.apply(this, arguments);
    }
    Application.prototype.urlRoot = "/applications";
    Application.prototype.initialize = function(attributes) {
      return this.set({
        id: attributes.apikey
      });
    };
    return Application;
  })();
  this.Application = Application;
  ApplicationCollection = (function() {
    __extends(ApplicationCollection, Backbone.Collection);
    function ApplicationCollection() {
      this.getAndRun = __bind(this.getAndRun, this);
      ApplicationCollection.__super__.constructor.apply(this, arguments);
    }
    ApplicationCollection.prototype.model = Application;
    ApplicationCollection.prototype.url = "/applications";
    ApplicationCollection.prototype.getAndRun = function(id, success, error) {
      var app;
      app = this.get(id);
      if (!app) {
        return this.fetch({
          success: __bind(function() {
            if (typeof console !== "undefined" && console !== null) {
              console.log("collection: ", this);
            }
            app = this.get(id);
            return success(app);
          }, this),
          error: error
        });
      } else {
        return success(app);
      }
    };
    return ApplicationCollection;
  })();
  this.ApplicationCollection = ApplicationCollection;
}).call(this);
