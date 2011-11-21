class NabaztagView extends Backbone.View    
    events:
        'submit .play':"play"
   
    template: JST['nabaztag']
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        this
    
    play: =>
        url = $('.playUrl').val()
        @model.stream(url)

this.NabaztagView = NabaztagView