package com.nabalive.server.web;

import com.nabalive.framework.web.HttpServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    private ClassPathXmlApplicationContext ap;

    public App(String[] configs){
        ap = new ClassPathXmlApplicationContext(configs);
        final HttpServer httpserver = ap.getBean("httpserver", HttpServer.class);

        httpserver.setPort(Integer.parseInt(System.getProperty("port", "8999")));

        httpserver.start();
        System.out.println("HTTP STARTED");
    }

    public static void main(String[] args) throws InterruptedException {
//        new App(new String[] {"/com/ma/mongo/core/bean.xml","/bean.xml"});
        new App(new String[] {"/application-context.xml"});
        Thread.sleep(Long.MAX_VALUE);
    }
}
