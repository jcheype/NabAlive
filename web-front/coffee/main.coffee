_.templateSettings = {
  interpolate : /\{\{(.+?)\}\}/g
};

$.ajaxPrefilter( ( options, originalOptions, jqXHR ) ->
    # console.log("before options: ", options)
    # console.log("originalOptions: ", originalOptions)
    token = $.Storage.get("token")
    if token
        jqXHR.setRequestHeader('token', token);
);

$('#topbar').scrollSpy()

# odlSync = Backbone.sync
# Backbone.sync = (method, model, options) ->
#     console.log("METHOD:" + method + ": ", model)
#     console.log("options" + ": " , options)
#     token = $.Storage.get("token")
#     if(token)
#         options.headers ?= {}
#         options.headers.token = token
#     odlSync(method, model, options)
