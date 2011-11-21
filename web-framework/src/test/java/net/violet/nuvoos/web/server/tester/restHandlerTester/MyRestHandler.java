package net.violet.nuvoos.web.server.tester.restHandlerTester;

import com.nabalive.framework.web.*;
import com.nabalive.framework.web.exception.HttpException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 10/20/11
 */
public class MyRestHandler extends SimpleRestHandler {
    public MyRestHandler() {
        get(new Route("/api/montest") {
            @Override
            public void handle(Request request, Response response, Map<String,String> mapper) {
                response.write("OK");
            }
        });

        get(new Route("/api/:name/:age") {
            @Override
            public void handle(Request request, Response response, Map<String,String> mapper) {
                String result = mapper.toString();
                response.write(result);
            }
        });

        before(new Route(".*"){
            private AtomicInteger counter = new AtomicInteger();
            @Override
            public void handle(Request request, Response response, Map<String, String> map) throws HttpException {
                final int i = counter.incrementAndGet();
                if(i % 1000 == 0)
                    System.out.println("hello : " + i);
            }
        });

        staticServe("/.*", new HttpStaticFileServerHandler(System.getProperty("user.home")+"/tmp"));
    }
}
