class Nabaztag extends Backbone.Model   
    urlRoot: "/nabaztags"
         
    initialize: (attributes) =>
        @set({id: attributes.macAddress})
        @bind('all', =>
            console?.log("@attributes", @attributes)
            @set({id: @attributes.macAddress})
        )
        
    stream: (url, success, error)=>
        apikey = @get("apikey")
        $.get( "/nabaztags/#{apikey}/play", {"url": url}).success(=> success() if success ).error(=> error() if error)
        
    tts: (text, success, error)=>
        apikey = @get("apikey")
        $.get( "/nabaztags/#{apikey}/tts/fr", {"text": text}).success(=> success() if success ).error(=> error() if error)
        
    subscribe: (email, success, error)=>
        apikey = @get("apikey")
        $.get( "/nabaztags/#{apikey}/subscribe", {"email": email})
            .success(=> @fetch(); success() if success )
            .error(=> @fetch(); error() if error)
        
    unsubscribe: (objectId, success, error)=>
        apikey = @get("apikey")
        $.ajax({
            url: "/nabaztags/#{apikey}/subscribe/#{objectId}",
            type: 'DELETE',
            success: => success() if success,
            error: => error() if error,
            complete: => @fetch()
        });
    
    exec: (command, success, error)=>
        apikey = @get("apikey")
        $.get( "/nabaztags/#{apikey}/exec", {"command": command}).success(=> success() if success ).error(=> error() if error)
        
    sleep: (success, error)=>
        apikey = @get("apikey")
        $.get( "/nabaztags/#{apikey}/sleep")
            .success(=> @fetch(); success() if success )
            .error(=> @fetch(); error() if error)
            
    wakeup: (success, error)=>
        apikey = @get("apikey")
        $.get( "/nabaztags/#{apikey}/wakeup")
            .success(=> @fetch(); success() if success )
            .error(=> @fetch(); error() if error)
                
this.Nabaztag = Nabaztag


class NabaztagCollection extends  Backbone.Collection
    model: Nabaztag
    url: "/nabaztags"
    
    getAndRun: (id, success, error) =>
        nab = @get(id)
        if(!nab)
            @fetch({success: =>
                console?.log("collection: ", this)
                nab = @get(id)
                success(nab)
            ,error: error
            })
        else
            success(nab)
    
this.NabaztagCollection = NabaztagCollection