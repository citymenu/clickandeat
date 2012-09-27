package com.ezar.clickandeat.web.tags;

import com.ezar.clickandeat.config.MessageFactory;
import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class MessageTag extends SimpleTagSupport {
    
    private String key;

    private boolean escape = true;
    
    private String format;

    @Override
    public void doTag() throws JspException, IOException {
        if( StringUtils.hasText(format)) {
            getJspContext().getOut().write(MessageFactory.formatMessage(key,escape,format));
        }
        else {
            getJspContext().getOut().write(MessageFactory.getMessage(key,escape));
        }
    }
    
    public void setKey(String key) {
        this.key = key;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
