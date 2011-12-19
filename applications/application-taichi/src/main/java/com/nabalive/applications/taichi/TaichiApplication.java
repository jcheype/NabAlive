package com.nabalive.applications.taichi;

import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component("taichi")
public class TaichiApplication extends ApplicationBase {
    private static Random rand = new Random();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getApikey() {
        return "872AC9F0-F513-4980-B4DA-7D57CCB8D20E";
    }
    @Autowired
    private MessageService messageService;

    @Override
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {
        StringBuilder command = new StringBuilder();
        String durationStr = "30";
        if(applicationConfig.getParameters().get("duration") != null){
            durationStr = applicationConfig.getParameters().get("duration").get(0);
        }

        int duration = Integer.parseInt(durationStr)*10;

        messageService.sendMessage(nabaztag.getMacAddress(), "CH http://www.nabalive.com/api/chor/rand/"+duration+"\nMW\n");
    }
}
