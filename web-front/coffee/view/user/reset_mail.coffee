class ResetMailView extends Backbone.View    
    events:
        'submit form'   : 'submit'
   
    template: JST['user/reset_mail']
   
       
    initialize: (options)->
        @options = options
   
    render: =>
        $(@el).html( @template() )
        this
    
    submit: =>
        console?.log("submit")
        email = $(@el).find(".email").val()
        $.get("/user/reset/mail", {'email':email}).success(
            =>
                router.info.alert("mail sent.")
                router.navigate("doc", true)
        ).error(
            =>
                router.info.alert("error.", "error")
                router.navigate("doc", true)
        )
        
this.ResetMailView = ResetMailView