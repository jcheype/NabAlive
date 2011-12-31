package com.nabalive.application.core;

import com.google.common.io.ByteStreams;
import com.nabalive.application.core.util.FolderWatcher;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/30/11
 */

@Component
public class ApplicationGroovyLoader implements ApplicationContextAware {
    private static String APPS_FOLDER = System.getProperty("apps.folder", "apps");
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    @Autowired
    ApplicationManager applicationManager;

    static String stripExtension (String str) {
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }


    @PostConstruct
    public void init(){
        File scriptFolder = new File(APPS_FOLDER);

        if (!scriptFolder.isDirectory()) {
            throw new IllegalStateException("parameter \"apps.folder\" must point to a directory: " + APPS_FOLDER);
        }

        Runnable folderWatcher = new FolderWatcher(scriptFolder) {
            @Override
            protected void onChange(File file) {
                logger.debug("FolderWatcher onChange: {}", file.getName());
                if(!file.exists()){
                    applicationManager.unRegisterByName(stripExtension(file.getName()));
                    return;
                }
                else if(file.getName().endsWith(".zip")){
                    try {
                        registerZip(file);
                    } catch (Exception e) {
                        logger.error("cannot load app: {}", file.getName(), e);
                    }
                }
            }
        };

        new Thread(folderWatcher).start();

    }

    private void registerZip(File file) throws IOException, InstantiationException, IllegalAccessException {
        ZipInputStream zipinputstream = new ZipInputStream(
                new FileInputStream(file));
        
        String name = stripExtension(file.getName());
        Application application = null;
        byte[] logo = null;
        String descriptor = null;

        ZipEntry zipentry = zipinputstream.getNextEntry();
        while(zipentry != null){
            String entryName = zipentry.getName();
            if("main.groovy".equalsIgnoreCase(entryName)){
                application = loadApplication(zipentry, zipinputstream, name);
            }
            else if("icon.png".equalsIgnoreCase(entryName)){
                logo = loadLogo(zipentry, zipinputstream);
            }
            else if("descriptor.json".equalsIgnoreCase(entryName)){
                descriptor = loadDescriptor(zipentry, zipinputstream);
            }
            zipinputstream.closeEntry();
            zipentry = zipinputstream.getNextEntry();
        }
        zipinputstream.close();

        if(application != null && logo != null && descriptor != null){
            applicationManager.registerApp(application, name, descriptor, logo);
        }
    }

    private String loadDescriptor(ZipEntry zipentry, ZipInputStream zipinputstream) throws IOException {
        String jsonString = new String(ByteStreams.toByteArray(zipinputstream), "UTF-8");

        return jsonString;
    }

    private byte[] loadLogo(ZipEntry zipentry, ZipInputStream zipinputstream) throws IOException {
        return ByteStreams.toByteArray(zipinputstream);
    }

    private Application loadApplication(ZipEntry zipentry, ZipInputStream zipinputstream, String name) throws IOException, IllegalAccessException, InstantiationException {
        String groovyString = new String(ByteStreams.toByteArray(zipinputstream), "UTF-8");

        GroovyClassLoader gcl = new GroovyClassLoader();
        Class clazz = gcl.parseClass(groovyString);

        Object aScript = clazz.newInstance();
        Application application = (Application) aScript;

        applicationContext.getAutowireCapableBeanFactory().initializeBean(application, name+".groovy");
        return application;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
