package com.ezar.clickandeat.web.tags;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class StringEscapingTag extends SimpleTagSupport {
    
    private String value;
    
    private boolean escapeComments;

    private boolean escapeNewLines;

    public void doTag() throws JspException, IOException {
        if( escapeComments && StringUtils.hasText(value)) {
            value = value.replace("'","###");
        }
        if( escapeNewLines && StringUtils.hasText(value)) {
            value = value.replace("\n","<br>");
        }
        getJspContext().getOut().write(value);
    }

    public void setValue(String value) {
        this.value = StringEscapeUtils.escapeHtml(value);
    }

    public void setEscapeComments(boolean escapeComments) {
        this.escapeComments = escapeComments;
    }

    public void setEscapeNewLines(boolean escapeNewLines) {
        this.escapeNewLines = escapeNewLines;
    }
}
