package com.ezar.clickandeat.scheduling;

import com.ezar.clickandeat.model.Restaurant;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class RestaurantScraperTest {

    private static final Logger LOGGER = Logger.getLogger(RestaurantScraperTest.class);

    @Autowired
    private RestaurantScraper restaurantScraper;

    @Test
    public void testScrapeData() throws Exception {
        restaurantScraper.scrapeData();
    }


}
