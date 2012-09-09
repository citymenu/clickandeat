package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class SpecialOffer {

    String specialOfferId;
    
    String title;

    String description;

    private Double cost;

    List<SpecialOfferItem> specialOfferItems;

    private List<ApplicableTime> offerApplicableTimes;

    public SpecialOffer() {
        this.specialOfferItems = new ArrayList<SpecialOfferItem>();
        this.offerApplicableTimes = new ArrayList<ApplicableTime>();
    }

    public String getSpecialOfferId() {
        return specialOfferId;
    }

    public void setSpecialOfferId(String specialOfferId) {
        this.specialOfferId = specialOfferId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public List<SpecialOfferItem> getSpecialOfferItems() {
        return specialOfferItems;
    }

    public void setSpecialOfferItems(List<SpecialOfferItem> specialOfferItems) {
        this.specialOfferItems = specialOfferItems;
    }

    public List<ApplicableTime> getOfferApplicableTimes() {
        return offerApplicableTimes;
    }

    public void setOfferApplicableTimes(List<ApplicableTime> offerApplicableTimes) {
        this.offerApplicableTimes = offerApplicableTimes;
    }
}


