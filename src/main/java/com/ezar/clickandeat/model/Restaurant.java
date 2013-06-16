package com.ezar.clickandeat.model;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.util.DateUtil;
import com.ezar.clickandeat.util.LocationUtils;
import com.ezar.clickandeat.util.NumberUtil;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
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

    private static final Double DEFAULT_COMMISSION_PERCENT = 10d;

    public static final String CONTENT_STATUS_APPROVED = "approved";
    public static final String CONTENT_STATUS_REJECTED = "rejected";
    public static final String CONTENT_STATUS_SENT_FOR_APPROVAL = "sent to be approved";
    public static final String CONTENT_STATUS_NO_ACTION = "";
    
    @Indexed(unique=true)
    private String restaurantId;

    private String uuid;

    @Indexed
    private String name;

    private String description;
    
    private Boolean listOnSite;

    private Boolean phoneOrdersOnly;

    private Boolean recommended;

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
    
    private boolean hasUploadedImage;

    // Admin
    private Long created;
    private Long lastUpdated;
    private Long lastContentApprovalStatusUpdated;
    private List<RestaurantUpdate> restaurantUpdates;
    private Boolean testMode;
    private int searchRanking; // Indicates where it should appear in the search results
    private Double commissionPercent = DEFAULT_COMMISSION_PERCENT;

    // The last time the restaurant responded in any way to an order
    private DateTime lastOrderReponseTime;
    
    @Transient
    private Double distanceToSearchLocation;

    @Transient
    private boolean open;

    // External id (i.e. url to Just eat)
    private String externalId;
    private Integer justEatRating;
    
    // Determines if the restaurant owner has approved or not the restaurant content
    private boolean contentApproved;

    // String format for the status of the data of the restaurant
    private String contentStatus;

    // Specify the reasons why the content has been rejected
    private String rejectionReasons;

    // Indicates if the restaurant is deleted
    @Indexed
    private boolean deleted;


    public Restaurant() {
        this.uuid = UUID.randomUUID().toString();
        this.address = new Address();
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
        this.testMode = false;
        this.phoneOrdersOnly = false;
        this.contentApproved = false;
        this.contentStatus = CONTENT_STATUS_NO_ACTION;
        this.recommended = false;
        this.restaurantUpdates = new ArrayList<RestaurantUpdate>();
    }


    /**
     * Truncates page title if over 70 characters
     * @return
     */

    public String getPageTitle() {
        StringBuilder sb = new StringBuilder();
        if( name != null ) {
            sb.append(name);
        }
        if( address != null && address.getTown() != null ) { 
            sb.append(" | ").append(MessageFactory.formatMessage("page.takeaway",false,address.getTown()));
        }
        sb.append(" | Llamarycomer.com");
        String title = sb.toString();
        if( title.length() > 70 ) {
            title = name + " | " + MessageFactory.formatMessage("page.order-online",false) + " | Llamarycomer.com";
            if( title.length() > 70 ) {
                return name + " | Llamarycomer.com";
            }
            else {
                return title;
            }
        }
        else {
            return title;
        }
    }
    

    /**
     * Builds a url link to open this restaurant
     */
    
    public String getUrl() {
        StringBuilder sb = new StringBuilder("/app/");
        sb.append(MessageFactory.getMessage("url.find-takeaway",false));
        if( address != null && address.getTown() != null )
            sb.append("-").append(address.getTown().trim());
        if( name != null )
            sb.append("/").append(name.replace(" ","-"));
        if( restaurantId != null )
            sb.append("/restaurant/").append(restaurantId);
        return sb.toString().toLowerCase(MessageFactory.getLocale());
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

        boolean isBankHoliday = DateUtil.isBankHoliday(now.toLocalDate());
        OpeningTime bankHolidayOpeningTime = openingTimes.getBankHolidayOpeningTimes();

        // Iterate through open dates
        for( OpeningTime openingTime: openingTimes.getOpeningTimes() ) {

            // Check if this date is a bank holiday
            int dayOfWeek = openingTime.getDayOfWeek();

            if( isBankHoliday ) {
                openingTime = bankHolidayOpeningTime;
            }

            // If the openingTime is not open at all, continue
            if( !openingTime.isOpen()) {
                continue;
            }

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
                            try {
                                times[1] = now.toLocalDate().plusDays(1).toDateTime(earlyClose);
                            }
                            catch( IllegalFieldValueException ex ) {
                                times[1] = now.toLocalDate().plusDays(1).toDateTime(earlyClose.minusHours(1));
                            }
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
                            try {
                                times[3] = now.toLocalDate().plusDays(1).toDateTime(lateClose);
                            }
                            catch( IllegalFieldValueException ex ) {
                                times[3] = now.toLocalDate().plusDays(1).toDateTime(lateClose.minusHours(1));
                            }
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


    /**
     * @param specialOfferTitle
     * @return
     */

    public SpecialOffer getSpecialOfferByTitle(String specialOfferTitle) {
        for( SpecialOffer specialOffer: specialOffers ) {
            if( specialOffer.getTitle().equals(specialOfferTitle)) {
                return specialOffer;
            }
        }
        return null;
    }

    /**
     * @param order
     * @return
     */
    
    public boolean willDeliverToLocation(Order order) {
        Address deliveryAddress = order.getDeliveryAddress();
        if( deliveryAddress == null || deliveryAddress.getLocation() == null ) {
            return false;
        }
        if( deliveryOptions.isCollectionOnly()) {
            return false;
        }
        String postCode = deliveryAddress.getPostCode();
        if( StringUtils.hasText(postCode)) {
            String postCodeMatcher = postCode.toUpperCase().replace(" ","");
            for( String deliverToPostCode: deliveryOptions.getAreasDeliveredTo()) {
                String postCodeCandidate = deliverToPostCode.toUpperCase().replace(" ","");
                if( postCodeMatcher.equals(postCodeCandidate)) {
                    return true;
                }
            }
        }
        if( deliveryOptions.getDeliveryRadiusInKilometres() != null ) {
            double distance = LocationUtils.getDistance(address.getLocation(), deliveryAddress.getLocation());
            double locationRadius = deliveryAddress.getRadius();
            if( distance - locationRadius <= deliveryOptions.getDeliveryRadiusInKilometres()) {
                return true;
            }
        }
        return false;
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

    public String getMetaDescription() {
        return MessageFactory.formatMessage("restaurant.metadescription", false, StringUtils.collectionToDelimitedString(cuisines, ", "), name , address.getTown() == null? address.getPostCode(): address.getTown());
    }
    
    public String getImageAlt() {
        return MessageFactory.formatMessage("restaurant.image-alt", false, name , address.getTown() == null? address.getPostCode(): address.getTown());
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

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
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

    public String getLongitude() {
        if( this.address == null || this.address.getLocation() == null ) {
            return "";
        }
        else {
            return this.address.getLocation()[1] + "";
        }
    }

    public String getLatitude() {
        if( this.address == null || this.address.getLocation() == null ) {
            return "";
        }
        else {
            return this.address.getLocation()[0] + "";
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
    
    public Discount getFirstDiscount() {
        return discounts.size() > 0? discounts.get(0): null;
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
        return hasUploadedImage? restaurantId: "default-logo.jpg";
    }

    public boolean isHasUploadedImage() {
        return hasUploadedImage;
    }

    public void setHasUploadedImage(boolean hasUploadedImage) {
        this.hasUploadedImage = hasUploadedImage;
    }

    public Double getDistanceToSearchLocation() {
        return distanceToSearchLocation;
    }
    
    public String getFormattedDistanceToSearchLocation() {
        return NumberUtil.format(distanceToSearchLocation);
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

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isContentApproved() {
        return contentApproved;
    }

    public void setContentApproved(boolean contentApproved) {
        this.contentApproved = contentApproved;
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    public String getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(String rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    public Long getLastContentApprovalStatusUpdated() {
        return lastContentApprovalStatusUpdated;
    }

    public void setLastContentApprovalStatusUpdated(Long lastContentApprovalStatusUpdated) {
        this.lastContentApprovalStatusUpdated = lastContentApprovalStatusUpdated;
    }

    public List<RestaurantUpdate> getRestaurantUpdates() {
        return restaurantUpdates;
    }

    public void setRestaurantUpdates(List<RestaurantUpdate> restaurantUpdates) {
        this.restaurantUpdates = restaurantUpdates;
    }

    public Boolean getTestMode() {
        return testMode;
    }

    public void setTestMode(Boolean testMode) {
        this.testMode = testMode;
    }

    public int getSearchRanking() {
        return searchRanking;
    }

    public void setSearchRanking(int searchRanking) {
        this.searchRanking = searchRanking;
    }

    public boolean getHasMenuItemIcon() {
        if( menu == null ) {
            return false;
        }
        for( MenuCategory category: menu.getMenuCategories()) {
            if( StringUtils.hasText(category.getIconClass())) {
                return true;
            }
            for( MenuItem item: category.getMenuItems()) {
                if( StringUtils.hasText(item.getIconClass())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    public boolean getHasSpicyItemIcon() {
        return getHasIconClass("spicy");
    }

    public boolean getHasVegetarianItemIcon() {
        return getHasIconClass("vegetarian");
    }

    public boolean getHasContainsNutsItemIcon() {
        return getHasIconClass("contains-nuts");
    }

    public boolean getHasGlutenFreeItemIcon() {
        return getHasIconClass("gluten-free");
    }


    /**
     * @param iconClass
     * @return
     */
    
    public boolean getHasIconClass(String iconClass) {
        if( menu == null ) {
            return false;
        }
        for( MenuCategory category: menu.getMenuCategories()) {
            if( category.getIconClass() != null && category.getIconClass().startsWith(iconClass)) {
                return true;
            }
            for( MenuItem item: category.getMenuItems()) {
                if( item.getIconClass() != null && item.getIconClass().startsWith(iconClass)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @param text
     */
    public void addRestaurantUpdate(String text) {
        RestaurantUpdate restaurantUpdate = new RestaurantUpdate();
        restaurantUpdate.setText(text);
        restaurantUpdate.setUpdateTime(new DateTime());
        restaurantUpdates.add(restaurantUpdate);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Double getCommissionPercent() {
        return commissionPercent == null? DEFAULT_COMMISSION_PERCENT: commissionPercent;
    }

    public void setCommissionPercent(Double commissionPercent) {
        this.commissionPercent = commissionPercent;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getJustEatRating() {
        return justEatRating;
    }

    public void setJustEatRating(Integer justEatRating) {
        this.justEatRating = justEatRating;
    }

    public String getOrigin() {
        return externalId == null? "Local": "JustEat";
    }

    public String getTown() {
        return address == null || address.getTown() == null? null: address.getTown();
    }

    public String getPostcode() {
        return address == null || address.getPostCode() == null? null: address.getPostCode();
    }
}
