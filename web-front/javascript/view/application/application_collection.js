(function() {
  var ApplicationCollectionView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  ApplicationCollectionView = (function() {
    __extends(ApplicationCollectionView, Backbone.View);
    function ApplicationCollectionView() {
      this.render = __bind(this.render, this);
      ApplicationCollectionView.__super__.constructor.apply(this, arguments);
    }
    ApplicationCollectionView.prototype.template = JST['application/application_collection'];
    ApplicationCollectionView.prototype.initialize = function() {
      if (typeof console !== "undefined" && console !== null) {
        console.log("init ApplicationCollectionView");
      }
      this.model.bind('all', this.render);
      return this.model.fetch();
    };
    ApplicationCollectionView.prototype.render = function() {
      var col;
      $(this.el).html(this.template());
      col = $(this.el).find(".applicationCollection");
      this.model.each(function(app) {
        return col.append(new ApplicationItemView({
          model: app
        }).render().el);
      });
      return this;
    };
    return ApplicationCollectionView;
  })();
  this.ApplicationCollectionView = ApplicationCollectionView;
}).call(this);
