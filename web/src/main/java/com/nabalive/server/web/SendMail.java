package com.nabalive.server.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/20/11
 */
public class SendMail {
    private final static Logger logger = LoggerFactory.getLogger(SendMail.class);

    private static Executor tpe = Executors.newFixedThreadPool(5);

    private static class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = System.getProperty("mail.user");
            String password = System.getProperty("mail.password");
            return new PasswordAuthentication(username, password);
        }
    }

    private static void postMailBG( String recipients[ ], String subject, String message , String from) throws MessagingException
    {
        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", System.getProperty("mail.smtp.host"));
        props.put("mail.smtp.auth", "true");

        // create some properties and get the default Session


        Session session = Session.getDefaultInstance(props, new SMTPAuthenticator());
        //session.setDebug(true);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++)
        {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

    public static void postMail(final String recipients[ ],final String subject,final String message ,final String from){
        tpe.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    postMailBG(recipients, subject, message, from);
                    Thread.sleep(60000);
                } catch (MessagingException e) {
                    logger.error("cannot send mail:", e);
                } catch (InterruptedException e) {
                    logger.error("mail wait interrupted:", e);
                }
            }
        });
    }
}
