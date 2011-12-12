class Nab2NabsView extends Backbone.View    
        
    template: JST['nab2nabs/nab2nabs']
       
    initialize: ()->
        console?.log("init nab2nabs")
        @model.bind('reset', @render)
   
    render: =>
        $(@el).html( @template() )
        col = $(@el).find(".content")
        @model.each( (nab) ->
            col.append(new NabItemView({model: nab}).render().el)
        )
        this
        
this.Nab2NabsView = Nab2NabsView