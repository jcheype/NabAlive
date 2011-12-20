class ResetPasswordView extends Backbone.View    
    events:
        'submit form'   : 'submit'
   
    template: JST['user/reset_password']
   
       
    initialize: (options)->
        @options = options
   
    render: =>
        $(@el).html( @template() )
        this
    
    submit: =>
        console?.log("submit")
        @options.password = $(@el).find(".password").val()
        $.post("/user/reset", @options).success(
            =>
                router.info.alert("password reset.")
                router.navigate("doc", true)
        ).error(
            =>
                router.info.alert("error.", "error")
                router.navigate("doc", true)
        )
        
this.ResetPasswordView = ResetPasswordView