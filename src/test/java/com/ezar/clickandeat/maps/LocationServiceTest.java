package com.ezar.clickandeat.maps;

import com.ezar.clickandeat.model.AddressLocation;
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
        String postCode3 = "E18";
        String postCode4 = "South Woodford";
        String postCode5 = "Peel Road South Woodford";

        AddressLocation address1 = locationService.getSingleLocation(postCode1);
        AddressLocation address2 = locationService.getSingleLocation(postCode2);
        AddressLocation address3 = locationService.getSingleLocation(postCode3);
        AddressLocation address4 = locationService.getSingleLocation(postCode4);
        AddressLocation address5 = locationService.getSingleLocation(postCode5);

        LOGGER.info(postCode1 + " radius: " + address1.getRadius());
        LOGGER.info(postCode2 + " radius: " + address2.getRadius());
        LOGGER.info(postCode3 + " radius: " + address3.getRadius());
        LOGGER.info(postCode4 + " radius: " + address4.getRadius());
        LOGGER.info(postCode5 + " radius: " + address5.getRadius());

        double[] location1 = address1.getLocation();
        double[] location2 = address2.getLocation();

        double distance = locationService.getDistance(location1,location2);
        
        LOGGER.info("Distance between points is " + distance + " kilometres");

    }

}
