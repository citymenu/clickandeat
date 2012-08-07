package com.ezar.clickandeat.util;

import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URLDecoder;
import java.net.URLEncoder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class SecurityUtilsTest {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityUtilsTest.class);
    
    @Autowired
    private SecurityUtils securityUtils;
    
    @Test
    public void testEncryptAndDecrypt() throws Exception {
        String plainText = "orderId=00000234#action=" + OrderWorkflowEngine.ACTION_RESTAURANT_ACCEPTS;
        String encrypted = securityUtils.encrypt(plainText);
        LOGGER.info("Encrypted: " + encrypted);
        String decrypted = securityUtils.decrypt(encrypted);
        LOGGER.info("Decrypted: " + decrypted);
        Assert.assertEquals("Decrypted value should match input", plainText, decrypted);
    }
    
}
