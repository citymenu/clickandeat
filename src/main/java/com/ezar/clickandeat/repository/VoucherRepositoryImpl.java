package com.ezar.clickandeat.repository;

import com.ezar.clickandeat.cache.ClusteredCache;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.util.SequenceGenerator;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.StringUtils;

import java.io.Console;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class VoucherRepositoryImpl implements VoucherRepositoryCustom{

    private static final Logger LOGGER = Logger.getLogger(OrderRepositoryImpl.class);

    private static final List<String> letters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
    
    @Autowired
    private MongoOperations operations;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private ClusteredCache clusteredCache;


    @Override
    public Voucher findByVoucherId(String voucherId) {
        if( voucherId == null ) {
            throw new IllegalArgumentException("voucherId must not be null");
        }
        Voucher voucher;
        voucher = clusteredCache.get(Voucher.class, voucherId);
        if( voucher == null ) {
            voucher = operations.findOne(query(where("voucherId").is(voucherId)),Voucher.class);
            if( voucher != null ) {
                clusteredCache.store(Voucher.class,voucherId,voucher);
            }
        }
        return voucher;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Voucher saveVoucher(Voucher voucher) {
        clusteredCache.remove(Voucher.class, voucher.getVoucherId());
        operations.save(voucher);
        return voucher;
    }

    
    @Override
    public Voucher createVoucher() {
        Voucher voucher = new Voucher();
        String voucherId = generateVoucherId();
        boolean voucherAlreadyExists = findByVoucherId(voucherId) != null;
        while( voucherAlreadyExists ) {
            voucherId = generateVoucherId();
            voucherAlreadyExists = findByVoucherId(voucherId) != null;
        }
        voucher.setVoucherId(voucherId);
        saveVoucher(voucher);
        return voucher;
    }


    private String generateVoucherId() {
        String id = "";
        for( int i = 0; i < 8; i++ ) {
            Double random = Math.random() * 26;
            int floor = (int)Math.floor(random);
            id += letters.get(floor);
        }
        return id;
    }

    

    
}
