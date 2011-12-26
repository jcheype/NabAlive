class NabaztagConfigView extends Backbone.View    
    events:
        "click .tags button.save": "saveTags"
    
    template: JST['nabaztag/nabaztag_config']
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        this
        
    saveTags: =>
        console?.log("saveTags")
        $tags = $(@el).find(".tags input")
        tags = []
        _.each($tags,
            (input) =>
                tagId = $(input).attr("name")
                name = $(input).val()
                console?.log(tagId)
                console?.log(name)
                tags.push({"name": name, "value": tagId})
        )
        @model.saveTags(tags,  => router.info.alert("tags saved.") )
        

this.NabaztagConfigView = NabaztagConfigView