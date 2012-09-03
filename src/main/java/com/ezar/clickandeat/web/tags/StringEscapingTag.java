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

    private boolean escapeSpaces;
    
    public void doTag() throws JspException, IOException {
        boolean hasText = StringUtils.hasText(value);
        if( escapeComments && hasText) {
            value = value.replace("'","###");
        }
        if( escapeNewLines && hasText) {
            value = value.replace("\n","<br>");
        }
        if( escapeSpaces && hasText) {
            value = value.replace(" ","_");
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

    public void setEscapeSpaces(boolean escapeSpaces) {
        this.escapeSpaces = escapeSpaces;
    }
}
