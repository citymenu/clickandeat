package com.ezar.clickandeat.maps;

import com.ezar.clickandeat.model.Address;
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
        locationService.setLocale("es_ES");
    }


    @Test
    public void testLocateValidAddress() throws Exception {
        String address = "Calle de Bailén, 56 08009";
        AddressLocation location = locationService.getLocation(address);
        LOGGER.info("Resolved location: " + location );
    }
    
    


    
}

