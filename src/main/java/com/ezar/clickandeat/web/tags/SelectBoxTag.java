package com.ezar.clickandeat.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class SelectBoxTag extends SimpleTagSupport {
    
    private String id;
    
    private int size = 5;

    public void doTag() throws JspException, IOException {
        StringBuilder sb = new StringBuilder("<select id='" + id + "'>");
        for( int i = 1; i <= size; i++ ) {
            sb.append("<option value='" + i + "'>" + i + "</option>");
        }
        sb.append("</select>");
        getJspContext().getOut().write(sb.toString());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
