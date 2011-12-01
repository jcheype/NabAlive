package com.nabalive.application.core;

import com.nabalive.data.core.dao.ApplicationStoreDAO;
import com.nabalive.data.core.model.ApplicationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ApplicationManager implements ApplicationContextAware, BeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationStoreDAO applicationStoreDAO;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Application getApplication(String apikey){
        Map<String, Application> applicationMap = applicationContext.getBeansOfType(Application.class);
        for(Map.Entry<String, Application> entry: applicationMap.entrySet()){
            if(apikey.equals(entry.getValue().getApikey())){
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if(o instanceof Application){
            Application application = (Application) o;
            ApplicationStore applicationStore = applicationStoreDAO.findOne("apikey", application.getApikey());
            if(applicationStore == null){
                registerApp(application, s);
            }
        }
        return o;
    }

    private void registerApp(Application application, String name) {
        logger.debug("registering: {}", name);
        ApplicationStore applicationStore = new ApplicationStore();
        applicationStore.setApikey(application.getApikey());
        applicationStore.setName(name);
        applicationStoreDAO.save(applicationStore);
    }
}
