(function() {
  var ApplicationInstallModalView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  ApplicationInstallModalView = (function() {
    __extends(ApplicationInstallModalView, Backbone.View);
    function ApplicationInstallModalView() {
      this.doInstall = __bind(this.doInstall, this);
      this.render = __bind(this.render, this);
      ApplicationInstallModalView.__super__.constructor.apply(this, arguments);
    }
    ApplicationInstallModalView.prototype.template = JST['application/install_modal'];
    ApplicationInstallModalView.prototype.render = function() {
      var c;
      $(this.el).html(this.template(this.model.toJSON()));
      $('#modals').html($(this.el));
      this.modal = $('#modals .modal');
      this.modal.bind('hide', __bind(function() {
        return $('#modals').empty();
      }, this));
      this.modal.find('button.select').click(this.doInstall);
      this.modal.find('button.cancel').click(__bind(function() {
        return this.modal.modal('hide');
      }, this));
      this.modal.modal({
        keyboard: true,
        show: true,
        backdrop: true
      });
      this.select = this.modal.find('select');
      c = router.nabaztagCollection;
      c.fetch({
        success: __bind(function() {
          return c.each(__bind(function(nab) {
            return this.select.append("<option value=\"" + (nab.get('macAddress')) + "\">" + (nab.get('name')) + "</option>");
          }, this));
        }, this)
      });
      return this;
    };
    ApplicationInstallModalView.prototype.doInstall = function() {
      if (typeof console !== "undefined" && console !== null) {
        console.log("doInstall");
      }
      this.modal.modal('hide');
      return router.navigate("nabaztag/" + (this.select.val()) + "/appinstall/" + (this.model.get('apikey')), true);
    };
    return ApplicationInstallModalView;
  })();
  this.ApplicationInstallModalView = ApplicationInstallModalView;
}).call(this);
