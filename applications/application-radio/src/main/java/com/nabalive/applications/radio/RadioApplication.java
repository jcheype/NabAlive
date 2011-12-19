package com.nabalive.applications.radio;

import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("radio")
public class RadioApplication extends ApplicationBase {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getApikey() {
        return "0C11C8D4-F5D2-4C0C-988A-109E48BFAFF2";
    }
    @Autowired
    private MessageService messageService;

    @Override
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {
        String radio = applicationConfig.getParameters().get("custom").get(0);
        if(radio.trim().isEmpty()){
            radio = applicationConfig.getParameters().get("radio").get(0);
        }
        messageService.sendMessage(nabaztag.getMacAddress(), "ST "+radio+"\nMW\n");
    }
}
