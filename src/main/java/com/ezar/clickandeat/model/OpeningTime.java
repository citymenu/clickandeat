package com.ezar.clickandeat.model;

import org.joda.time.LocalTime;

public class OpeningTime {
    
    private int dayOfWeek;
    
    private LocalTime openingTime;
    
    private LocalTime closingTime;

    @Override
    public String toString() {
        return "OpeningTime{" +
                "dayOfWeek=" + dayOfWeek +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                '}';
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
}
