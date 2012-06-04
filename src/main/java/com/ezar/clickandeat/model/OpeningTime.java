package com.ezar.clickandeat.model;

import org.joda.time.LocalTime;

public class OpeningTime {
    
    private int dayOfWeek;

    private boolean open;

    private LocalTime collectionOpeningTime;
    
    private LocalTime collectionClosingTime;

    private LocalTime deliveryOpeningTime;

    private LocalTime deliveryClosingTime;

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

    public LocalTime getCollectionOpeningTime() {
        return collectionOpeningTime;
    }

    public void setCollectionOpeningTime(LocalTime collectionOpeningTime) {
        this.collectionOpeningTime = collectionOpeningTime;
    }

    public LocalTime getCollectionClosingTime() {
        return collectionClosingTime;
    }

    public void setCollectionClosingTime(LocalTime collectionClosingTime) {
        this.collectionClosingTime = collectionClosingTime;
    }

    public LocalTime getDeliveryOpeningTime() {
        return deliveryOpeningTime;
    }

    public void setDeliveryOpeningTime(LocalTime deliveryOpeningTime) {
        this.deliveryOpeningTime = deliveryOpeningTime;
    }

    public LocalTime getDeliveryClosingTime() {
        return deliveryClosingTime;
    }

    public void setDeliveryClosingTime(LocalTime deliveryClosingTime) {
        this.deliveryClosingTime = deliveryClosingTime;
    }
}
