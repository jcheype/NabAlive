package com.nabalive.server.jabber;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    final ClassPathXmlApplicationContext ap;


    public App(String configs[]) {
        ap = new ClassPathXmlApplicationContext(configs);
    }

    public static void main(String[] args) {
        final App app = new App(new String[] {"/com/nabalive/server/jabber/bean.xml"});
    }
}
