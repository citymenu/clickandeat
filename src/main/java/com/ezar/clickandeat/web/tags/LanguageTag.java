package com.ezar.clickandeat.web.tags;

import com.ezar.clickandeat.config.MessageFactory;
import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class LanguageTag extends SimpleTagSupport {
    
    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().getOut().write(MessageFactory.getLocale().split("_")[0]);
    }
    
}
