package com.ezar.clickandeat.scheduling;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class TelephoneNumberScraperTest {

    private static final Logger LOGGER = Logger.getLogger(TelephoneNumberScraperTest.class);

    @Autowired
    private TelephoneNumberScraper telephoneNumberScraper;

    @Test
    @Ignore
    public void testScrapeData() throws Exception {
        telephoneNumberScraper.scrapeData();
    }


}
