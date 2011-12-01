(function() {
  var AppRouter, global;
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
      this.applicationList = __bind(this.applicationList, this);
      this.nabaztagInstallApp = __bind(this.nabaztagInstallApp, this);
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
      this.applicationCollection = new ApplicationCollection();
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
      "nabaztag/:mac/appinstall/:apikey": "nabaztagInstallApp",
      "applications": "applicationList",
      "*actions": "defaultRoute"
    };
    AppRouter.prototype.defaultRoute = function(actions) {
      if (typeof console !== "undefined" && console !== null) {
        console.log(actions);
      }
      $('#content').html("");
      if (this.isLogin) {
        return this.navigate("nabaztag/list", true);
      }
    };
    AppRouter.prototype.onLogin = function() {
      if (typeof console !== "undefined" && console !== null) {
        console.log("onLogin");
      }
      this.setIsLogin(true);
      return this.navigate("nabaztag/list", true);
    };
    AppRouter.prototype.logout = function() {
      if (typeof console !== "undefined" && console !== null) {
        console.log("logout");
      }
      $.Storage.remove("token");
      this.setIsLogin(false);
      return this.navigate("home", true);
    };
    AppRouter.prototype.nabaztagList = function() {
      var nabaztagCollectionView;
      if (typeof console !== "undefined" && console !== null) {
        console.log("nabaztagList: ", this.nabaztagCollection);
      }
      nabaztagCollectionView = new NabaztagCollectionView({
        model: this.nabaztagCollection
      });
      return $('#content').html($(nabaztagCollectionView.el));
    };
    AppRouter.prototype.nabaztagAction = function(id) {
      return this.nabaztagCollection.getAndRun(id, __bind(function(nab) {
        var nabaztagActionView;
        if (typeof console !== "undefined" && console !== null) {
          console.log("nab", nab);
        }
        nabaztagActionView = new NabaztagActionView({
          model: nab
        });
        return $('#content').html($(nabaztagActionView.render().el));
      }, this));
    };
    AppRouter.prototype.nabaztagAdd = function() {
      var nabaztagAddView;
      if (typeof console !== "undefined" && console !== null) {
        console.log("nabaztagAdd");
      }
      nabaztagAddView = new NabaztagAddView();
      return $('#content').html($(nabaztagAddView.render().el));
    };
    AppRouter.prototype.nabaztagInstallApp = function(mac, apikey) {
      if (typeof console !== "undefined" && console !== null) {
        console.log("nabaztagInstallApp");
      }
      return this.nabaztagCollection.getAndRun(mac, __bind(function(nab) {
        return this.applicationCollection.getAndRun(apikey, __bind(function(app) {
          var applicationConfigView;
          if (typeof console !== "undefined" && console !== null) {
            console.log("nab", nab);
          }
          if (typeof console !== "undefined" && console !== null) {
            console.log("app", app);
          }
          applicationConfigView = new ApplicationConfigView({
            model: nab,
            application: app
          });
          return $('#content').html($(applicationConfigView.render().el));
        }, this));
      }, this));
    };
    AppRouter.prototype.applicationList = function() {
      var applicationCollectionView;
      if (typeof console !== "undefined" && console !== null) {
        console.log("applicationList");
      }
      applicationCollectionView = new ApplicationCollectionView({
        model: this.applicationCollection
      });
      return $('#content').html($(applicationCollectionView.render().el));
    };
    return AppRouter;
  })();
  global = this;
  $(document).ready(__bind(function() {
    var login, token;
    login = new LoginView();
    $('#login').html($(login.render().el));
    global.router = new AppRouter();
    token = $.Storage.get("token");
    if (token) {
      return $.getJSON('user/info', {
        "token": token
      }).success(__bind(function() {
        global.router.setIsLogin(true);
        return global.router.nabaztagCollection.fetch();
      }, this)).error(__bind(function() {
        global.router.setIsLogin(false);
        return global.router.navigate("login", true);
      }, this)).complete(__bind(function() {
        return Backbone.history.start();
      }, this));
    } else {
      global.router.setIsLogin(false);
      global.router.navigate("home", true);
      return Backbone.history.start();
    }
  }, this));
}).call(this);
