package com.ezar.clickandeat.model;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class OpeningTimes {
    
    private String openingTimesSummary;
    
    private List<OpeningTime> openingTimes;
    
    private List<LocalDate> closedDates;

    public OpeningTimes() {
        this.openingTimes = new ArrayList<OpeningTime>();
        this.closedDates = new ArrayList<LocalDate>();
    }

    @Override
    public String toString() {
        return "OpeningTimes{" +
                "openingTimes=" + openingTimes +
                ", closedDates=" + closedDates +
                '}';
    }

    public String getOpeningTimesSummary() {
        return openingTimesSummary;
    }

    public void setOpeningTimesSummary(String openingTimesSummary) {
        this.openingTimesSummary = openingTimesSummary;
    }

    public List<OpeningTime> getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(List<OpeningTime> openingTimes) {
        this.openingTimes = openingTimes;
    }

    public List<LocalDate> getClosedDates() {
        return closedDates;
    }

    public void setClosedDates(List<LocalDate> closedDates) {
        this.closedDates = closedDates;
    }
}
