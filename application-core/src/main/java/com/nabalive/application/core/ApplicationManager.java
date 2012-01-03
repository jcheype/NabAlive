package com.nabalive.application.core;

import com.google.common.io.ByteStreams;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;
import com.nabalive.data.core.dao.ApplicationLogoDAO;
import com.nabalive.data.core.dao.ApplicationStoreDAO;
import com.nabalive.data.core.model.ApplicationLogo;
import com.nabalive.data.core.model.ApplicationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApplicationManager implements ApplicationContextAware, BeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationStoreDAO applicationStoreDAO;
    @Autowired
    private ApplicationLogoDAO applicationLogoDAO;

    private ApplicationContext applicationContext;
    
    private Map<String , Application> applicationMap = new HashMap<String, Application>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Application getApplication(String apikey) {
        return applicationMap.get(apikey);
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if (o instanceof Application) {
            Application application = (Application) o;
            try {
                registerApp(application, s);
            } catch (IOException e) {
                logger.error("cannot register {}:", application, e);
            }
        }
        return o;
    }

    private void registerApp(Application application, String name) throws IOException {
        logger.debug("registering: {}", name);
        if(name.endsWith("groovy")){
            return;
        }

        InputStream inputStream = application.getClass().getResourceAsStream("/"+name + ".json");
        String jsonString = new String(ByteStreams.toByteArray(inputStream), "UTF-8");

        InputStream logoInputStream = application.getClass().getResourceAsStream("/"+name + ".png");
        byte[] logo = ByteStreams.toByteArray(logoInputStream);
        registerApp(application, name, jsonString, logo);
    }

    public void registerApp(Application application, String name, String jsonString, byte[] logo) throws IOException {
        logger.debug("registering: {}", name);
        DBObject dbObject = (DBObject) JSON.parse(jsonString);
        dbObject.put("className", ApplicationStore.class.getName());
        dbObject.put("name", name);
        dbObject.put("apikey", application.getApikey());

        BasicDBObject query = new BasicDBObject("apikey", application.getApikey());

        logger.debug("upsert: {}", dbObject);
        applicationStoreDAO.getCollection().update(query, dbObject, true, false, WriteConcern.SAFE);

        applicationLogoDAO.deleteById(application.getApikey());
        ApplicationLogo applicationLogo = new ApplicationLogo();
        applicationLogo.setData(logo);
        applicationLogo.setApikey(application.getApikey());
        applicationLogo.setContentType("application/octet-stream");
        applicationLogo.setFilename("logo.png");
        applicationLogoDAO.save(applicationLogo);

        applicationMap.put(application.getApikey(), application);
    }
    
    public void unRegister(ApplicationStore applicationStore){
        applicationLogoDAO.deleteById(applicationStore.getApikey());
        applicationStoreDAO.delete(applicationStore);
    }

    public void unRegisterByName(String name){
        ApplicationStore applicationStore = applicationStoreDAO.findOne("name", name);
        if(applicationStore != null)
            unRegister(applicationStore);
    }
}
