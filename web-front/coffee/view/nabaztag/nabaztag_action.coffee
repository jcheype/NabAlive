class NabaztagActionView extends Backbone.View    
    events:
        'submit form.play':"play"
        'submit form.tts':"tts"
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
        
    exec: =>
        command = $('.command').val()
        @model.exec(command)

this.NabaztagActionView = NabaztagActionView