class NabaztagCollectionView extends Backbone.View    
    events:
        'click .addNabaztag': "addNabaztag"
   
    template: JST['nabaztag/nabaztag_collection']
       
    initialize: ()->
        console?.log("init NabaztagListView")
        
        @model.bind('reset', @render)
        @model.bind('add', @render)
        @model.bind('remove', @render)
        #@model.bind('all', @render)
        @model.fetch()
   
    render: =>
        $(@el).html( @template() )
        col = $(@el).find(".content")
        @model.each( (nab) ->
            col.append(new NabaztagItemView({model: nab}).render().el)
        )
        this
    
    addNabaztag: =>
        console?.log("addNabaztag")
        router.navigate("nabaztag/add", true)

this.NabaztagCollectionView = NabaztagCollectionView