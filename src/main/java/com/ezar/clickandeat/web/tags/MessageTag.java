package com.ezar.clickandeat.web.tags;

import com.ezar.clickandeat.config.MessageFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class MessageTag extends SimpleTagSupport {
    
    private String key;

    private boolean escape = true;

    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().getOut().write(MessageFactory.getMessage(key,escape));
    }
    
    public void setKey(String key) {
        this.key = key;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}
