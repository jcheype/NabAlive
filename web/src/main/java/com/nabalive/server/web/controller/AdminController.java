package com.nabalive.server.web.controller;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.nabalive.data.core.dao.NabaztagDAO;
import com.nabalive.data.core.dao.UserDAO;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.data.core.model.User;
import com.nabalive.framework.web.Request;
import com.nabalive.framework.web.Response;
import com.nabalive.framework.web.Route;
import com.nabalive.framework.web.SimpleRestHandler;
import com.nabalive.framework.web.exception.HttpException;
import com.nabalive.server.jabber.ConnectionManager;
import com.nabalive.server.jabber.Status;
import com.nabalive.server.web.Token;
import com.nabalive.server.web.TokenUtil;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 12/15/11
 */

@Component
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleRestHandler restHandler;

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private NabaztagDAO nabaztagDAO;


    @Autowired
    private UserDAO userDAO;

    private Supplier<Long> awakeSupplier = Suppliers.memoizeWithExpiration(new AwakeSupplier(), 5, TimeUnit.SECONDS);
    private Supplier<List<Nab>> detailSupplier = Suppliers.memoizeWithExpiration(new DetailSupplier(), 1, TimeUnit.MINUTES);

    @PostConstruct
    void init() {
        restHandler
                .get(new Route("/admin/connected/infos") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        long nabaztagRegistered = nabaztagDAO.count();
                        long users = userDAO.count();
                        Infos infos = new Infos(connectionManager.size(), awakeSupplier.get(), nabaztagRegistered, users);
                        response.writeJSON(infos);
                    }
                })
                .get(new Route("/admin/connected/infos/details") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        response.writeJSON(detailSupplier.get());
                    }
                })
                .get(new Route("/admin/connected/list") {
                    @Override
                    public void handle(Request request, Response response, Map<String, String> map) throws Exception {
                        Token token = TokenUtil.decode(checkNotNull(request.getParamOrHeader("token")), Token.class);
                        User user = checkNotNull(userDAO.get(token.getUserId()));
                        if (!user.getPermissions().contains("admin"))
                            throw new HttpException(HttpResponseStatus.UNAUTHORIZED, "UNAUTHORIZED access");

                        response.writeJSON(connectionManager.keySet());
                    }
                });
    }
    
    class Infos{
        public final long connected;
        public final long awake;
        public final long registered;
        public final long users;

        Infos(long connected, long awake, long registered, long users) {
            this.connected = connected;
            this.awake = awake;
            this.registered = registered;
            this.users = users;
        }
    }


    private class AwakeSupplier implements Supplier<Long>{
        @Override
        public Long get() {
            long counter = 0;
            for(Status status: connectionManager.values()){
                if(!status.isAsleep()) counter++;
            }
            return counter;
        }
    }

    private class Nab{
        public final String name;
        public final String macFiltered;
        public final String ipFiltered;
        public final boolean aSleep;

        private Nab(String name, String macFiltered, String ipFiltered, boolean aSleep) {
            this.name = name;
            this.macFiltered = macFiltered;
            this.ipFiltered = ipFiltered;
            this.aSleep = aSleep;
        }
    }

    private class DetailSupplier implements Supplier<List<Nab>>{
        @Override
        public List<Nab> get() {
            List<Nab> nabList = new ArrayList<Nab>();
            for(Status status: connectionManager.values()){
                InetSocketAddress remoteAddress = (InetSocketAddress) status.getContext().getChannel().getRemoteAddress();
                String ip = remoteAddress.getAddress().getHostAddress();
                String ipFiltered = ip.replaceAll("\\.\\d+$", ".1");
                String macFiltered = "";
                if(status.getUsername() != null)
                    macFiltered = "000000"+status.getUsername().substring(6);
                nabList.add(new Nab(macFiltered, macFiltered, ipFiltered, status.isAsleep()));
            }
            return nabList;
        }
    }
}
