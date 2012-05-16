package com.ezar.clickandeat.model;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Document(collection="restaurants")
public class Restaurant extends BaseObject {

    @Indexed(unique=true)
    private String restaurantId;

    private String name;

    private String email;

    private String telephone;

    private String website;

    private List<String> cuisines = new ArrayList<String>();
    
    private Person mainContact;

    private Address address;

    private Menu menu;

    private DeliveryOptions deliveryOptions;
    
    private OpeningTimes openingTimes;

    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantId='" + restaurantId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", website='" + website + '\'' +
                ", cuisines='" + cuisines + '\'' +
                ", mainContact=" + mainContact +
                ", address=" + address +
                ", menu=" + menu +
                ", deliveryOptions=" + deliveryOptions +
                ", openingTimes=" + openingTimes +
                '}';
    }


    /**
     * Returns true if this restaurant is currently open based on the given date and time
     * @param date
     * @param time
     * @return
     */
    
    public boolean isOpen(LocalDate date, LocalTime time) {
        
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

        // Check if the restaurant is open at this time today
        if( openingTimes.getOpeningTimes() != null ) {
            for( OpeningTime openingTime: openingTimes.getOpeningTimes() ) {
                if( openingTime.getDayOfWeek() == date.getDayOfWeek()) {
                    return !time.isBefore(openingTime.getOpeningTime()) &&
                            !time.isAfter(openingTime.getClosingTime());
                }
            }
        }

        return false;
    }
    
    
    public Restaurant() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
}
