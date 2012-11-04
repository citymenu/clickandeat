package com.ezar.clickandeat.web.controller.helper;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    
    private String field;
    
    private String type;

    private List<String> comparisons = new ArrayList<String>();

    private List<String> values = new ArrayList<String>();

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

    public List<String> getComparisons() {
        return comparisons;
    }

    public void setComparisons(List<String> comparisons) {
        this.comparisons = comparisons;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
