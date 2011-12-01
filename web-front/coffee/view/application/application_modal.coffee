class ApplicationInstallModalView extends Backbone.View    
    template: JST['application/install_modal']
   
    render: =>
        $(@el).html( @template(@model.toJSON()) )

        $('#modals').html($(@el))
        @modal = $('#modals .modal')
        @modal.bind('hide', => $('#modals').empty())
        @modal.find('button.select').click(@doInstall)
        @modal.find('button.cancel').click(=>
            @modal.modal('hide')
        )
        @modal.modal({keyboard: true, show: true, backdrop: true})
        @select = @modal.find('select')
        c = router.nabaztagCollection
        c.fetch(
            { success: => c.each((nab) => @select.append("<option value=\"#{nab.get('macAddress')}\">#{nab.get('name')}</option>") ) }
        )
        this
    
    doInstall: =>
        console?.log("doInstall")
        @modal.modal('hide')
        router.navigate("nabaztag/#{@select.val()}/appinstall/#{@model.get('apikey')}", true)

this.ApplicationInstallModalView = ApplicationInstallModalView