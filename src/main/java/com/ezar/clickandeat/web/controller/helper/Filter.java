package com.ezar.clickandeat.web.controller.helper;

/**
 * Created by IntelliJ IDEA.
 * User: Joe Pugh
 * Date: 03/11/12
 * Time: 23:37
 * To change this template use File | Settings | File Templates.
 */
public class Filter {
    
    private String field;
    
    private String type;

    private String comparison;

    private String[] values;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComparison() {
        return comparison;
    }

    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
