package com.ezar.clickandeat.model;

import com.ezar.clickandeat.config.MessageFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection="restaurants")
public class Restaurant extends PersistentObject {

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");

    @Indexed(unique=true)
    private String restaurantId;

    private String uuid;
    
    private String name;

    private String description;
    
    private Boolean listOnSite;

    private Boolean phoneOrdersOnly;
    
    private String contactEmail;

    private String contactTelephone;

    private String contactMobile;

    private String website;

    @Indexed
    private List<String> cuisines;
    
    private Person mainContact;

    private Address address;

    private Menu menu;

    private DeliveryOptions deliveryOptions;

    private NotificationOptions notificationOptions;

    private OpeningTimes openingTimes;

    private List<Discount> discounts;
    
    private List<SpecialOffer> specialOffers;
    
    private String imageName;

    // The last time the restaurant responded in any way to an order
    private DateTime lastOrderReponseTime;
    
    @Transient
    private Double distanceToSearchLocation;

    @Transient
    private boolean open;
    
    public Restaurant() {
        this.uuid = UUID.randomUUID().toString();
        this.openingTimes = new OpeningTimes();
        this.deliveryOptions = new DeliveryOptions();
        this.mainContact = new Person();
        this.notificationOptions = new NotificationOptions();
        this.notificationOptions.setReceiveNotificationCall(true);
        this.menu = new Menu();
        this.cuisines = new ArrayList<String>();
        this.discounts = new ArrayList<Discount>();
        this.specialOffers = new ArrayList<SpecialOffer>();
        this.listOnSite = true;
        this.phoneOrdersOnly = false;
    }


    /**
     * Builds a url link to open this restaurant
     */
    
    public String getUrl() {

        StringBuilder sb = new StringBuilder("app/");
        sb.append(MessageFactory.getMessage("url.find-takeaway",false));
        if( address != null || address.getTown() != null )
            sb.append("-").append(address.getTown());
        if( address != null || address.getTown() != null )
            sb.append("-").append(address.getPostCode());
        if( cuisines != null )
            sb.append("-").append(StringUtils.collectionToDelimitedString(cuisines,"-"));
        if( name != null )
            sb.append("-").append(name.replace(" ","-"));
        if( restaurantId != null )
            sb.append("/restaurant/").append(restaurantId);
        return sb.toString();
    }
    
    
    /**
     * Returns true if this restaurant is currently open based on the given date and time
     * @param now
     * @return
     */
    
    public boolean isOpen(DateTime now) {
        
        DateTime[] dateTimes = getOpeningAndClosingTimes(now);
        
        DateTime earlyOpeningTime = dateTimes[0];
        DateTime earlyClosingTime = dateTimes[1];
        DateTime lateOpeningTime = dateTimes[2];
        DateTime lateClosingTime = dateTimes[3];

        if( earlyOpeningTime == null && earlyClosingTime == null && lateOpeningTime == null && lateClosingTime == null ) {
            return false;
        }
        
        if( earlyOpeningTime != null && earlyClosingTime != null ) {
            if( !now.isBefore(earlyOpeningTime) && !now.isAfter(earlyClosingTime)) {
                return true;
            }
        }

        if( lateOpeningTime != null && lateClosingTime != null ) {
            if( !now.isBefore(lateOpeningTime) && !now.isAfter(lateClosingTime)) {
                return true;
            }
        }

        return false;
    }


    public DateTime getEarlyOpeningTime(DateTime now) {
        DateTime[] times = getOpeningAndClosingTimes(now);
        DateTime earlyOpeningTime = times[0];
        DateTime lateOpeningTime = times[2];
        return earlyOpeningTime == null? lateOpeningTime: earlyOpeningTime;
    }
    
    
    /**
     * @param now
     * @return
     */

    public DateTime getOpeningTime(DateTime now) {
        DateTime[] times = getOpeningAndClosingTimes(now);
        DateTime earlyOpeningTime = times[0];
        DateTime earlyClosingTime = times[1];
        DateTime lateOpeningTime = times[2];
        DateTime lateClosingTime = times[3];

        if( earlyOpeningTime == null || earlyClosingTime == null ) {
            if( lateOpeningTime != null && lateClosingTime != null ) {
                return lateOpeningTime;
            }
            else {
                return null;
            }
        }
        else {
            if( lateOpeningTime != null && lateClosingTime != null ) {
                return now.isAfter(earlyClosingTime)? lateOpeningTime: earlyOpeningTime;
            }
            else {
                return earlyOpeningTime;
            }
        }
    }


    /**
     * @param now
     * @return
     */

    public DateTime getClosingTime(DateTime now) {
        DateTime[] times = getOpeningAndClosingTimes(now);
        DateTime earlyOpeningTime = times[0];
        DateTime earlyClosingTime = times[1];
        DateTime lateOpeningTime = times[2];
        DateTime lateClosingTime = times[3];

        if( earlyOpeningTime == null || earlyClosingTime == null ) {
            if( lateOpeningTime != null && lateClosingTime != null ) {
                return lateClosingTime;
            }
            else {
                return null;
            }
        }
        else {
            if( lateOpeningTime != null && lateClosingTime != null ) {
                return now.isAfter(earlyClosingTime)? lateClosingTime: earlyClosingTime;
            }
            else {
                return earlyOpeningTime;
            }
        }
    }


    /**
     * @param now
     * @return
     */

    public DateTime[] getOpeningAndClosingTimes(DateTime now) {

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

            // If the openingTime is not open at all, continue
            if( !openingTime.isOpen()) {
                continue;
            }
            
            int dayOfWeek = openingTime.getDayOfWeek();
            LocalTime earlyOpen = openingTime.getEarlyOpeningTime();
            LocalTime earlyClose = openingTime.getEarlyClosingTime();
            LocalTime lateOpen = openingTime.getLateOpeningTime();
            LocalTime lateClose = openingTime.getLateClosingTime();

            // Test collection times if collection open and close are not null
            if( earlyOpen != null && earlyClose != null ) {

                // Check from day before for closing times after midnight
                if( currentDayOfWeek - 1 == dayOfWeek % 7 && earlyClose.isBefore(earlyOpen)) {
                    if(!now.toLocalTime().isAfter(earlyClose)) {
                        times[0] = now.toLocalDate().minusDays(1).toDateTime(earlyOpen); // Collection open the day before
                        times[1] = now.toLocalDate().toDateTime(earlyClose);
                    }
                }
                else if( currentDayOfWeek == dayOfWeek ) {
                    // Don't override previously set collection times
                    if( times[0] == null && times[1] == null ) {
                        times[0] = now.toLocalDate().toDateTime(earlyOpen);
                        if( earlyClose.isBefore(earlyOpen)) {
                            times[1] = now.toLocalDate().plusDays(1).toDateTime(earlyClose);
                        }
                        else {
                            times[1] = now.toLocalDate().toDateTime(earlyClose);
                        }
                    }
                }
            }

            // Test delivery times if delivery open and close are not null
            if( lateOpen != null && lateClose != null ) {

                // Check from day before for closing times after midnight
                if( currentDayOfWeek - 1 == dayOfWeek % 7 && lateClose.isBefore(lateOpen)) {
                    if(!now.toLocalTime().isAfter(lateClose)) {
                        times[2] = now.toLocalDate().minusDays(1).toDateTime(lateOpen); // Delivery open the day before
                        times[3] = now.toLocalDate().toDateTime(lateClose);
                    }
                }
                else if( currentDayOfWeek == dayOfWeek ) {
                    // Don't override previously set delivery times
                    if( times[2] == null && times[3] == null ) {
                        times[2] = now.toLocalDate().toDateTime(lateOpen);
                        if( lateClose.isBefore(lateOpen)) {
                            times[3] = now.toLocalDate().plusDays(1).toDateTime(lateClose);
                        }
                        else {
                            times[3] = now.toLocalDate().toDateTime(lateClose);
                        }
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

    
    public String getTodaysOpeningTimes() {
        DateTime[] openingAndClosingTimes = getOpeningAndClosingTimes(new DateTime());
        DateTime earlyOpeningTime = openingAndClosingTimes[0];
        DateTime earlyClosingTime = openingAndClosingTimes[1];
        DateTime lateOpeningTime = openingAndClosingTimes[2];
        DateTime lateClosingTime = openingAndClosingTimes[3];

        boolean hasEarlyTimes = (earlyOpeningTime != null && earlyClosingTime != null);
        boolean hasLateTimes = (lateOpeningTime != null && lateClosingTime != null);

        if( !hasEarlyTimes && !hasLateTimes ) {
            return MessageFactory.getMessage("restaurant.closed",false);
        }
        
        if( hasEarlyTimes && !hasLateTimes ) {
            return earlyOpeningTime.toString(formatter) + "-" + earlyClosingTime.toString(formatter);
        }
        else if( !hasEarlyTimes ) {
            return lateOpeningTime.toString(formatter) + "-" + lateClosingTime.toString(formatter);
        }
        else {
            return earlyOpeningTime.toString(formatter) + "-" + earlyClosingTime.toString(formatter) + " | " + lateOpeningTime.toString(formatter) + "-" + lateClosingTime.toString(formatter);
        }
    }
    

    /**
     * @param menuItemId
     * @return
     */

    public MenuItem getMenuItem(String menuItemId) {
        for( MenuCategory menuCategory: menu.getMenuCategories()) {
            for( MenuItem menuItem: menuCategory.getMenuItems()) {
                if( menuItemId.equals(menuItem.getItemId())) {
                    return menuItem;
                }
            }
        }
        return null;
    }

    
    public int getSpecialOfferCount() {
        return specialOffers.size();
    }
    
    /**
     * @param specialOfferId
     * @return
     */

    public SpecialOffer getSpecialOffer(String specialOfferId) {
        for( SpecialOffer specialOffer: specialOffers ) {
            if( specialOfferId.equals(specialOffer.getSpecialOfferId())) {
                return specialOffer;
            }
        }
        return null;
    }
    
    public int getDeliveryTimeMinutes() {
        return deliveryOptions.getDeliveryTimeMinutes();
    }

    public int getCollectionTimeMinutes() {
        return deliveryOptions.getCollectionTimeMinutes();
    }
    
    public boolean getCollectionOnly() {
        return deliveryOptions.isCollectionOnly();
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

    public Boolean getPhoneOrdersOnly() {
        return phoneOrdersOnly;
    }

    public void setPhoneOrdersOnly(Boolean phoneOrdersOnly) {
        this.phoneOrdersOnly = phoneOrdersOnly;
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

    public String getCuisineSummary() {
        return StringUtils.collectionToDelimitedString(cuisines, ", ");
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
    
    public String getCoordinates() {
        if( this.address == null || this.address.getLocation() == null ) {
            return "";
        }
        else {
            return this.address.getLocation()[1] + "," + this.address.getLocation()[0];
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public DateTime getLastOrderReponseTime() {
        return lastOrderReponseTime;
    }

    public void setLastOrderReponseTime(DateTime lastOrderReponseTime) {
        this.lastOrderReponseTime = lastOrderReponseTime;
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

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }
    
    public boolean getHasDiscounts() {
        return discounts.size() > 0;
    }

    public List<SpecialOffer> getSpecialOffers() {
        return specialOffers;
    }

    public void setSpecialOffers(List<SpecialOffer> specialOffers) {
        this.specialOffers = specialOffers;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Double getDistanceToSearchLocation() {
        return distanceToSearchLocation;
    }

    public void setDistanceToSearchLocation(Double distanceToSearchLocation) {
        this.distanceToSearchLocation = distanceToSearchLocation;
    }

    public boolean getOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
