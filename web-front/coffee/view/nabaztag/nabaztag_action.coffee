class NabaztagActionView extends Backbone.View    
    events:
        'submit form.play':"play"
        'submit form.tts':"tts"
        'submit form.subscribe':"subscribe"
        'submit form.exec':"exec"
   
    template: JST['nabaztag/nabaztag_action']
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        this
    
    play: =>
        url = $('input.play').val()
        @model.stream(url)
        
    tts: =>
        text = $('textarea.tts').val()
        console?.log(text)
        @model.tts(text)

    subscribe: =>
        email = $('input.subscribe').val()
        console?.log(email)
        @model.subscribe(email, => router.info.alert("subscription done."))
    
    exec: =>
        command = $('.command').val()
        @model.exec(command)

this.NabaztagActionView = NabaztagActionView