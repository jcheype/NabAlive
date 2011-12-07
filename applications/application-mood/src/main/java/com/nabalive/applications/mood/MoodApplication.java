package com.nabalive.applications.mood;

import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

import static com.google.common.base.Objects.firstNonNull;

@Component("moods")
public class MoodApplication extends ApplicationBase {
    private static Random rand = new Random();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getApikey() {
        return "BC330670-6D25-4FB7-8613-EFD384D035E1";
    }
    @Autowired
    private MessageService messageService;

    @Override
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {
        StringBuilder command = new StringBuilder();
        String lang = "fr";
        if(applicationConfig.getParameters().get("lang") != null){
            lang = applicationConfig.getParameters().get("lang").get(0);
        }

        command.append("MU http://karotz.s3.amazonaws.com/moods/"+lang+"/"+(rand.nextInt(304)+1)+".mp3\nMW\n");

        messageService.sendMessage(nabaztag.getMacAddress(), command.toString());
    }
}
