class NabaztagConfigItemView extends Backbone.View    
    events:
        'click .delete'   : 'deleteClick'
   
    template: JST['nabaztag/config_item']
   
       
    initialize: (options)->
        @config = options.config
   
    render: =>
        $(@el).html( @template(@config) )
        this
    
    deleteClick: =>
        console?.log("deleteClick")
        $.ajax({ type: "DELETE", url: "/config/#{@config.uuid}"})
        router.nabaztagCollection.fetch()
        

this.NabaztagConfigItemView = NabaztagConfigItemView