(function() {
  var NabaztagConfigItemView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  NabaztagConfigItemView = (function() {
    __extends(NabaztagConfigItemView, Backbone.View);
    function NabaztagConfigItemView() {
      this.moreClick = __bind(this.moreClick, this);
      this.deleteClick = __bind(this.deleteClick, this);
      this.render = __bind(this.render, this);
      NabaztagConfigItemView.__super__.constructor.apply(this, arguments);
    }
    NabaztagConfigItemView.prototype.events = {
      'click .delete': 'deleteClick',
      'click .more': 'moreClick'
    };
    NabaztagConfigItemView.prototype.template = JST['nabaztag/config_item'];
    NabaztagConfigItemView.prototype.initialize = function(options) {
      return this.config = options.config;
    };
    NabaztagConfigItemView.prototype.render = function() {
      $(this.el).html(this.template(this.config));
      return this;
    };
    NabaztagConfigItemView.prototype.deleteClick = function() {
      if (typeof console !== "undefined" && console !== null) {
        console.log("deleteClick");
      }
      $.ajax({
        type: "DELETE",
        url: "/config/" + this.config.uuid
      });
      return router.nabaztagCollection.fetch();
    };
    NabaztagConfigItemView.prototype.moreClick = function() {
      return router.navigate("nabaztag/" + (this.model.get('macAddress')) + "/appinstall/" + this.config.applicationStoreApikey + "/" + this.config.uuid, true);
    };
    return NabaztagConfigItemView;
  })();
  this.NabaztagConfigItemView = NabaztagConfigItemView;
}).call(this);
