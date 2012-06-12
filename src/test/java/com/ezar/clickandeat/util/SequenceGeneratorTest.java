package com.ezar.clickandeat.util;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class SequenceGeneratorTest {
    
    private static final Logger LOGGER = Logger.getLogger(SequenceGeneratorTest.class);
    
    @Autowired
    private SequenceGenerator sequenceGenerator;
    
    @Test
    public void testGetNextSequence() throws Exception {

        String sequence1 = sequenceGenerator.getNextSequence();
        LOGGER.info("Got sequence: " + sequence1);

        String sequence2 = sequenceGenerator.getNextSequence();
        LOGGER.info("Got sequence: " + sequence2);
        
        Assert.assertTrue(sequence2.compareTo(sequence1) > 0 );
        
    }
}
