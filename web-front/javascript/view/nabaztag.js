(function() {
  var NabaztagView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  NabaztagView = (function() {
    __extends(NabaztagView, Backbone.View);
    function NabaztagView() {
      this.play = __bind(this.play, this);
      this.render = __bind(this.render, this);
      NabaztagView.__super__.constructor.apply(this, arguments);
    }
    NabaztagView.prototype.events = {
      'submit .play': "play"
    };
    NabaztagView.prototype.template = JST['nabaztag'];
    NabaztagView.prototype.render = function() {
      $(this.el).html(this.template(this.model.toJSON()));
      return this;
    };
    NabaztagView.prototype.play = function() {
      var url;
      url = $('.playUrl').val();
      return this.model.stream(url);
    };
    return NabaztagView;
  })();
  this.NabaztagView = NabaztagView;
}).call(this);
