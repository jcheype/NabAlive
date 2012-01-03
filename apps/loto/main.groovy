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

import org.springframework.beans.factory.annotation.Autowired;


public class LotoApplication extends ApplicationBase{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    
    @Autowired
    private MessageService messageService;
    
    
    public String getLotoPage(){
        def tagsoupParser = new org.ccil.cowan.tagsoup.Parser()
        def slurper = new XmlSlurper(tagsoupParser)
        def htmlParser = slurper.parse("http://mobi.fdj.fr/loto.php?d=h")
        def message =  new StringBuilder(htmlParser.'**'.findAll{ it.@id == 'tirage-date'}[0].text())
        def boules = htmlParser.'**'.findAll{ it.@class == 'boule'}
        message.append( ", le " + boules[0])
        message.append( ", le " + boules[1])
        message.append( ", le " + boules[2])
        message.append( ", le " + boules[3])
        message.append( ", le " + boules[4])
        message.append(", et le numéro chance, " + boules[5])
        return message.toString()
    }
    
    def lotoResult = Suppliers.memoizeWithExpiration(new Supplier<String>(){
        String get(){
            return getLotoPage();
        }
    }, 10, TimeUnit.MINUTES)
    
    public void onStartup(Nabaztag nabaztag, ApplicationConfig applicationConfig) throws Exception {
        StringBuilder command = new StringBuilder();
        String text = URLEncoder.encode(lotoResult.get(), "UTF-8");
        
        command.append("ST http://www.nabaztag.com/tts/"+nabaztag.getApikey()+"/fr?text="+text+"\nMW\n");
        
        logger.debug("LOTO APP")
        logger.debug("messageService {}", messageService)
        logger.debug("nabaztag {}", nabaztag)
        logger.debug("command {}", command)
        messageService.sendMessage(nabaztag.getMacAddress(), command.toString());
    }

    public String getApikey() {
        return "E0BF5D09-16E7-422D-864B-87FF51C34500"
    }
}