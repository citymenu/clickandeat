package com.ezar.clickandeat.web.tags;

import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class StringEscapingTag extends SimpleTagSupport {
    
    private String value;

    public void doTag() throws JspException, IOException {
        getJspContext().getOut().write(StringEscapeUtils.escapeHtml(value));
    }

    public void setValue(String value) {
        this.value = value;
    }

}
