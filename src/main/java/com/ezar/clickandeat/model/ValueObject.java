package com.ezar.clickandeat.model;

public class ValueObject {
    
    private String id;
    
    private int value;

    public ValueObject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
