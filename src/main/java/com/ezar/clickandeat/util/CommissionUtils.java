package com.ezar.clickandeat.util;

import com.ezar.clickandeat.model.Order;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommissionUtils implements InitializingBean {

    private static final Logger LOGGER = Logger.getLogger(CommissionUtils.class);
    
    private static CommissionUtils instance;
    
    private double cateringSalesTax;
    
    private double companySalesTax;
    

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }


    /**
     * @param order
     * @return
     */
    
    public static Double calculateCommission(Order order) {
        if( instance == null ) {
            throw new UnsupportedOperationException("Do not use until initialized");
        }
        if( order == null ) {
            throw new IllegalArgumentException("Order must not be null");
        }
        if( order.getRestaurant() == null ) {
            throw new IllegalArgumentException("Order restaurant must not be null");
        }
        Double restaurantNetCost = order.getRestaurantCost() / ( 100 + instance.cateringSalesTax ) * 100;
        Double commissionPercent = order.getRestaurant().getCommissionPercent();
        Double netCommission = restaurantNetCost * commissionPercent / 100;
        Double commission = netCommission * ( 100 + instance.companySalesTax ) / 100;
        if( commission > 0 ) {
            LOGGER.info("Calculated commission " + commission + " for order id: " + order.getOrderId());
        }
        return commission;
    }


    @Required
    @Value(value="${commission.cateringSalesTax}")
    public void setCateringSalesTaxRate(double cateringSalesTax) {
        this.cateringSalesTax = cateringSalesTax;
    }


    @Required
    @Value(value="${commission.companySalesTax}")
    public void setCompanySalesTaxRate(double companySalesTax) {
        this.companySalesTax = companySalesTax;
    }
}
