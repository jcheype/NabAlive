class ApplicationItemView extends Backbone.View    
    events:
        'click .install'   : 'installClick'
   
    template: JST['application/application_item']
   
       
    initialize: ()->
        @model.bind('change', @render)
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        this
    
    installClick: =>
        console?.log("installClick")
        applicationInstallModalView = new ApplicationInstallModalView({model:@model})
        applicationInstallModalView.render()


this.ApplicationItemView = ApplicationItemView