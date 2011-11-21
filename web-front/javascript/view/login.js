(function() {
  var LoginView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  LoginView = (function() {
    __extends(LoginView, Backbone.View);
    function LoginView() {
      this.doRegister = __bind(this.doRegister, this);
      this.doLogin = __bind(this.doLogin, this);
      this.reset = __bind(this.reset, this);
      this.render = __bind(this.render, this);
      LoginView.__super__.constructor.apply(this, arguments);
    }
    LoginView.prototype.template = JST['login'];
    LoginView.prototype.initialize = function() {
      $('#loginmodal').modal({
        keyboard: true
      });
      $('#registermodal').modal({
        keyboard: true
      });
      $('#doLogin').live("submit", this.doLogin);
      $('#doLogin').live("submit", this.doLogin);
      $('#doRegister').live("submit", this.doRegister);
      $('#loginmodal').live('hide', this.reset);
      return $('#registermodal').live('hidden', this.reset);
    };
    LoginView.prototype.render = function() {
      $(this.el).html(this.template());
      $(this.el).find('button.login').click(this.doLogin);
      $(this.el).find('button.register').click(this.doLogin);
      $(this.el).find('button.cancel').click(__bind(function() {
        $('#loginmodal').modal('hide');
        return $('#registermodal').modal('hide');
      }, this));
      return this;
    };
    LoginView.prototype.reset = function() {
      $('#loginmodal').find(':input', '#myform').not(':button, :submit, :reset, :hidden').val('').removeAttr('checked').removeAttr('selected');
      return $('#registermodal').find(':input', '#myform').not(':button, :submit, :reset, :hidden').val('').removeAttr('checked').removeAttr('selected');
    };
    LoginView.prototype.doLogin = function() {
      var dataSent;
      console.log("doLogin");
      dataSent = $('#doLogin').serialize();
      return $.post('/user/login', dataSent, function(data, textStatus) {
        if (textStatus === 'success') {
          $.Storage.set("token", data);
          $('#loginmodal').modal('hide');
          router.navigate("onLogin", true);
        }
        return console.log(data);
      }, "json");
    };
    LoginView.prototype.doRegister = function() {
      var dataSent;
      console.log("doRegister");
      dataSent = $('#doRegister').serialize();
      return $.post('/user/register', dataSent, function(data, textStatus) {
        if (textStatus === 'success') {
          $.Storage.set("token", data);
          $('#registermodal').modal('hide');
          router.navigate("onLogin", true);
        }
        return console.log(data);
      }, "json");
    };
    return LoginView;
  })();
  this.LoginView = LoginView;
}).call(this);
