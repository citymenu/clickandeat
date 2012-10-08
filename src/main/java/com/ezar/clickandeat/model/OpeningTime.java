package com.ezar.clickandeat.model;

import org.joda.time.LocalTime;

public class OpeningTime {
    
    private int dayOfWeek;

    private boolean open;

    private LocalTime earlyOpeningTime;
    
    private LocalTime earlyClosingTime;

    private LocalTime lateOpeningTime;

    private LocalTime lateClosingTime;

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public LocalTime getEarlyOpeningTime() {
        return earlyOpeningTime;
    }

    public void setEarlyOpeningTime(LocalTime earlyOpeningTime) {
        this.earlyOpeningTime = earlyOpeningTime;
    }

    public LocalTime getEarlyClosingTime() {
        return earlyClosingTime;
    }

    public void setEarlyClosingTime(LocalTime earlyClosingTime) {
        this.earlyClosingTime = earlyClosingTime;
    }

    public LocalTime getLateOpeningTime() {
        return lateOpeningTime;
    }

    public void setLateOpeningTime(LocalTime lateOpeningTime) {
        this.lateOpeningTime = lateOpeningTime;
    }

    public LocalTime getLateClosingTime() {
        return lateClosingTime;
    }

    public void setLateClosingTime(LocalTime lateClosingTime) {
        this.lateClosingTime = lateClosingTime;
    }
}
