class NabaztagItemView extends Backbone.View    
    events:
        'click .nab .delete' : "deleteClick"
        'click .action'   : 'actionClick'
   
    template: JST['nabaztag/nabaztag_item']
   
       
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
            
        apps = $(@el).find(".apps")
        apps.hide()
        $(@el).find(".nab").click(=>
            apps.slideToggle()
        )
        
        configs = @model.get("applicationConfigList")
        console?.log("configs: ", configs)
        _.each(configs, (config) =>
            cItem = new NabaztagConfigItemView({config: config})
            apps.append(cItem.render().el)
        )
        this
    
    actionClick: =>
        console?.log("actionClick")
        if @model.get("connected")
            router.navigate("nabaztag/action/#{@model.id}", true)
    
    deleteClick: =>
        console?.log("deleteClick")
        @model.destroy({success: () ->
            router.nabaztagCollection.remove(@model)
        })

this.NabaztagItemView = NabaztagItemView