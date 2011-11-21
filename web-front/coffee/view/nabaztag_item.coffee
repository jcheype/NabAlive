class NabaztagItemView extends Backbone.View    
    events:
        'click .delete' : "deleteClick"
        'click .action'   : 'actionClick'
   
    template: JST['nabaztag_item']
   
       
    initialize: ()->
        @model.bind('change', @render)
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        
        actionBtn = $(@el).find(".action")
        isConnected = @model.get("connected")
        if(isConnected)
            actionBtn.removeClass("disabled")
        else
            actionBtn.addClass("disabled")
        this
    
    actionClick: =>
        console.log("actionClick")
        if @model.get("connected")
            router.navigate("nabaztag/action/#{@model.id}", true)
    
    deleteClick: =>
        @model.destroy({success: () ->
            router.nabaztagCollection.remove(@model)
        })

this.NabaztagItemView = NabaztagItemView