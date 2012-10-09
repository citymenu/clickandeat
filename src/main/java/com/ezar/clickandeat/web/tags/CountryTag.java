package com.ezar.clickandeat.web.tags;

import com.ezar.clickandeat.config.MessageFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class CountryTag extends SimpleTagSupport {
    
    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().getOut().write(MessageFactory.getLocaleString().split("_")[1]);
    }
    
}
