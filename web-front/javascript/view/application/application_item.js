(function() {
  var ApplicationItemView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  ApplicationItemView = (function() {
    __extends(ApplicationItemView, Backbone.View);
    function ApplicationItemView() {
      this.installClick = __bind(this.installClick, this);
      this.render = __bind(this.render, this);
      ApplicationItemView.__super__.constructor.apply(this, arguments);
    }
    ApplicationItemView.prototype.events = {
      'click .install': 'installClick'
    };
    ApplicationItemView.prototype.template = JST['application/application_item'];
    ApplicationItemView.prototype.initialize = function() {
      return this.model.bind('change', this.render);
    };
    ApplicationItemView.prototype.render = function() {
      $(this.el).html(this.template(this.model.toJSON()));
      return this;
    };
    ApplicationItemView.prototype.installClick = function() {
      var applicationInstallModalView;
      if (typeof console !== "undefined" && console !== null) {
        console.log("installClick");
      }
      applicationInstallModalView = new ApplicationInstallModalView({
        model: this.model
      });
      return applicationInstallModalView.render();
    };
    return ApplicationItemView;
  })();
  this.ApplicationItemView = ApplicationItemView;
}).call(this);
