package com.ezar.clickandeat.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="vouchers")
public class Voucher extends PersistentObject {
    
    public static final Double DEFAULT_VOUCHER_DISCOUNT = 10d;
    
    @Indexed
    private String voucherId;
    
    private boolean used;
    
    private double discount = DEFAULT_VOUCHER_DISCOUNT;

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
