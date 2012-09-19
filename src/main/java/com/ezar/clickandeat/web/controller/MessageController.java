package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import org.joda.time.DateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class MessageController {
    
    private static final String script;
    
    static {
        StringBuilder sb = new StringBuilder("var labels = {};\n");
        for(Map.Entry<String,String> entry: MessageFactory.getMessages()) {
            sb.append("labels[\"").append(entry.getKey()).append("\"] = \"").append(entry.getValue()).append("\";\n");
        }
        sb.append("function getLabel(key){ return (labels[key]? labels[key]: 'missing: ' + key); }");
        script = sb.toString();
    }

    // Used for cache control, cache until server is restarted
    private final DateTime started;


    public MessageController() {
        started = new DateTime();
    }
    

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/script/messages.html", method = RequestMethod.GET )
    public ResponseEntity<byte[]> getMessages(HttpServletRequest request) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setLastModified(started.getMillis());
        return new ResponseEntity<byte[]>(script.getBytes("utf-8"), headers, HttpStatus.OK);
    }

    
    
    
}
