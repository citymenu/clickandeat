package com.ezar.clickandeat.model;

import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.NumberUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SpecialOffer {

    private static JSONUtils jsonUtils = new JSONUtils();
    
    String specialOfferId;

    private int number;

    String title;

    String description;

    private Double cost;

    List<SpecialOfferItem> specialOfferItems;

    private List<ApplicableTime> offerApplicableTimes;

    public SpecialOffer() {
        this.specialOfferItems = new ArrayList<SpecialOfferItem>();
        this.offerApplicableTimes = new ArrayList<ApplicableTime>();
    }


    /**
     * @param dateTime
     * @return
     */

    public boolean isAvailableAt(DateTime dateTime) {

        ApplicableTime applicableTime = getOfferApplicableTime(dateTime);
        if( applicableTime == null || !applicableTime.getApplicable()) {
            return false;
        }
        if( applicableTime.getApplicableFrom() == null || applicableTime.getApplicableTo() == null ) {
            return true;
        }
        LocalTime time = dateTime.toLocalTime();
        return !time.isBefore(applicableTime.getApplicableFrom()) && !time.isAfter(applicableTime.getApplicableTo());
    }


    /**
     * @param order
     * @return
     */

    public boolean isApplicableTo(Order order) {

        if( Order.DELIVERY.equals(order.getDeliveryType())) {
            DateTime expectedDeliveryTime = order.getExpectedDeliveryTime() == null? new DateTime(): order.getExpectedDeliveryTime();
            ApplicableTime applicableTime = getOfferApplicableTime(expectedDeliveryTime);
            if( applicableTime == null || !applicableTime.getApplicable()) {
                return false;
            }
            if( applicableTime.getApplicableFrom() == null || applicableTime.getApplicableTo() == null ) {
                return true;
            }
            LocalTime time = expectedDeliveryTime.toLocalTime();
            return !time.isBefore(applicableTime.getApplicableFrom()) && !time.isAfter(applicableTime.getApplicableTo());
        }
        else {
            DateTime expectedCollectionTime = order.getExpectedCollectionTime() == null? new DateTime(): order.getExpectedCollectionTime();
            ApplicableTime applicableTime = getOfferApplicableTime(expectedCollectionTime);
            if( applicableTime == null || !applicableTime.getApplicable()) {
                return false;
            }
            if( applicableTime.getApplicableFrom() == null || applicableTime.getApplicableTo() == null ) {
                return true;
            }
            LocalTime time = expectedCollectionTime.toLocalTime();
            return !time.isBefore(applicableTime.getApplicableFrom()) && !time.isAfter(applicableTime.getApplicableTo());
        }
    }


    /**
     * @param dateTime
     * @return
     */

    private ApplicableTime getOfferApplicableTime(DateTime dateTime ) {
        int dayOfWeek = dateTime.getDayOfWeek();
        for( ApplicableTime time: offerApplicableTimes ) {
            if( dayOfWeek == time.getDayOfWeek()) {
                return time;
            }
        }
        return null;
    }

    public String getSpecialOfferItemsArray() {
        StringBuilder sb = new StringBuilder("[");
        String delim = "";
        for( SpecialOfferItem item: specialOfferItems ) {
            sb.append(delim);
            sb.append("'");
            sb.append(StringEscapeUtils.escapeHtml(item.getNullSafeTitle().replace("'", "###"))).append("%%%");
            sb.append(StringEscapeUtils.escapeHtml(item.getNullSafeDescription()).replace("'", "###").replace("\n","<br>")).append("%%%");
            sb.append(StringEscapeUtils.escapeHtml(StringUtils.collectionToDelimitedString(item.getSpecialOfferItemChoices(),"$$$")).replace("'","###"));
            sb.append("'");
            delim = ",";
        }
        sb.append("]");
        return sb.toString();
    }

    public String getSpecialOfferId() {
        return specialOfferId;
    }

    public void setSpecialOfferId(String specialOfferId) {
        this.specialOfferId = specialOfferId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public String getFormattedCost() {
        return NumberUtil.format(cost);
    }

}


