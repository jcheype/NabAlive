(function() {
  var NabaztagAddView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  NabaztagAddView = (function() {
    __extends(NabaztagAddView, Backbone.View);
    function NabaztagAddView() {
      this.cancel = __bind(this.cancel, this);
      this.addNabaztag = __bind(this.addNabaztag, this);
      this.render = __bind(this.render, this);
      NabaztagAddView.__super__.constructor.apply(this, arguments);
    }
    NabaztagAddView.prototype.events = {
      'submit #addNabaztag': "addNabaztag",
      'click .cancel': "cancel"
    };
    NabaztagAddView.prototype.template = JST['nabaztag/nabaztag_add'];
    NabaztagAddView.prototype.render = function() {
      $(this.el).html(this.template());
      return this;
    };
    NabaztagAddView.prototype.addNabaztag = function() {
      var data, nab, params;
      if (typeof console !== "undefined" && console !== null) {
        console.log($('#addNabaztag'));
      }
      data = $('#addNabaztag').serializeArray();
      if (typeof console !== "undefined" && console !== null) {
        console.log("save: ", data);
      }
      nab = new Nabaztag();
      params = {};
      _.each(data, __bind(function(item) {
        if (typeof console !== "undefined" && console !== null) {
          console.log("item ", item.name, item.value);
        }
        return params[item.name] = item.value;
      }, this));
      return nab.save(params, {
        success: function(newNab) {
          router.nabaztagCollection.add(newNab);
          return router.navigate("nabaztag/list", true);
        }
      });
    };
    NabaztagAddView.prototype.cancel = function() {
      $("#addNabaztag").reset();
      return router.navigate("nabaztag/list", true);
    };
    return NabaztagAddView;
  })();
  this.NabaztagAddView = NabaztagAddView;
}).call(this);
