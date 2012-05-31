package com.ezar.clickandeat.model;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class OpeningTimes {
    
    private String summary;
    
    private List<OpeningTime> openingTimes = new ArrayList<OpeningTime>();
    
    private List<LocalDate> closedDates = new ArrayList<LocalDate>();

    @Override
    public String toString() {
        return "OpeningTimes{" +
                "openingTimes=" + openingTimes +
                ", closedDates=" + closedDates +
                '}';
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
