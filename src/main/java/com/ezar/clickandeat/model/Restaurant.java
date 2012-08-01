package com.ezar.clickandeat.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection="restaurants")
public class Restaurant extends PersistentObject {

    @Indexed(unique=true)
    private String restaurantId;

    private String uuid;
    
    private String name;

    private String description;
    
    private Boolean listOnSite;
    
    private String contactEmail;

    private String contactTelephone;

    private String contactMobile;

    private String website;

    private List<String> cuisines;
    
    private Person mainContact;

    private Address address;

    private Menu menu;

    private DeliveryOptions deliveryOptions;

    private NotificationOptions notificationOptions;

    private OpeningTimes openingTimes;

    private String imageId;

    // Transient properties
    private Double distanceToSearchLocation;

    private boolean openForDelivery;
    
    public Restaurant() {
        this.uuid = UUID.randomUUID().toString();
        this.openingTimes = new OpeningTimes();
        this.deliveryOptions = new DeliveryOptions();
        this.mainContact = new Person();
        this.notificationOptions = new NotificationOptions();
        this.menu = new Menu();
        this.cuisines = new ArrayList<String>();
        this.listOnSite = true;
    }


    /**
     * @param date
     * @param time
     * @return
     */

    public boolean isOpenForDelivery(LocalDate date, LocalTime time) {
        Assert.notNull(date, "date must not be null");
        Assert.notNull(time, "time must not be null");

        if( openingTimes == null ) {
            return false;
        }

        // Check if the restaurant is closed on this date
        if( openingTimes.getClosedDates() != null ) {
            for( LocalDate closedDate: openingTimes.getClosedDates()) {
                if( closedDate.equals(date)) {
                    return false;
                }
            }
        }

        int currentDayOfWeek = date.getDayOfWeek();

        // Iterate through open dates
        for( OpeningTime openingTime: openingTimes.getOpeningTimes() ) {

            int dayOfWeek = openingTime.getDayOfWeek();
            LocalTime open = openingTime.getDeliveryOpeningTime();
            LocalTime close = openingTime.getDeliveryClosingTime();

            // Do not test if either open or close time is null
            if( open == null || close == null ) {
                continue;
            }

            // Check from day before for delivery closing times after midnight
            if( currentDayOfWeek - 1 == dayOfWeek % 7 && close.isBefore(open)) {
                if(!close.isBefore(time)) {
                    return true;
                }
            }
            else if( currentDayOfWeek == dayOfWeek ) {
                if(!time.isBefore(open) && !time.isAfter(close)) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * @param date
     * @param time
     * @return
     */

    public boolean isOpenForCollection(LocalDate date, LocalTime time) {
        Assert.notNull(date, "date must not be null");
        Assert.notNull(time, "time must not be null");

        if( openingTimes == null ) {
            return false;
        }

        // Check if the restaurant is closed on this date
        if( openingTimes.getClosedDates() != null ) {
            for( LocalDate closedDate: openingTimes.getClosedDates()) {
                if( closedDate.equals(date)) {
                    return false;
                }
            }
        }

        int currentDayOfWeek = date.getDayOfWeek();

        // Iterate through open dates
        for( OpeningTime openingTime: openingTimes.getOpeningTimes() ) {

            int dayOfWeek = openingTime.getDayOfWeek();
            LocalTime open = openingTime.getCollectionOpeningTime();
            LocalTime close = openingTime.getCollectionClosingTime();

            // Do not test if either open or close time is null
            if( open == null || close == null ) {
                continue;
            }

            // Check from day before for delivery closing times after midnight
            if( currentDayOfWeek - 1 == dayOfWeek % 7 && close.isBefore(open)) {
                if(!close.isBefore(time)) {
                    return true;
                }
            }
            else if( currentDayOfWeek == dayOfWeek ) {
                if(!time.isBefore(open) && !time.isAfter(close)) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Returns true if this restaurant is currently open based on the given date and time
     * @param date
     * @param time
     * @return
     */
    
    public RestaurantOpenStatus isOpen(LocalDate date, LocalTime time) {
        
        Assert.notNull(date, "date must not be null");
        Assert.notNull(time, "time must not be null");

        if( openingTimes == null ) {
            return RestaurantOpenStatus.CLOSED;
        }

        // Check if the restaurant is closed on this date
        if( openingTimes.getClosedDates() != null ) {
            for( LocalDate closedDate: openingTimes.getClosedDates()) {
                if( closedDate.equals(date)) {
                    return RestaurantOpenStatus.CLOSED;
                }
            }
        }

        // Check if the restaurant is open for collection or delivery at this time today
        if( openingTimes.getOpeningTimes() != null ) {
            for( OpeningTime openingTime: openingTimes.getOpeningTimes() ) {
                if( openingTime.getDayOfWeek() == date.getDayOfWeek()) {
                    if( !openingTime.isOpen()) {
                        return RestaurantOpenStatus.CLOSED;
                    }
                    boolean[] isOpen = new boolean[2];
                    
                    if( openingTime.getCollectionOpeningTime() == null || openingTime.getCollectionOpeningTime() == null ) {
                        isOpen[0] = false;
                    } 
                    else { 
                        isOpen[0] = !time.isBefore(openingTime.getCollectionOpeningTime()) && !time.isAfter(openingTime.getCollectionClosingTime());
                    }
                    
                    if( openingTime.getDeliveryOpeningTime() == null || openingTime.getDeliveryClosingTime() == null ) {
                        isOpen[1] = false;
                    }
                    else {
                        isOpen[1] = !time.isBefore(openingTime.getDeliveryOpeningTime()) && !time.isAfter(openingTime.getDeliveryClosingTime());
                    }

                    if( isOpen[0] && isOpen[1]) {
                        return RestaurantOpenStatus.FULLY_OPEN;
                    }
                    else if( isOpen[0]) {
                        return RestaurantOpenStatus.OPEN_FOR_COLLECTION;
                    }
                    else {
                        return RestaurantOpenStatus.CLOSED;
                    }
                }
            }
        }

        return RestaurantOpenStatus.CLOSED;
    }


    /**
     * @param date
     * @return
     */

    public OpeningTime getOpeningTime(LocalDate date) {
        OpeningTime ret = null;
        for( OpeningTime openingTime: openingTimes.getOpeningTimes()) {
            if( openingTime.getDayOfWeek() == date.getDayOfWeek()) {
                ret = openingTime;
                break;
            }
        }
        return ret;
    }
    

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getListOnSite() {
        return listOnSite;
    }

    public void setListOnSite(Boolean listOnSite) {
        this.listOnSite = listOnSite;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<String> cuisines) {
        this.cuisines = cuisines;
    }

    public Person getMainContact() {
        return mainContact;
    }

    public void setMainContact(Person mainContact) {
        this.mainContact = mainContact;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public DeliveryOptions getDeliveryOptions() {
        return deliveryOptions;
    }

    public void setDeliveryOptions(DeliveryOptions deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    public OpeningTimes getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(OpeningTimes openingTimes) {
        this.openingTimes = openingTimes;
    }

    public NotificationOptions getNotificationOptions() {
        return notificationOptions;
    }

    public void setNotificationOptions(NotificationOptions notificationOptions) {
        this.notificationOptions = notificationOptions;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Double getDistanceToSearchLocation() {
        return distanceToSearchLocation;
    }

    public void setDistanceToSearchLocation(Double distanceToSearchLocation) {
        this.distanceToSearchLocation = distanceToSearchLocation;
    }

    public boolean isOpenForDelivery() {
        return openForDelivery;
    }

    public void setOpenForDelivery(boolean openForDelivery) {
        this.openForDelivery = openForDelivery;
    }
}
