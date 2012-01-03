import com.nabalive.application.core.ApplicationBase;
import com.nabalive.data.core.model.ApplicationConfig;
import com.nabalive.data.core.model.Nabaztag;
import com.nabalive.common.server.MessageService;
import com.google.common.base.Suppliers
import com.google.common.base.Supplier
import java.util.concurrent.TimeUnit
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import groovy.json.JsonSlurper

import org.springframework.beans.factory.annotation.Autowired;


public class LotoApplication extends ApplicationBase{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    
    @Autowired
    private MessageService messageService;
    
    public Object getIncidents(){
        def data = new URL("http://www.incidents-transports.com/api/incidents.json/day").getText()
        def slurper = new JsonSlurper()
        return slurper.parseText(data)
    }
    
    def incidents = Suppliers.memoizeWithExpiration(new Supplier<Object>(){
        Object get(){
            return getIncidents();
        }
    }, 10, TimeUnit.MINUTES)
    
    String textIncident(incident){
        return incident.line.replace("RER", "R E R., ") + ". " + incident.reason;
    }
    
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {
        def line = applicationConfig.getParameters().get("ligne").get(0)
        
        
        def data = "";
        incidents.get().each() {
            if(it.line == line || line == "all"){
                    data += textIncident(it)+". ";
            }
        }
            
        StringBuilder command = new StringBuilder();
        
        
        String text = URLEncoder.encode(data, "UTF-8");
        
        command.append("ST http://www.nabaztag.com/tts/fr?text="+text+"\nMW\n");
       
        messageService.sendMessage(nabaztag.getMacAddress(), command.toString());
    }

    public String getApikey() {
        return "FDFA0603-0CC6-45CB-A34B-CDDC69DB5C85"
    }
}