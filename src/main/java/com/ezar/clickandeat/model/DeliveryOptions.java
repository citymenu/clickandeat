package com.ezar.clickandeat.model;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOptions {

    private List<DeliveryOption> deliveryOptions = new ArrayList<DeliveryOption>();

    @Override
    public String toString() {
        return "DeliveryOptions{" +
                "deliveryOptions=" + deliveryOptions +
                '}';
    }

    public List<DeliveryOption> getDeliveryOptions() {
        return deliveryOptions;
    }

    public void setDeliveryOptions(List<DeliveryOption> deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }
}
