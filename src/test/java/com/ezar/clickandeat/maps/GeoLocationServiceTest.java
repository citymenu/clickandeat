package com.ezar.clickandeat.maps;

import com.ezar.clickandeat.model.GeoLocation;
import com.ezar.clickandeat.util.LocationUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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


    @Test
    public void testLocateValidAddress() throws Exception {
        geoLocationService.setLocale("es_ES");
        geoLocationService.setCacheLocations(true);
        String address = "Barcelona";
        GeoLocation location = geoLocationService.getLocation(address);
        LOGGER.info("Resolved location: " + location );
    }
    
    
    @Test
    @Ignore
    public void testGetDistance() throws Exception {
        geoLocationService.setLocale("en_UK");
        geoLocationService.setCacheLocations(false);
        GeoLocation location1 = geoLocationService.getLocation("Woodford Green, London");
        GeoLocation location2 = geoLocationService.getLocation("E18 2LG");
        Double distance = LocationUtils.getDistance(location1.getLocation(), location2.getLocation());
        LOGGER.info("Distance: " + distance);
    }


    @Test
    @Ignore
    public void testRequestsToHitQueryLimit() throws Exception {
        int requestCount = 100;
        geoLocationService.setLocale("en_UK");
        geoLocationService.setCacheLocations(false);
        String address = "80 Peel Road E18 2LG";
        for( int i = 0; i < requestCount; i++ ) {
            LOGGER.info("Executed geolocation search: " + requestCount);
            GeoLocation location = geoLocationService.getLocation(address);
            Assert.assertNotNull(location);
        }

    }

    
}

