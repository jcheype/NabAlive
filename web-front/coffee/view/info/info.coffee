class InfoView extends Backbone.View    
    events:
        'click .close'   : 'closeClick'
   
    template: JST['info/info']
   
    render: =>
        $(@el).html( @template() ).hide()
        this
        
    alert: (text, type) =>
        console?.log("alert", text, type)
        
        if !type
            type = "info"
        $(@el).hide()
        $(@el).find('.alert-message').removeClass().addClass('alert-message').addClass(type)
        $(@el).find('.alert-message').html(text)
        $(@el).slideDown().delay(2000).slideUp()
        
    closeClick: =>
        console?.log("closeClick")



this.InfoView = InfoView