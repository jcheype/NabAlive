class ApplicationItemView extends Backbone.View    
    events:
        'click .install'   : 'installClick'
   
    template: JST['application/application_item']
   
       
    initialize: ()->
        @model.bind('change', @render)
   
    render: =>
        jsonModel = @model.toJSON()
        jsonModel.description += "<br/><ul>"
        if(jsonModel.triggers.length == 1 && jsonModel.triggers[0] == "RFID")
            jsonModel.description += "<li>Utilisable exclusivement avec Nanoz ou Stampz</li>"
        else
            jsonModel.description += "<li>Utilisable avec Nanoz ou Stampz</li>" if _.include(jsonModel.triggers, "RFID")
            jsonModel.description += "<li>Declenchement automatique</li>" if _.include(jsonModel.triggers, "PERMANENT")
        
        jsonModel.description += "</ul>"
        $(@el).html( @template(jsonModel) )
        this
    
    installClick: =>
        console?.log("installClick")
        applicationInstallModalView = new ApplicationInstallModalView({model:@model})
        applicationInstallModalView.render()


this.ApplicationItemView = ApplicationItemView