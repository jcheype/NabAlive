(function() {
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; };
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
  $(document).ready(__bind(function() {
    return $('#topbar').scrollSpy();
  }, this));
}).call(this);
