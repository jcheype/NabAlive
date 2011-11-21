class Nabaztag extends Backbone.Model   
    urlRoot: "/nabaztags"
         
    initialize: (attributes) =>
        # console.log(attributes)
        @set({id: attributes.idString})
        
    stream: (url, success)=>
        apikey = @get("apikey")
        jQuery.getJSON( "/nabaztags/#{apikey}/play", {"url": url}, success)
        
this.Nabaztag = Nabaztag

class NabaztagCollection extends  Backbone.Collection
    model: Nabaztag
    url: "/nabaztags"
    
    getAndRun: (id, success, error) =>
        nab = @get(id)
        if(!nab)
            @fetch({success: =>
                console.log("collection: ", this)
                nab = @get(id)
                success(nab)
            ,error: error
            })
        else
            success(nab)
    
this.NabaztagCollection = NabaztagCollection