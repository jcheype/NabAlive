(function() {
  var NabaztagCollectionView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  NabaztagCollectionView = (function() {
    __extends(NabaztagCollectionView, Backbone.View);
    function NabaztagCollectionView() {
      this.addNabaztag = __bind(this.addNabaztag, this);
      this.render = __bind(this.render, this);
      NabaztagCollectionView.__super__.constructor.apply(this, arguments);
    }
    NabaztagCollectionView.prototype.events = {
      'click .addNabaztag': "addNabaztag"
    };
    NabaztagCollectionView.prototype.template = JST['nabaztag_collection'];
    NabaztagCollectionView.prototype.initialize = function() {
      console.log("init NabaztagListView");
      this.model.bind('all', this.render);
      return this.model.fetch();
    };
    NabaztagCollectionView.prototype.render = function() {
      var col;
      $(this.el).html(this.template());
      col = $(this.el).find(".nabaztagCollection");
      this.model.each(function(nab) {
        return col.append(new NabaztagItemView({
          model: nab
        }).render().el);
      });
      return this;
    };
    NabaztagCollectionView.prototype.addNabaztag = function() {
      console.log("addNabaztag");
      return router.navigate("nabaztag/add", true);
    };
    return NabaztagCollectionView;
  })();
  this.NabaztagCollectionView = NabaztagCollectionView;
}).call(this);
