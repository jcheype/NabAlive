package com.nabalive.applications.contes;

import com.google.common.io.ByteStreams;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.google.common.base.Objects.firstNonNull;

@Component("contes")
public class ContesApplication extends ApplicationBase {
    private static Random rand = new Random();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final  ObjectMapper mapper = new ObjectMapper();
    @Override
    public String getApikey() {
        return "0C3F8F93-2B87-4B7E-A6EF-62E0D0121FC1";
    }
    @Autowired
    private MessageService messageService;

    public String getRand() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/contes.json");
        JsonNode jsonNodes = mapper.readTree(inputStream);
        Iterator<Map.Entry<String,JsonNode>> fields = jsonNodes.get("fields").get(0).get("values").getFields();
        List<Map.Entry<String,JsonNode>> listFields = new ArrayList<Map.Entry<String,JsonNode>>();
        while(fields.hasNext()){
            listFields.add(fields.next());
        }
        int sz = listFields.size();
        return listFields.get(rand.nextInt(sz)).getValue().getTextValue();
    }
    
    @Override
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {
        StringBuilder command = new StringBuilder();
        String conte = applicationConfig.getParameters().get("conte").get(0);
        if("rand".equalsIgnoreCase(conte)){
            conte = getRand();
        }

        command.append("ST "+conte+"\nMW\n");

        messageService.sendMessage(nabaztag.getMacAddress(), command.toString());
    }
}
