package com.nabalive.applications.mood;

import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

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
    public void onStartup(String mac, ApplicationConfig applicationConfig) throws Exception {
        StringBuilder command = new StringBuilder();

        command.append("MU http://karotz.s3.amazonaws.com/moods/fr/"+(rand.nextInt(304)+1)+".mp3\nPL 3\nMW\n");

        messageService.sendMessage(mac, command.toString());
    }
}
