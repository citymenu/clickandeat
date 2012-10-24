package com.ezar.clickandeat.maps;

import com.ezar.clickandeat.model.GeoLocation;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class GeoLocationServiceTest {
    
    private static final Logger LOGGER = Logger.getLogger(GeoLocationServiceTest.class);

    @Autowired
    private GeoLocationService geoLocationService;

    @Before
    public void setup() throws Exception {
        geoLocationService.setLocale("es_ES");
    }


    @Test
    public void testLocateValidAddress() throws Exception {
        String address = "Barcelona";
        GeoLocation location = geoLocationService.getLocation(address);
        LOGGER.info("Resolved location: " + location );
    }
    
    
    @Test
    public void testGetDistance() throws Exception {
        GeoLocation location1 = geoLocationService.getLocation("Woodford Green, London");
        GeoLocation location2 = geoLocationService.getLocation("E18 2LG");
        Double distance = geoLocationService.getDistance(location1.getLocation(), location2.getLocation());
        LOGGER.info("Distance: " + distance);
    }


    
}

