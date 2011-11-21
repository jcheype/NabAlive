(function() {
  var Nabaztag, NabaztagCollection;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  Nabaztag = (function() {
    __extends(Nabaztag, Backbone.Model);
    function Nabaztag() {
      this.stream = __bind(this.stream, this);
      this.initialize = __bind(this.initialize, this);
      Nabaztag.__super__.constructor.apply(this, arguments);
    }
    Nabaztag.prototype.urlRoot = "/nabaztags";
    Nabaztag.prototype.initialize = function(attributes) {
      return this.set({
        id: attributes.idString
      });
    };
    Nabaztag.prototype.stream = function(url, success) {
      var apikey;
      apikey = this.get("apikey");
      return jQuery.getJSON("/nabaztags/" + apikey + "/play", {
        "url": url
      }, success);
    };
    return Nabaztag;
  })();
  this.Nabaztag = Nabaztag;
  NabaztagCollection = (function() {
    __extends(NabaztagCollection, Backbone.Collection);
    function NabaztagCollection() {
      this.getAndRun = __bind(this.getAndRun, this);
      NabaztagCollection.__super__.constructor.apply(this, arguments);
    }
    NabaztagCollection.prototype.model = Nabaztag;
    NabaztagCollection.prototype.url = "/nabaztags";
    NabaztagCollection.prototype.getAndRun = function(id, success, error) {
      var nab;
      nab = this.get(id);
      if (!nab) {
        return this.fetch({
          success: __bind(function() {
            console.log("collection: ", this);
            nab = this.get(id);
            return success(nab);
          }, this),
          error: error
        });
      } else {
        return success(nab);
      }
    };
    return NabaztagCollection;
  })();
  this.NabaztagCollection = NabaztagCollection;
}).call(this);

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
    NabaztagAddView.prototype.template = JST['nabaztag_add'];
    NabaztagAddView.prototype.render = function() {
      $(this.el).html(this.template());
      return this;
    };
    NabaztagAddView.prototype.addNabaztag = function() {
      var data, nab, params;
      console.log($('#addNabaztag'));
      data = $('#addNabaztag').serializeArray();
      console.log("save: ", data);
      nab = new Nabaztag();
      params = {};
      _.each(data, __bind(function(item) {
        console.log("item ", item.name, item.value);
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

(function() {
  var NabaztagItemView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  NabaztagItemView = (function() {
    __extends(NabaztagItemView, Backbone.View);
    function NabaztagItemView() {
      this.deleteClick = __bind(this.deleteClick, this);
      this.actionClick = __bind(this.actionClick, this);
      this.render = __bind(this.render, this);
      NabaztagItemView.__super__.constructor.apply(this, arguments);
    }
    NabaztagItemView.prototype.events = {
      'click .delete': "deleteClick",
      'click .action': 'actionClick'
    };
    NabaztagItemView.prototype.template = JST['nabaztag_item'];
    NabaztagItemView.prototype.initialize = function() {
      return this.model.bind('change', this.render);
    };
    NabaztagItemView.prototype.render = function() {
      var actionBtn, isConnected;
      $(this.el).html(this.template(this.model.toJSON()));
      actionBtn = $(this.el).find(".action");
      isConnected = this.model.get("connected");
      if (isConnected) {
        actionBtn.removeClass("disabled");
      } else {
        actionBtn.addClass("disabled");
      }
      return this;
    };
    NabaztagItemView.prototype.actionClick = function() {
      console.log("actionClick");
      if (this.model.get("connected")) {
        return router.navigate("nabaztag/action/" + this.model.id, true);
      }
    };
    NabaztagItemView.prototype.deleteClick = function() {
      return this.model.destroy({
        success: function() {
          return router.nabaztagCollection.remove(this.model);
        }
      });
    };
    return NabaztagItemView;
  })();
  this.NabaztagItemView = NabaztagItemView;
}).call(this);

(function() {
  _.templateSettings = {
    interpolate: /\{\{(.+?)\}\}/g
  };
  $.ajaxPrefilter(function(options, originalOptions, jqXHR) {
    var token;
    token = $.Storage.get("token");
    if (token) {
      return jqXHR.setRequestHeader('token', token);
    }
  });
  $('#topbar').scrollSpy();
}).call(this);

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
