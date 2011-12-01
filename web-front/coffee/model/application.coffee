class Application extends Backbone.Model   
    urlRoot: "/applications"
    
    initialize: (attributes) =>
        @set({id: attributes.apikey})

this.Application = Application

class ApplicationCollection extends  Backbone.Collection
    model: Application
    url: "/applications"
    
    getAndRun: (id, success, error) =>
        app = @get(id)
        if(!app)
            @fetch({success: =>
                console?.log("collection: ", this)
                app = @get(id)
                success(app)
            ,error: error
            })
        else
            success(app)
    
this.ApplicationCollection = ApplicationCollection
