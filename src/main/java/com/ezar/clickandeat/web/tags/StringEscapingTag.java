package com.ezar.clickandeat.web.tags;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class StringEscapingTag extends SimpleTagSupport {
    
    private String value;
    
    private boolean escape;

    public void doTag() throws JspException, IOException {
        if( escape && StringUtils.hasText(value)) {
            value = value.replace("'","###");
        }
        getJspContext().getOut().write(StringEscapeUtils.escapeHtml(value));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

}
