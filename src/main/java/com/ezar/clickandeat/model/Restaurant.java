package com.ezar.clickandeat.model;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Document(collection="restaurants")
public class Restaurant extends PersistentObject {

    private static final JSONSerializer SERIALIZER = new JSONSerializer();

    private static final JSONDeserializer<Restaurant> DESERIALIZER = new JSONDeserializer<Restaurant>();

    @Indexed(unique=true)
    private String restaurantId;

    private String name;

    private String description;
    
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

    public Restaurant() {
        this.cuisines = new ArrayList<String>();
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
                    boolean[] isOpen = new boolean[2];
                    isOpen[0] = !time.isBefore(openingTime.getCollectionOpeningTime()) && !time.isAfter(openingTime.getCollectionClosingTime());
                    isOpen[1] = !time.isBefore(openingTime.getDeliveryOpeningTime()) && !time.isAfter(openingTime.getDeliveryClosingTime());
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
     * @param restaurant
     * @return
     */

    public static String toJSON(Restaurant restaurant) {
        return SERIALIZER.deepSerialize(restaurant);
    }

    /**
     * @param json
     * @return
     */

    public static Restaurant fromJSON(String json) {
        return DESERIALIZER.deserialize(json);
    }
    
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
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
    
}
