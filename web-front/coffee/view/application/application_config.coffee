class ApplicationConfigView extends Backbone.View    
    events:
        "click .save": 'save'
   
    template: JST['application/config']
    templateRfid: JST['application/config_rfid']
    
    templateInput: JST['application/config_input']
        
    
    initialize: (options)=>
        console?.log("model: ", @model)
        console?.log("options: ", options)
        console?.log("app: ", options.application)
        @application = options.application
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        apikey = @application.get("apikey")
        $(@el).find("input.apikey").val(apikey)
        appName = @application.get("name")
        $(@el).find("input.appName").val(appName)
                
        @renderFields()
        
        triggers = @application.get('triggers')
        _.each(triggers, (trigger) =>
            if(trigger=="RFID")
                @addRfid()
        )
        this
        
    renderFields: =>
        form = $(@el).find("form")
        _.each(@application.get('fields'), (field) =>
            if field.type == "INPUT"
                form.append(@templateInput(field))
        )
        
    addRfid: =>
        field = $(@templateRfid())
        select = field.find("select")
        tags = @model.get("tags")

        _.each(tags, (tag)=>
            console?.log("tag", tag)
            select.append("<option value=\"#{tag}\">#{tag}</option>")
        )
        console?.log("field: ", field)
        console?.log("select: ", select)
        console?.log("tags: ", tags)
        $(@el).find("form").append(field)
        
    save: =>
        data = $(@el).find("form").serialize()
        console?.log("data: ", data)
        
        $.post("/nabaztags/#{@model.get('macAddress')}/addconfig", data,
        (res) =>
            console?.log(res)
            router.nabaztagCollection.fetch(
                { success: => router.navigate("nabaztag/list", true) }
            )
        )
        
        
this.ApplicationConfigView = ApplicationConfigView
