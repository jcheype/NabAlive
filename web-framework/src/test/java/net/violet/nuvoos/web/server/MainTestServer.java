package net.violet.nuvoos.web.server;

import com.nabalive.framework.web.HttpServer;
import net.violet.nuvoos.web.server.tester.restHandlerTester.MyRestHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 10/20/11
 */
public class MainTestServer {

    public static void main(String args[]) throws InterruptedException {
        final HttpServer httpServer = new HttpServer();
        httpServer.setRestHandler(new MyRestHandler());

        httpServer.start();

        Thread.sleep(Long.MAX_VALUE);
    }
}
