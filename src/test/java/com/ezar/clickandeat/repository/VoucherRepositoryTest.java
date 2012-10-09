package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/application-context.xml"})
public class VoucherRepositoryTest {

    private static final Logger LOGGER = Logger.getLogger(VoucherRepositoryTest.class);
    
    @Autowired
    private VoucherRepository voucherRepository;


    @Test
    public void testGetValidVoucher() throws Exception {
        Voucher voucher = voucherRepository.createVoucher();
        LOGGER.info("Got valid voucher: " + voucher.getVoucherId());
        
        Voucher usedVoucher = voucherRepository.createVoucher();
        usedVoucher.setUsed(true);
        voucherRepository.saveVoucher(usedVoucher);
        LOGGER.info("Got used voucher: " + usedVoucher.getVoucherId());
    }
    
    
}
