package com.ezar.clickandeat.maps;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class PlacesServiceTest {

    private static final Logger LOGGER = Logger.getLogger(PlacesServiceTest.class);
    
    @Autowired
    private PlacesService placesService;

    @Before
    public void setup() throws Exception {
        placesService.setLocale("es_ES");
    }

    @Test
    public void testPlaceLookup() throws Exception {
        String lookup = "0";
        List<String> addresses = placesService.getAddresses(lookup);
        for( String address: addresses ) {
            LOGGER.info(address);
        }
    }

    @Test
    public void testStreetLookup() throws Exception {
        String lookup = "Calle del Planeta, 37";
        List<String> addresses = placesService.getAddresses(lookup);
        for( String address: addresses ) {
            LOGGER.info(address);
        }
    }

}
