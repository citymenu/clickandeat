package com.ezar.clickandeat.workflow;

import com.ezar.clickandeat.model.OpeningTime;
import com.ezar.clickandeat.model.OpeningTimes;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.model.RestaurantOpenStatus;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OpeningTimesTest {
    
    private static final Logger LOGGER = Logger.getLogger(OpeningTimesTest.class);
    
    private Restaurant restaurant;
    
    @Before
    public void setup() throws Exception {

        // Build a restaurant
        restaurant = new Restaurant();
        restaurant.setRestaurantId("test");
        OpeningTimes openingTimes = new OpeningTimes();
        restaurant.setOpeningTimes(openingTimes);
        
        // Set up opening times for collection and delivery for Monday to Friday
        for( int i = 1; i < 6; i++ ) {
            OpeningTime openingTime = new OpeningTime();
            openingTime.setOpen(true);
            openingTime.setDayOfWeek(i);
            openingTime.setCollectionOpeningTime(new LocalTime(12,0));
            openingTime.setCollectionClosingTime(new LocalTime(20, 0));
            openingTime.setDeliveryOpeningTime(new LocalTime(18, 0));
            openingTime.setDeliveryClosingTime(new LocalTime(23, 0));
            openingTimes.addOpeningTime(openingTime);
        }
        
        // Set up opening times for saturday, set delivery closing as 2am (t+1)
        OpeningTime saturday = new OpeningTime();
        saturday.setOpen(true);
        saturday.setDayOfWeek(DateTimeConstants.SATURDAY);
        saturday.setCollectionOpeningTime(new LocalTime(11,0));
        saturday.setCollectionClosingTime(new LocalTime(20, 0));
        saturday.setDeliveryOpeningTime(new LocalTime(17,0));
        saturday.setDeliveryClosingTime(new LocalTime(2, 0));
        openingTimes.addOpeningTime(saturday);

        // Set up opening times for saturday, set no collections and delivery closing as midnight (t+1)
        OpeningTime sunday = new OpeningTime();
        sunday.setOpen(true);
        sunday.setDayOfWeek(DateTimeConstants.SUNDAY);
        sunday.setDeliveryOpeningTime(new LocalTime(17,0));
        sunday.setDeliveryClosingTime(new LocalTime(0, 0));
        openingTimes.addOpeningTime(sunday);

        // Set the restaurant as closed on Christmas day 2012
        openingTimes.addClosedDate(new LocalDate(2012,12,25));
        
    }


    @Test
    public void testRestaurantOpeningTimes() throws Exception {

        // Confirm that restaurant is closed on Christmas day 2012
        Assert.assertEquals("Status should be closed", RestaurantOpenStatus.CLOSED,
                restaurant.isOpen(new DateTime(2012,12,25,13,0)));

        // Confirm that restaurant is closed at 10:00 on a Monday
        Assert.assertEquals("Status should be closed", RestaurantOpenStatus.CLOSED,
                restaurant.isOpen(new DateTime(2012,8,6,10,0)));

        // Confirm that restaurant is open for collection only at 13:00 on a Monday
        Assert.assertEquals("Status should be open for collection only", RestaurantOpenStatus.OPEN_FOR_COLLECTION_ONLY,
                restaurant.isOpen(new DateTime(2012,8,6,13,0)));
        
        // Confirm that restaurant is open for collection and delivery at 19:00 on a Monday
        Assert.assertEquals("Status should be open for collection and delivery", RestaurantOpenStatus.OPEN_FOR_COLLECTION_AND_DELIVERY,
                restaurant.isOpen(new DateTime(2012,8,6,19,0)));

        // Confirm that restaurant is open for delivery only at 22:00 on a Monday
        Assert.assertEquals("Status should be open for delivery only", RestaurantOpenStatus.OPEN_FOR_DELIVERY_ONLY,
                restaurant.isOpen(new DateTime(2012,8,6,22,0)));

        // Confirm that restaurant is open for delivery only at at midnight on a Monday
        Assert.assertEquals("Status should be open for delivery only", RestaurantOpenStatus.OPEN_FOR_DELIVERY_ONLY,
                restaurant.isOpen(new DateTime(2012,8,6,0,0)));

        // Confirm that restaurant is closed at 1am on a Monday
        Assert.assertEquals("Status should be open closed", RestaurantOpenStatus.CLOSED,
                restaurant.isOpen(new DateTime(2012,8,6,1,0)));

        // Confirm that restaurant is open for delivery only at at 1:00 on a Sunday
        Assert.assertEquals("Status should be open for delivery only", RestaurantOpenStatus.OPEN_FOR_DELIVERY_ONLY,
                restaurant.isOpen(new DateTime(2012,8,5,1,0)));
    }

}
