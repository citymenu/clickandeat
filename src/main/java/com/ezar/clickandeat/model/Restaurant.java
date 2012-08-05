package com.ezar.clickandeat.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.data.annotation.Transient;
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

    @Transient
    private Double distanceToSearchLocation;

    @Transient
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
     * Returns true if this restaurant is currently open based on the given date and time
     * @param now
     * @return
     */
    
    public RestaurantOpenStatus isOpen(DateTime now) {
        
        DateTime[] dateTimes = getOpeningAndClosingTimes(now);
        
        DateTime collectionOpeningTime = dateTimes[0];
        DateTime collectionClosingTime = dateTimes[1];
        DateTime deliveryOpeningTime = dateTimes[2];
        DateTime deliveryClosingTime = dateTimes[3];

        if( collectionOpeningTime == null && collectionClosingTime == null && deliveryOpeningTime == null && deliveryClosingTime == null ) {
            return RestaurantOpenStatus.CLOSED;
        }

        boolean openForCollection = false;
        boolean openForDelivery = false;
        
        // Check collection times status for T+1 closing first
        if( collectionOpeningTime != null && collectionClosingTime != null ) {
            if( collectionClosingTime.isBefore(collectionOpeningTime)) {
                if( !now.isAfter(collectionClosingTime)) {
                    openForCollection = true;
                }
            }
            else if( !now.isBefore(collectionOpeningTime) && !now.isAfter(collectionClosingTime)) {
                openForCollection = true;
            }
        }

        // Check delivery times status for T+1 closing first
        if( deliveryOpeningTime != null && deliveryClosingTime != null ) {
            if( deliveryClosingTime.isBefore(deliveryOpeningTime)) {
                if( !now.isAfter(deliveryClosingTime)) {
                    openForDelivery = true;
                }
            }
            else if( !now.isBefore(deliveryOpeningTime) && !now.isAfter(deliveryClosingTime)) {
                openForDelivery = true;
            }
        }

        // Return status based on opening times
        if( openForCollection && openForDelivery ) {
            return RestaurantOpenStatus.OPEN_FOR_COLLECTION_AND_DELIVERY;
        }
        else if( openForDelivery ) {
            return RestaurantOpenStatus.OPEN_FOR_DELIVERY_ONLY;
        }
        else if( openForCollection ) {
            return RestaurantOpenStatus.OPEN_FOR_COLLECTION_ONLY;
        }
        else {
            return RestaurantOpenStatus.CLOSED;
        }
    }


    /**
     * @param now
     * @return
     */

    public boolean isOpenForDelivery(DateTime now) {
        RestaurantOpenStatus status = isOpen(now);
        return RestaurantOpenStatus.OPEN_FOR_COLLECTION_AND_DELIVERY.equals(status) || RestaurantOpenStatus.OPEN_FOR_DELIVERY_ONLY.equals(status);
    }


    /**
     * @param now
     * @return
     */

    public boolean isOpenForCollection(DateTime now) {
        RestaurantOpenStatus status = isOpen(now);
        return RestaurantOpenStatus.OPEN_FOR_COLLECTION_AND_DELIVERY.equals(status) || RestaurantOpenStatus.OPEN_FOR_COLLECTION_ONLY.equals(status);
    }


    /**
     * @param now
     * @return
     */

    public DateTime getCollectionOpeningTime(DateTime now ) {
        return getOpeningAndClosingTimes(now)[0];
    }


    /**
     * @param now
     * @return
     */

    public DateTime getCollectionClosingTime(DateTime now) {
        return getOpeningAndClosingTimes(now)[1];
    }


    /**
     * @param now
     * @return
     */

    public DateTime getDeliveryOpeningTime(DateTime now ) {
        return getOpeningAndClosingTimes(now)[2];
    }


    /**
     * @param now
     * @return
     */

    public DateTime getDeliveryClosingTime(DateTime now ) {
        return getOpeningAndClosingTimes(now)[3];
    }


    /**
     * @param now
     * @return
     */

    private DateTime[] getOpeningAndClosingTimes(DateTime now) {

        Assert.notNull(now, "now must not be null");

        DateTime[] times = new DateTime[4];

        if( openingTimes == null ) {
            return times;
        }

        // Check if the restaurant is closed on this date
        if( openingTimes.getClosedDates() != null ) {
            for( LocalDate closedDate: openingTimes.getClosedDates()) {
                if( closedDate.equals(now.toLocalDate())) {
                    return times;
                }
            }
        }

        int currentDayOfWeek = now.getDayOfWeek();

        // Iterate through open dates
        for( OpeningTime openingTime: openingTimes.getOpeningTimes() ) {

            int dayOfWeek = openingTime.getDayOfWeek();
            LocalTime collectionOpen = openingTime.getCollectionOpeningTime();
            LocalTime collectionClose = openingTime.getCollectionClosingTime();
            LocalTime deliveryOpen = openingTime.getDeliveryOpeningTime();
            LocalTime deliveryClose = openingTime.getDeliveryClosingTime();

            // Test collection times if collection open and close are not null
            if( collectionOpen != null && collectionClose != null ) {

                // Check from day before for closing times after midnight
                if( currentDayOfWeek - 1 == dayOfWeek % 7 && collectionClose.isBefore(collectionOpen)) {
                    if(!now.toLocalTime().isAfter(collectionClose)) {
                        times[0] = now.toLocalDate().minusDays(1).toDateTime(collectionOpen); // Collection open the day before
                        times[1] = now.toLocalDate().toDateTime(collectionClose);
                    }
                }
                else if( currentDayOfWeek == dayOfWeek ) {
                    // Don't override previously set collection times
                    if( times[0] == null && times[1] == null ) {
                        times[0] = now.toLocalDate().toDateTime(collectionOpen);
                        times[1] = now.toLocalDate().toDateTime(collectionClose);
                    }
                }
            }

            // Test delivery times if delivery open and close are not null
            if( deliveryOpen != null && deliveryClose != null ) {

                // Check from day before for closing times after midnight
                if( currentDayOfWeek - 1 == dayOfWeek % 7 && deliveryClose.isBefore(deliveryOpen)) {
                    if(!now.toLocalTime().isAfter(deliveryClose)) {
                        times[2] = now.toLocalDate().minusDays(1).toDateTime(deliveryOpen); // Delivery open the day before
                        times[3] = now.toLocalDate().toDateTime(deliveryClose);
                    }
                }
                else if( currentDayOfWeek == dayOfWeek ) {
                    // Don't override previously set delivery times
                    if( times[2] == null && times[3] == null ) {
                        times[2] = now.toLocalDate().toDateTime(deliveryOpen);
                        times[3] = now.toLocalDate().toDateTime(deliveryClose);
                    }
                }
            }
        }

        return times;
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
