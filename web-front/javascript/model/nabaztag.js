(function() {
  var Nabaztag, NabaztagCollection;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  Nabaztag = (function() {
    __extends(Nabaztag, Backbone.Model);
    function Nabaztag() {
      this.stream = __bind(this.stream, this);
      this.initialize = __bind(this.initialize, this);
      Nabaztag.__super__.constructor.apply(this, arguments);
    }
    Nabaztag.prototype.urlRoot = "/nabaztags";
    Nabaztag.prototype.initialize = function(attributes) {
      return this.set({
        id: attributes.idString
      });
    };
    Nabaztag.prototype.stream = function(url, success) {
      var apikey;
      apikey = this.get("apikey");
      return jQuery.getJSON("/nabaztags/" + apikey + "/play", {
        "url": url
      }, success);
    };
    return Nabaztag;
  })();
  this.Nabaztag = Nabaztag;
  NabaztagCollection = (function() {
    __extends(NabaztagCollection, Backbone.Collection);
    function NabaztagCollection() {
      this.getAndRun = __bind(this.getAndRun, this);
      NabaztagCollection.__super__.constructor.apply(this, arguments);
    }
    NabaztagCollection.prototype.model = Nabaztag;
    NabaztagCollection.prototype.url = "/nabaztags";
    NabaztagCollection.prototype.getAndRun = function(id, success, error) {
      var nab;
      nab = this.get(id);
      if (!nab) {
        return this.fetch({
          success: __bind(function() {
            console.log("collection: ", this);
            nab = this.get(id);
            return success(nab);
          }, this),
          error: error
        });
      } else {
        return success(nab);
      }
    };
    return NabaztagCollection;
  })();
  this.NabaztagCollection = NabaztagCollection;
}).call(this);
