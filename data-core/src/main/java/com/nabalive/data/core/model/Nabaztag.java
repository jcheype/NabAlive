package com.nabalive.data.core.model;

import com.google.code.morphia.annotations.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/16/11
 */

@Entity("nabaztag")
public class Nabaztag {
    private final static Pattern schedulePattern = Pattern.compile("(\\d+):(\\d+)-(\\d)");

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NotNull
    private String macAddress;

    private String name;

    @Indexed
    private String apikey;

    private ObjectId owner;

    private String timeZone;

    @Indexed
    private Set<Subscription> subscribe = new HashSet<Subscription>();

    @Indexed
    private Set<String> wakeup = new HashSet<String>();

    @Indexed
    private Set<String> sleep = new HashSet<String>();

    @Transient
    private boolean connected = false;

    @Embedded()
    List<ApplicationConfig> applicationConfigList = new ArrayList<ApplicationConfig>();

    List<Tag> tags = new ArrayList<Tag>();


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addApplicationConfig(ApplicationConfig config) {
        applicationConfigList.add(config);
    }

    public void removeApplicationConfig(String uuid) {
        Iterator<ApplicationConfig> iterator = applicationConfigList.iterator();
        while (iterator.hasNext()) {
            ApplicationConfig applicationConfig = iterator.next();
            if (applicationConfig.getUuid().equals(uuid)) {
                iterator.remove();
                return;
            }
        }
    }

    public List<ApplicationConfig> getApplicationConfigList() {
        return applicationConfigList;
    }

    public ObjectId getOwner() {
        return owner;
    }

    public void setOwner(ObjectId owner) {
        this.owner = owner;
    }

    public void setOwner(String owner) {
        setOwner(new ObjectId(owner));
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey.toLowerCase();
    }

    public String getIdString() {
        return id.toString();
    }

    public String getOwnerIdString() {
        return owner.toString();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Set<Subscription> getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Set<Subscription> subscribe) {
        this.subscribe = subscribe;
    }

    public Set<String> getWakeup() {
        return wakeup;
    }

    public Set<String> getSleep() {
        return sleep;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean hasTag(String tagValue) {
        for (Tag tag : tags) {
            if (tag.getValue().equals(tagValue))
                return true;
        }
        return false;
    }


    public void setSleepLocal(List<String> localSleep){
        List<String> utc = Nabaztag.changeTz(localSleep, getTimeZone(), "UTC");
        getSleep().clear();
        for(String schedule: utc)
        {
            getSleep().add(schedule);
        }
    }

    public void setWakeupLocal(List<String> localWakeup){
        List<String> utc = Nabaztag.changeTz(localWakeup, getTimeZone(), "UTC");
        getWakeup().clear();
        for(String schedule: utc)
        {
            getWakeup().add(schedule);
        }
    }

    public List<String> getSleepLocal(){
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> list = new ArrayList<String>(getSleep());
        List<String> local = Nabaztag.changeTz(list, "UTC", getTimeZone());
        for(String schedule: local)
        {
            result.add(schedule);
        }
        return result;
    }

    public List<String> getWakeupLocal(){
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> list = new ArrayList<String>(getWakeup());
        List<String> local = Nabaztag.changeTz(list, "UTC", getTimeZone());
        for(String schedule: local)
        {
            result.add(schedule);
        }
        return result;
    }

    public static DateTime convertJodaTimezone(LocalDateTime date, String srcTz, String destTz) {
        DateTime srcDateTime = date.toDateTime(DateTimeZone.forID(srcTz));
        DateTime dstDateTime = srcDateTime.withZone(DateTimeZone.forID(destTz));
        return dstDateTime.toLocalDateTime().toDateTime();
    }

    public static List<String> changeTz(List<String> from, final String fromTimeZone, final String toTimeZone) {
        return Lists.transform(from, new Function<String, String>() {
            @Override
            public String apply(@Nullable String s) {
                Matcher matcher = schedulePattern.matcher(s);
                if (matcher.matches()) {
                    String hour = matcher.group(1);
                    String minute = matcher.group(2);
                    String day = matcher.group(3);

                    LocalDateTime dateTime = new LocalDateTime(2018, 1, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute));
                    DateTime converted = convertJodaTimezone(dateTime, fromTimeZone, toTimeZone);
                    return String.format("%02d:%02d-%s", converted.hourOfDay().get(), converted.minuteOfHour().get(), converted.dayOfWeek().get());
                }
                return null;
            }
        });
    }
}
