package com.ezar.clickandeat.model;

import org.joda.time.DateTime;

public class OrderUpdate {
    
    private String text;
    
    private DateTime updateTime;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }
}
