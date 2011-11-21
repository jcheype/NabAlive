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
