class SubscribeItemView extends Backbone.View
    events:
        'click .delete'   : 'deleteClick'
        
    template: JST['nab2nabs/subscribe_item']
   
    initialize: (options)->
        @nab = options.nab
        @sub = options.sub
   
    render: =>
        console?.log("SubscribeItemView: ", @sub.name)
        
        $(@el).html( @template(@sub) )
        this
        
    deleteClick: =>
        @nab.unsubscribe(@sub.objectId)

this.SubscribeItemView = SubscribeItemView