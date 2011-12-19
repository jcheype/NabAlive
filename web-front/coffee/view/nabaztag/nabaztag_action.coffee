class NabaztagActionView extends Backbone.View    
    events:
        'submit form.play':"play"
        'submit form.tts':"tts"
        'submit form.subscribe':"subscribe"
        'submit form.exec':"exec"
        'click .btn.timeZone':'timeZone'
        'click .btn.wakeup':'wakeup'
        'click .btn.sleep':'sleep'
        'click .btn.week':'schedule'
   
    template: JST['nabaztag/nabaztag_action']
    
    templateDay: JST['nabaztag/day']
   
    days: [
        "Monday",
        "Tuesday",
        "Wenesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
        ]
        
    dayValue:
        "Monday":2
        "Tuesday":3
        "Wenesday":4
        "Thursday":5
        "Friday":6
        "Saturday":7
        "Sunday":1
    
    render: =>
        $(@el).html( @template(@model.toJSON()) )
        $tz = $(@el).find('select.timeZone')
        console?.log("tzTable",tzTable)
        _.each(tzTable, (tz) =>
            opt = $("<option></option>").attr("value",tz).text(tz)
            opt.attr("selected","selected") if tz == @model.get("timeZone")
            $tz.append(opt)
        )
        
        $days = $(@el).find('.days')
        
        sleepValue={}
        _.each(@model.get("sleep"), (s) =>
            sp = s.split("-")
            day = sp[1]
            time=sp[0].split(":")
            sleepValue[day] = time
        )
        wakeupValue={}
        _.each(@model.get("wakeup"), (s) =>
            sp = s.split("-")
            day = sp[1]
            time=sp[0].split(":")
            wakeupValue[day] = time
        )
        console?.log("sleepValue",sleepValue)
        _.each(@days, (day) =>
            s = sleepValue[@dayValue[day]] || ["",""]
            w = wakeupValue[@dayValue[day]] || ["",""]
            console?.log("s",s)
            console?.log("w",w)
            
            data = {'day': day, "sleep": s, "wakeup": w}
            $days.append($(@templateDay(data)).addClass(day) )
        )
        this
    
    play: =>
        url = $('input.play').val()
        @model.stream(url)
        
    tts: =>
        text = $('textarea.tts').val()
        console?.log(text)
        @model.tts(text)

    subscribe: =>
        email = $('input.subscribe').val()
        console?.log(email)
        @model.subscribe(email, => router.info.alert("subscription done."))
    
    exec: =>
        command = $('.command').val()
        @model.exec(command, => router.info.alert("command sent."))
        
    timeZone: =>
        tz = $('select.timeZone').val()
        @model.timeZone(tz, => router.info.alert("timezone set."))
        
    wakeup: =>
        @model.wakeup()
        
    sleep: =>
        @model.sleep()
        
    schedule: =>
        wakeup = []
        sleep = []
        $days = $(@el).find('.days')
        _.each(@days, (day) =>
            inputs = $days.find(".#{day} input")
            wakeup.push("#{inputs[0].value}:#{inputs[1].value}-#{@dayValue[day]}") if inputs[0].value
            sleep.push("#{inputs[2].value}:#{inputs[3].value}-#{@dayValue[day]}") if inputs[2].value
        )
        
        console?.log("wakeup", wakeup)
        console?.log("sleep", sleep)
        @model.schedule(wakeup, sleep, => router.info.alert("schedule set."))
        
        

this.NabaztagActionView = NabaztagActionView