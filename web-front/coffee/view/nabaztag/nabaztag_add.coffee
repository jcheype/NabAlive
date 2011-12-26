class NabaztagAddView extends Backbone.View    
    events:
        'submit #addNabaztag': "addNabaztag"
        'click .cancel':"cancel"
   
    template: JST['nabaztag/nabaztag_add']
   
    render: =>
        $(@el).html( @template() )
        this
    
    addNabaztag: =>
        console?.log($('#addNabaztag'))
        data = $('#addNabaztag').serializeArray()
        console?.log("save: ", data)

        nab = new Nabaztag()
        params = {}
        _.each(data, (item) =>
            console?.log("item ", item.name, item.value)
            params[item.name] = item.value
        )
        nab.save(params,{
            success: (newNab) =>
                
                if newNab.attributes.error
                    router.info.alert(newNab.attributes.error, "error")
                else
                    router.nabaztagCollection.add(newNab)
                    router.navigate("nabaztag/list", true)
            error: =>
                router.info.alert("Le nabaztag ne semble pas connecté.", "error")
            
        })
    
    cancel: =>
        $("#addNabaztag").reset()
        router.navigate("nabaztag/list", true)

this.NabaztagAddView = NabaztagAddView