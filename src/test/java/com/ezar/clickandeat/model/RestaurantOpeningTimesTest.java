package com.ezar.clickandeat.model;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.joda.time.*;
import org.junit.Before;
import org.junit.Test;

public class RestaurantOpeningTimesTest {
    
    private static final Logger LOGGER = Logger.getLogger(RestaurantOpeningTimesTest.class);
    
    private Restaurant restaurant;


    @Before
    public void setup() throws Exception {

        restaurant = new Restaurant();
        
        OpeningTimes openingTimes = new OpeningTimes();
        openingTimes.addClosedDate(new LocalDate(2012,7,3));

        OpeningTime openingTime = new OpeningTime();
        openingTime.setDayOfWeek(DateTimeConstants.TUESDAY);
        openingTime.setOpen(true);
        openingTime.setEarlyOpeningTime(new LocalTime(10,0));
        openingTime.setEarlyClosingTime(new LocalTime(13, 0));
        openingTime.setLateOpeningTime(new LocalTime(18,0));
        openingTime.setLateClosingTime(new LocalTime(23, 0));
        openingTimes.addOpeningTime(openingTime);

        OpeningTime bankHolidayOpeningTime = new OpeningTime();
        bankHolidayOpeningTime.setOpen(true);
        bankHolidayOpeningTime.setEarlyOpeningTime(new LocalTime(11,0));
        bankHolidayOpeningTime.setEarlyClosingTime(new LocalTime(14, 0));
        bankHolidayOpeningTime.setLateOpeningTime(new LocalTime(17,0));
        bankHolidayOpeningTime.setLateClosingTime(new LocalTime(22, 0));
        openingTimes.setBankHolidayOpeningTimes(bankHolidayOpeningTime);
        
        restaurant.setOpeningTimes(openingTimes);
        
    }
    

    @Test
    public void testOpeningTimesOnATuesday() throws Exception {
        MutableDateTime tuesday = new MutableDateTime();
        tuesday.setDayOfWeek(DateTimeConstants.TUESDAY);
        DateTime[] openingAndClosingTimes = restaurant.getOpeningAndClosingTimes(tuesday.toDateTime());
        Assert.assertEquals(new LocalTime(10,0),openingAndClosingTimes[0].toLocalTime());
        Assert.assertEquals(new LocalTime(13,0),openingAndClosingTimes[1].toLocalTime());
        Assert.assertEquals(new LocalTime(18,0),openingAndClosingTimes[2].toLocalTime());
        Assert.assertEquals(new LocalTime(23,0),openingAndClosingTimes[3].toLocalTime());
    }


    @Test
    public void testOpeningTimesOnABankHoliday() throws Exception {
        DateTime dt = new DateTime(2012,12,25,0,0,0,0);
        DateTime[] openingAndClosingTimes = restaurant.getOpeningAndClosingTimes(dt);
        Assert.assertEquals(new LocalTime(11,0),openingAndClosingTimes[0].toLocalTime());
        Assert.assertEquals(new LocalTime(14,0),openingAndClosingTimes[1].toLocalTime());
        Assert.assertEquals(new LocalTime(17,0),openingAndClosingTimes[2].toLocalTime());
        Assert.assertEquals(new LocalTime(22,0),openingAndClosingTimes[3].toLocalTime());
    }


    @Test
    public void testOpeningTimesOnAClosedDate() throws Exception {
        DateTime dt = new DateTime(2012,7,3,0,0,0,0);
        DateTime[] openingAndClosingTimes = restaurant.getOpeningAndClosingTimes(dt);
        Assert.assertNull(openingAndClosingTimes[0]);
        Assert.assertNull(openingAndClosingTimes[1]);
        Assert.assertNull(openingAndClosingTimes[2]);
        Assert.assertNull(openingAndClosingTimes[3]);
    }

}
