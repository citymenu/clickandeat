package com.ezar.clickandeat.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestaurantTest {

    private Restaurant restaurant;

    @Before
    public void setup() {
        restaurant = new Restaurant();
        OpeningTimes openingTimes = new OpeningTimes();
        restaurant.setOpeningTimes(openingTimes);
        
        OpeningTime sunday = new OpeningTime();
        sunday.setDayOfWeek(7);
        sunday.setDeliveryOpeningTime(new LocalTime(18,0));
        sunday.setDeliveryClosingTime(new LocalTime(2,0));
        openingTimes.getOpeningTimes().add(sunday);
        
        OpeningTime monday = new OpeningTime();
        monday.setDayOfWeek(1);
        monday.setDeliveryOpeningTime(new LocalTime(18,0));
        monday.setDeliveryClosingTime(new LocalTime(23,30));
        openingTimes.getOpeningTimes().add(monday);
        
        openingTimes.getClosedDates().add(new LocalDate(2012,12,25));
    }


    @Test
    public void testDeliveryAferMidnight() throws Exception {

        // Monday
        LocalDate monday = new LocalDate(2012,6,4);

        Assert.assertTrue("Restaurant should be open for delivery at 1am", restaurant.isOpenForDelivery(monday, new LocalTime(1,0)));
        Assert.assertTrue("Restaurant should be closed for delivery at 2:30am", !restaurant.isOpenForDelivery(monday, new LocalTime(2,30)));
        Assert.assertTrue("Restaurant should be open for delivery at 8pm", restaurant.isOpenForDelivery(monday, new LocalTime(20,0)));

        Assert.assertTrue("Restaurant should be closed for delivery on 25th December 2012", !restaurant.isOpenForDelivery(new LocalDate(2012,12,25),new LocalTime()));
    }
    

}
