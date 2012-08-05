package com.ezar.clickandeat.notification;

import com.ezar.clickandeat.model.Order;

public interface ITwilioService {

    void sendOrderNotificationSMS(Order order) throws Exception;

    void makeOrderNotificationCall(Order order) throws Exception;

}
