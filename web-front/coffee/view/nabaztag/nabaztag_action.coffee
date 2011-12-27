class NabaztagActionView extends Backbone.View    
    events:
        'submit form.play':"play"
        'submit form.tts':"tts"
        'submit form.exec':"exec"
        'click .btn.wakeup':'wakeup'
        'click .btn.sleep':'sleep'
   
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

    exec: =>
        command = $('.command').val()
        @model.exec(command, => router.info.alert("command sent."))
        
        
    wakeup: =>
        @model.wakeup()
        
    sleep: =>
        @model.sleep()
        
        
        

this.NabaztagActionView = NabaztagActionView