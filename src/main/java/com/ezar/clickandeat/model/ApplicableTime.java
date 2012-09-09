package com.ezar.clickandeat.model;

import org.joda.time.LocalTime;

public class ApplicableTime {
    
    private int dayOfWeek;

    private boolean applicable;

    private LocalTime applicableFrom;
    
    private LocalTime applicableTo;

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean getApplicable() {
        return applicable;
    }

    public void setApplicable(boolean applicable) {
        this.applicable = applicable;
    }

    public LocalTime getApplicableFrom() {
        return applicableFrom;
    }

    public void setApplicableFrom(LocalTime applicableFrom) {
        this.applicableFrom = applicableFrom;
    }

    public LocalTime getApplicableTo() {
        return applicableTo;
    }

    public void setApplicableTo(LocalTime applicableTo) {
        this.applicableTo = applicableTo;
    }
}
