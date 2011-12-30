package com.nabalive.applications.callurl;

import com.nabalive.application.core.ApplicationBase;
import com.nabalive.common.server.MessageService;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;

@Component("callurl")
public class CallUrlApplication extends ApplicationBase {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient(
            new AsyncHttpClientConfig.Builder()
                    .setCompressionEnabled(true)
                    .setAllowPoolingConnection(true)
                    .setMaximumConnectionsTotal(1024)
                    .setRequestTimeoutInMs(5000)
                    .setConnectionTimeoutInMs(5000)
                    .setIdleConnectionInPoolTimeoutInMs(30000)
                    .setMaxRequestRetry(3)
                    .setExecutorService(Executors.newFixedThreadPool(8))
                    .build()
    );
    
    @Override
    public String getApikey() {
        return "400A9E89-21F5-43F0-B8A6-24A64F8C8A0E";
    }

    @Autowired
    private MessageService messageService;

    private void doGet(final Nabaztag nabaztag, String url) throws IOException {
        asyncHttpClient.prepareGet(url).execute(new AsyncCompletionHandler() {
            @Override
            public Object onCompleted(Response response) throws Exception {
                String responseBody = response.getResponseBody();
                logger.debug("responseBody",  responseBody);
                if(!responseBody.isEmpty())
                    messageService.sendMessage(nabaztag.getMacAddress(), responseBody);
                return response;
            }
        });

    }
    
    @Override
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {

        String urlToCall = applicationConfig.getParameters().get("callurl").get(0);
        String method = applicationConfig.getParameters().get("method").get(0);
        String parameters = applicationConfig.getParameters().get("parameters").get(0);

        parameters = parameters.replaceAll("\\$\\{object\\.name}", nabaztag.getName());

        if(applicationConfig.getParameters().containsKey("__RFID__")){
            String rfid = applicationConfig.getParameters().get("__RFID__").get(0);
            parameters = parameters.replaceAll("\\$\\{ztamp\\.id\\}", rfid);
        }

        if("GET".equalsIgnoreCase(method)){
            doGet( nabaztag, urlToCall+"?"+parameters);
        }
        else if( "POST".equalsIgnoreCase(method)){
            doPost( nabaztag, urlToCall, parameters);
        }
    }

    private void doPost(final Nabaztag nabaztag, String urlToCall, String parameters) throws IOException {
        asyncHttpClient.preparePost(urlToCall).setBody(parameters).execute(new AsyncCompletionHandler() {
            @Override
            public Object onCompleted(Response response) throws Exception {
                String responseBody = response.getResponseBody();
                logger.debug("responseBody",  responseBody);
                if(!responseBody.isEmpty())
                    messageService.sendMessage(nabaztag.getMacAddress(), responseBody);
                return response;
            }
        });
    }
}
