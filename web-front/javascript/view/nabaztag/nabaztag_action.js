(function() {
  var NabaztagActionView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  NabaztagActionView = (function() {
    __extends(NabaztagActionView, Backbone.View);
    function NabaztagActionView() {
      this.exec = __bind(this.exec, this);
      this.tts = __bind(this.tts, this);
      this.play = __bind(this.play, this);
      this.render = __bind(this.render, this);
      NabaztagActionView.__super__.constructor.apply(this, arguments);
    }
    NabaztagActionView.prototype.events = {
      'submit form.play': "play",
      'submit form.tts': "tts",
      'submit form.exec': "exec"
    };
    NabaztagActionView.prototype.template = JST['nabaztag/nabaztag_action'];
    NabaztagActionView.prototype.render = function() {
      $(this.el).html(this.template(this.model.toJSON()));
      return this;
    };
    NabaztagActionView.prototype.play = function() {
      var url;
      url = $('input.play').val();
      return this.model.stream(url);
    };
    NabaztagActionView.prototype.tts = function() {
      var text;
      text = $('textarea.tts').val();
      if (typeof console !== "undefined" && console !== null) {
        console.log(text);
      }
      return this.model.tts(text);
    };
    NabaztagActionView.prototype.exec = function() {
      var command;
      command = $('.command').val();
      return this.model.exec(command);
    };
    return NabaztagActionView;
  })();
  this.NabaztagActionView = NabaztagActionView;
}).call(this);
