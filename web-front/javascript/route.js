(function() {
  var AppRouter, login, token;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  AppRouter = (function() {
    __extends(AppRouter, Backbone.Router);
    function AppRouter() {
      this.nabaztagAdd = __bind(this.nabaztagAdd, this);
      this.nabaztagAction = __bind(this.nabaztagAction, this);
      this.nabaztagList = __bind(this.nabaztagList, this);
      this.logout = __bind(this.logout, this);
      this.onLogin = __bind(this.onLogin, this);
      this.defaultRoute = __bind(this.defaultRoute, this);
      this.setIsLogin = __bind(this.setIsLogin, this);
      this.initialize = __bind(this.initialize, this);
      AppRouter.__super__.constructor.apply(this, arguments);
    }
    AppRouter.prototype.initialize = function() {
      this.isLogin = false;
      this.nabaztagCollection = new NabaztagCollection();
      $('.showLogin').hide();
      return $('.showLogout').hide();
    };
    AppRouter.prototype.setIsLogin = function(isLogin) {
      this.isLogin = isLogin;
      if (isLogin) {
        $('.showLogin').hide();
        return $('.showLogout').show();
      } else {
        $('.showLogout').hide();
        return $('.showLogin').show();
      }
    };
    AppRouter.prototype.routes = {
      "onLogin": "onLogin",
      "logout": "logout",
      "nabaztag/action/:id": "nabaztagAction",
      "nabaztag/list": "nabaztagList",
      "nabaztag/add": "nabaztagAdd",
      "*actions": "defaultRoute"
    };
    AppRouter.prototype.defaultRoute = function(actions) {
      console.log(actions);
      $('#content').html("");
      if (this.isLogin) {
        return this.navigate("nabaztag/list", true);
      }
    };
    AppRouter.prototype.onLogin = function() {
      console.log("onLogin");
      this.setIsLogin(true);
      return this.navigate("nabaztag/list", true);
    };
    AppRouter.prototype.logout = function() {
      console.log("logout");
      $.Storage.remove("token");
      this.setIsLogin(false);
      return this.navigate("home", true);
    };
    AppRouter.prototype.nabaztagList = function() {
      var nabaztagCollectionView;
      console.log("nabaztagList: ", this.nabaztagCollection);
      nabaztagCollectionView = new NabaztagCollectionView({
        model: this.nabaztagCollection
      });
      return $('#content').html($(nabaztagCollectionView.el));
    };
    AppRouter.prototype.nabaztagAction = function(id) {
      return this.nabaztagCollection.getAndRun(id, __bind(function(nab) {
        var nabaztagView;
        console.log("nab", nab);
        nabaztagView = new NabaztagView({
          model: nab
        });
        return $('#content').html($(nabaztagView.render().el));
      }, this));
    };
    AppRouter.prototype.nabaztagAdd = function() {
      var nabaztagAddView;
      console.log("nabaztagAdd");
      nabaztagAddView = new NabaztagAddView();
      return $('#content').html($(nabaztagAddView.render().el));
    };
    return AppRouter;
  })();
  login = new LoginView();
  $('#login').html($(login.render().el));
  this.router = new AppRouter();
  token = $.Storage.get("token");
  if (token) {
    $.getJSON('user/info', {
      "token": token
    }).success(__bind(function() {
      this.router.setIsLogin(true);
      return this.router.nabaztagCollection.fetch();
    }, this)).error(__bind(function() {
      this.router.setIsLogin(false);
      return this.router.navigate("login", true);
    }, this)).complete(__bind(function() {
      return Backbone.history.start();
    }, this));
  } else {
    this.router.setIsLogin(false);
    this.router.navigate("home", true);
    Backbone.history.start();
  }
}).call(this);
