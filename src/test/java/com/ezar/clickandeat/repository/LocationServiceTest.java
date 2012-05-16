package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.LocationService;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class LocationServiceTest {
    
    private static final Logger LOGGER = Logger.getLogger(LocationServiceTest.class);

    @Autowired
    private LocationService locationService;
    
    @Test
    public void testDistanceBetweenPostCodes() throws Exception {
        
        String postCode1 = "E18 2LG";
        String postCode2 = "PE3 6LJ";
        
        double[] location1 = locationService.getLocation(postCode1);
        double[] location2 = locationService.getLocation(postCode2);
        
        double distance = locationService.getDistance(location1,location2);
        
        LOGGER.info("Distance between points is " + distance + " kilometres");

    }

}
