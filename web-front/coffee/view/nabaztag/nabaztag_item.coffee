class NabaztagItemView extends Backbone.View    
    events:
        'click .nab .delete' : "deleteClick"
        'click .nab .btn.action'   : 'actionClick'
   
    template: JST['nabaztag/nabaztag_item']
   
    initialize: ()->
        @model.bind('change', @update)
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        
        actionBtn = $(@el).find(".nab .btn.action")
        isConnected = @model.get("connected")
        if(isConnected)
            actionBtn.removeClass("disabled")
        else
            actionBtn.addClass("disabled")
            
        apps = $(@el).find(".apps")

        $(@el).find(".nab").click(=>
            apps.slideToggle()            
        )
        apps.hide()
        
        @update()
        this
    
    update: =>
        apps = $(@el).find(".apps")
        apps.empty()
        configs = @model.get("applicationConfigList")
        console?.log("configs: ", configs)
        _.each(configs, (config) =>
            cItem = new NabaztagConfigItemView({model: @model, config: config})
            apps.append(cItem.render().el)
        )
    
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