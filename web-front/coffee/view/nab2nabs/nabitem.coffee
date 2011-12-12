class NabItemView extends Backbone.View
    events:
        'submit form.list' : "addClick"
        'submit form.sound' : "sendSubscribers"
        'click .nab .action .btn.tts' : 'showTextForm'
        'click .nab .action .btn.sound' : 'showSoundForm'
        'click .nab .action .btn.list' : 'showSubscribersForm'
            
    template: JST['nab2nabs/nab_item']
   
    initialize: ()->
        @model.bind('change', @render)
   
    render: =>
        console?.log("nabItem: ", @model.get("name"))
        console?.log("nab: ", @model)
        
        $(@el).html( @template(@model.toJSON()) )
        $subs = $(@el).find(".subscriptions")
        
        subs = @model.get("subscribe").sort((a, b)=> return "#{a.ownerFisrtName.toLowerCase()} #{a.ownerLastName.toLowerCase()}" > "#{b.ownerFisrtName.toLowerCase()} #{b.ownerLastName.toLowerCase()}")
        _.each(subs, (sub) =>
            console?.log("subscription: ", sub)
            
            sv = new SubscribeItemView({sub: sub,nab: @model})
            $subs.append(sv.render().el)
        )
        this
        
    addClick: =>
        email = $(@el).find('input.email').val()
        console?.log(email)
        @model.subscribe(email, => router.info.alert("subscription done."))

    sendSubscribers: =>
        url = $(@el).find(".sound input.url").val()
        
        $.get("/nab2nabs/#{@model.get('apikey')}/send", {url: url},
            {
                success:  => router.info.alert("send to subscribers.")
            }
        )
        
    hideForms: =>
        $(@el).find('.forms >div').hide()
    
    showTextForm: =>
        @hideForms()
        $(@el).find('.forms >div.tts').show()
    showSoundForm: =>
        @hideForms()
        $(@el).find('.forms >div.sound').show()
    showSubscribersForm: =>
        @hideForms()
        $(@el).find('.forms >div.list').show()
        
this.NabItemView = NabItemView