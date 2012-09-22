package com.ezar.clickandeat.maps;

import com.ezar.clickandeat.model.AddressLocation;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class LocationServiceTest {
    
    private static final Logger LOGGER = Logger.getLogger(LocationServiceTest.class);

    @Autowired
    private LocationService locationService;

    @Before
    public void setup() throws Exception {
        locationService.setLocale("ca_ES");
    }
    
    @Test
    public void testLocateValidAddress() throws Exception {
        String address = "Calle de Bailén, 56 08009";
        List<AddressLocation> locations = locationService.getLocations(address);
        Assert.assertTrue("Should have found one location only", locations.size() == 1 );
        AddressLocation location = locations.get(0);
        LOGGER.info("Resolved location: " + location );
    }
    
    
    @Test
    public void testAmbiguousAddressWithOneResult() throws Exception {
        String address = "Calle Bailén 56 08009";
        List<AddressLocation> locations = locationService.getLocations(address);
        Assert.assertTrue("Should have found one location", locations.size() == 1 );
        AddressLocation location = locations.get(0);
        LOGGER.info("Resolved location: " + location );
    }


    @Test
    public void testAmbiguousAddressWithMultipleResults() throws Exception {
        String address = "Calle Bailén 56";
        List<AddressLocation> locations = locationService.getLocations(address);
        Assert.assertEquals("Should have found three locations", 3, locations.size());
        for(AddressLocation location: locations ) {
            LOGGER.info("Resolved location: " + location );
        }
    }


    @Test
    public void testExactAddressWithMultipleResults() throws Exception {
        String address = "Placa Joaquim Pena 4 08017";
        List<AddressLocation> locations = locationService.getLocations(address);
        Assert.assertEquals("Should have found three locations", 3, locations.size());
        for(AddressLocation location: locations ) {
            LOGGER.info("Resolved location: " + location );
        }
    }




}

