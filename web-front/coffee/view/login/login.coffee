class LoginView extends Backbone.View    
    template: JST['login/login']
       
    initialize: ()->
        $('#loginmodal').modal({keyboard: true})
        $('#registermodal').modal({keyboard: true})
        $('#doLogin').live("submit", @doLogin)
        $('#doRegister').live("submit", @doRegister)
        $('#loginmodal').live('hide', @reset)
        $('#registermodal').live('hide', @reset)

   
    render: =>
        $(@el).html( @template() )
        $(@el).find('button.login').click(@doLogin)
        $(@el).find('button.register').click(@doRegister)
        $(@el).find('button.cancel').click(=>
            $('#loginmodal').modal('hide')
            $('#registermodal').modal('hide')
        )
        this
        
    reset: =>
        $('#loginmodal').find(':input','#myform')
            .not(':button, :submit, :reset, :hidden')
            .val('')
            .removeAttr('checked')
            .removeAttr('selected')
        $('#registermodal').find(':input','#myform')
            .not(':button, :submit, :reset, :hidden')
            .val('')
            .removeAttr('checked')
            .removeAttr('selected')
    
    doLogin: =>
        console?.log("doLogin")
        dataSent = $('#doLogin').serialize();

        $.post('/user/login', dataSent, (data, textStatus) ->
            if(textStatus == 'success')
                $.Storage.set("token", data)
                $('#loginmodal').modal('hide')
                router.navigate("onLogin", true)
            console?.log(data)
        , "json").error(=>
            $('#loginmodal').modal('hide')
            $('#registermodal').modal('hide')
            router.info.alert("Erreur d'authentification.", "error")
        );
        

    doRegister: =>
        console?.log("doRegister")
        dataSent = $('#doRegister').serialize();

        $.post('/user/register', dataSent, (data, textStatus) ->
            if(textStatus == 'success')
                $.Storage.set("token", data)
                $('#registermodal').modal('hide')
                router.navigate("onLogin", true)
            console?.log(data)
        , "json").error(=>
                $('#loginmodal').modal('hide')
                $('#registermodal').modal('hide')
                router.info.alert("Erreur d'enregistrement.", "error")
            );     
        

this.LoginView = LoginView