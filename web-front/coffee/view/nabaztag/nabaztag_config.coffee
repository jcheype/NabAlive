class NabaztagConfigView extends Backbone.View    
    events:
        "click .tags button.save": "saveTags"
        "click .timeZone button.save": "saveTimeZone"
        "click .days button.save": "saveSchedule"
    
    template: JST['nabaztag/nabaztag_config']
   
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
        
        $(@el).find(".tags .tag .danger").click(
            (event)=>
                console?.log("event", event)
                $(event.srcElement).parent(".tag").remove()
        )
        $tz = $(@el).find('select.timeZone')
        console?.log("tzTable",tzTable)
        _.each(tzTable, (tz) =>
            opt = $("<option></option>").attr("value",tz).text(tz)
            opt.attr("selected","selected") if tz == @model.get("timeZone")
            $tz.append(opt)
        )
        
        $days = $(@el).find('.days table')
        @renderDays($days)
        
        this
        
    renderDays: ($days) =>
        
        sleepValue={}
        _.each(@model.get("sleepLocal"), (s) =>
            sp = s.split("-")
            day = sp[1]
            time=sp[0].split(":")
            sleepValue[day] = time
        )
        wakeupValue={}
        _.each(@model.get("wakeupLocal"), (s) =>
            sp = s.split("-")
            day = sp[1]
            time=sp[0].split(":")
            wakeupValue[day] = time
        )
        _.each(@days, (day) =>
            s = sleepValue[@dayValue[day]] || ["",""]
            w = wakeupValue[@dayValue[day]] || ["",""]            
            data = {'day': day, "sleep": s, "wakeup": w}
            $days.append($(@templateDay(data)).addClass(day) )
        )
        
    saveTags: =>
        console?.log("saveTags")
        $tags = $(@el).find(".tags input")
        tags = []
        _.each($tags,
            (input) =>
                tagId = $(input).attr("name")
                name = $(input).val()
                console?.log(tagId)
                console?.log(name)
                tags.push({"name": name, "value": tagId})
        )
        @model.saveTags(tags,  => router.info.alert("tags saved.") )
    
    saveTimeZone: =>
        tz = $('.timeZone select.timeZone').val()
        @model.timeZone(tz, => router.info.alert("timezone set."))
    
    saveSchedule: =>
        wakeup = []
        sleep = []
        $days = $(@el).find('.days')
        _.each(@days, (day) =>
            inputs = $days.find(".#{day} input")
            wakeup.push("#{inputs[0].value}:#{inputs[1].value}-#{@dayValue[day]}") if inputs[0].value
            sleep.push("#{inputs[2].value}:#{inputs[3].value}-#{@dayValue[day]}") if inputs[2].value
        )
        @model.schedule(wakeup, sleep, => router.info.alert("schedule set."))
        

this.NabaztagConfigView = NabaztagConfigView