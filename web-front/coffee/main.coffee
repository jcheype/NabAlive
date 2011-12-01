_.templateSettings = {
  interpolate : /\{\{(.+?)\}\}/g
};

$.ajaxPrefilter( ( options, originalOptions, jqXHR ) ->
    token = $.Storage.get("token")
    if token
        jqXHR.setRequestHeader('token', token);
);

$(document).ready(=> $('#topbar').scrollSpy())
