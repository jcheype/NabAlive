(function() {
  var ApplicationConfigView;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
  ApplicationConfigView = (function() {
    __extends(ApplicationConfigView, Backbone.View);
    function ApplicationConfigView() {
      this.cancel = __bind(this.cancel, this);
      this.save = __bind(this.save, this);
      this.addRfid = __bind(this.addRfid, this);
      this.isSelected = __bind(this.isSelected, this);
      this.renderFields = __bind(this.renderFields, this);
      this.render = __bind(this.render, this);
      this.getConfig = __bind(this.getConfig, this);
      this.initialize = __bind(this.initialize, this);
      ApplicationConfigView.__super__.constructor.apply(this, arguments);
    }
    ApplicationConfigView.prototype.events = {
      "click .save": 'save',
      "click .cancel": 'cancel'
    };
    ApplicationConfigView.prototype.template = JST['application/config'];
    ApplicationConfigView.prototype.templateRfid = JST['application/config_rfid'];
    ApplicationConfigView.prototype.templateInput = JST['application/config_input'];
    ApplicationConfigView.prototype.initialize = function(options) {
      var configList;
      if (typeof console !== "undefined" && console !== null) {
        console.log("model: ", this.model);
      }
      if (typeof console !== "undefined" && console !== null) {
        console.log("options: ", options);
      }
      if (typeof console !== "undefined" && console !== null) {
        console.log("app: ", options.application);
      }
      if (typeof console !== "undefined" && console !== null) {
        console.log("uuid: ", options.uuid);
      }
      this.application = options.application;
      this.uuid = options.uuid;
      configList = this.model.get("applicationConfigList");
      this.config = this.getConfig(this.uuid);
      if (typeof console !== "undefined" && console !== null) {
        console.log("configList: ", configList);
      }
      return typeof console !== "undefined" && console !== null ? console.log("config: ", this.config) : void 0;
    };
    ApplicationConfigView.prototype.getConfig = function(uuid) {
      var configList;
      configList = this.model.get("applicationConfigList");
      return _.first(_.filter(configList, __bind(function(conf) {
        return conf.uuid === uuid;
      }, this)));
    };
    ApplicationConfigView.prototype.render = function() {
      var apikey, appName, triggers;
      $(this.el).html(this.template(this.model.toJSON()));
      apikey = this.application.get("apikey");
      $(this.el).find("input.apikey").val(apikey);
      appName = this.application.get("name");
      $(this.el).find("input.appName").val(appName);
      if (this.config) {
        $(this.el).find("input.name").val(this.config.name);
      }
      this.renderFields();
      triggers = this.application.get('triggers');
      _.each(triggers, __bind(function(trigger) {
        if (trigger === "RFID") {
          return this.addRfid();
        }
      }, this));
      return this;
    };
    ApplicationConfigView.prototype.renderFields = function() {
      var form;
      form = $(this.el).find("form");
      return _.each(this.application.get('fields'), __bind(function(field) {
        var domField;
        if (field.type === "INPUT") {
          domField = $(this.templateInput(field));
          if (this.config) {
            domField.find("input").val(this.config.parameters[field.name]);
          }
          return form.append(domField);
        }
      }, this));
    };
    ApplicationConfigView.prototype.isSelected = function(tag) {
      return this.config && _.include(this.config.tags, tag);
    };
    ApplicationConfigView.prototype.addRfid = function() {
      var field, select, tags;
      field = $(this.templateRfid());
      select = field.find("select");
      tags = this.model.get("tags");
      _.each(tags, __bind(function(tag) {
        var s;
        if (typeof console !== "undefined" && console !== null) {
          console.log("tag", tag);
        }
        s = "";
        if (this.isSelected(tag)) {
          s = 'selected="selected"';
        }
        return select.append("<option " + s + (" value=\"" + tag + "\">" + tag + "</option>"));
      }, this));
      if (typeof console !== "undefined" && console !== null) {
        console.log("field: ", field);
      }
      if (typeof console !== "undefined" && console !== null) {
        console.log("select: ", select);
      }
      if (typeof console !== "undefined" && console !== null) {
        console.log("tags: ", tags);
      }
      return $(this.el).find("form").append(field);
    };
    ApplicationConfigView.prototype.save = function() {
      var data;
      data = $(this.el).find("form").serialize();
      if (this.config) {
        data += "&uuid=" + this.config.uuid;
      }
      if (typeof console !== "undefined" && console !== null) {
        console.log("data: ", data);
      }
      return $.post("/nabaztags/" + (this.model.get('macAddress')) + "/addconfig", data, __bind(function(res) {
        if (typeof console !== "undefined" && console !== null) {
          console.log(res);
        }
        return router.nabaztagCollection.fetch({
          success: __bind(function() {
            return router.navigate("nabaztag/list", true);
          }, this)
        });
      }, this));
    };
    ApplicationConfigView.prototype.cancel = function() {
      return router.navigate("nabaztag/list", true);
    };
    return ApplicationConfigView;
  })();
  this.ApplicationConfigView = ApplicationConfigView;
}).call(this);
