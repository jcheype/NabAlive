class ApplicationConfigView extends Backbone.View    
    events:
        "click .save": 'save'
        "click .cancel": 'cancel'
   
    template: JST['application/config']
    templateRfid: JST['application/config_rfid']
    
    templateInput: JST['application/config_input']
    

        
    
    initialize: (options)=>
        console?.log("model: ", @model)
        console?.log("options: ", options)
        console?.log("app: ", options.application)
        console?.log("uuid: ", options.uuid)
        @application = options.application
        @uuid = options.uuid
        configList = @model.get("applicationConfigList")
        @config = @getConfig(@uuid)
        console?.log("configList: ", configList)
        console?.log("config: ", @config)
        
    getConfig: (uuid) =>
        configList = @model.get("applicationConfigList")
        return _.first(_.filter(configList, (conf) => return conf.uuid == uuid ))
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        apikey = @application.get("apikey")
        $(@el).find("input.apikey").val(apikey)
        appName = @application.get("name")
        $(@el).find("input.appName").val(appName)
        if(@config)
            $(@el).find("input.name").val(@config.name)
                
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
                domField = $(@templateInput(field))
                if @config
                    domField.find("input").val(@config.parameters[field.name])
                form.append(domField)
                
        )
        
    isSelected: (tag) =>
        return @config && _.include(@config.tags, tag)
        
    addRfid: =>
        field = $(@templateRfid())
        select = field.find("select")
        tags = @model.get("tags")

        _.each(tags, (tag)=>
            console?.log("tag", tag)
            s = ""
            s = 'selected="selected"' if @isSelected(tag)
            select.append("<option "+s+" value=\"#{tag}\">#{tag}</option>")
        )
        console?.log("field: ", field)
        console?.log("select: ", select)
        console?.log("tags: ", tags)
        $(@el).find("form").append(field)
        
    save: =>
        data = $(@el).find("form").serialize()
        if @config
            data += "&uuid="+@config.uuid
        console?.log("data: ", data)
        
        $.post("/nabaztags/#{@model.get('macAddress')}/addconfig", data,
        (res) =>
            console?.log(res)
            router.nabaztagCollection.fetch(
                { success: => router.navigate("nabaztag/list", true) }
            )
        )
        
    cancel: =>
        router.navigate("nabaztag/list", true)
        
        
this.ApplicationConfigView = ApplicationConfigView
