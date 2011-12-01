class ApplicationCollectionView extends Backbone.View    
   
    template: JST['application/application_collection']
       
    initialize: ()->
        console?.log("init ApplicationCollectionView")
        @model.bind('all', @render)
        @model.fetch()        
   
    render: =>
        $(@el).html( @template() )
        col = $(@el).find(".applicationCollection")
        @model.each( (app) ->
            col.append(new ApplicationItemView({model: app}).render().el)
        )
        this

this.ApplicationCollectionView = ApplicationCollectionView