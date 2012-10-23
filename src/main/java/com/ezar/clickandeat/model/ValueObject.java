package com.ezar.clickandeat.model;

import java.util.Map;

public class ValueObject {
    
    private String id;
    
    private Map<String,Object> value;

    public ValueObject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public void setValue(Map<String, Object> value) {
        this.value = value;
    }
}
