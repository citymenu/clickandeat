package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.notification.NotificationService;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.validator.AddressValidator;
import com.ezar.clickandeat.validator.PersonValidator;
import com.ezar.clickandeat.web.controller.helper.RequestHelper;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PaymentController {
    
    private static final Logger LOGGER = Logger.getLogger(PaymentController.class);

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private NotificationService notificationService;
    

    @Autowired
    private RequestHelper requestHelper;

    @RequestMapping(value="/secure/payment.html", method= RequestMethod.GET)
    public String payment(HttpServletRequest request) throws Exception {
        return "payment";
    }


    @ResponseBody
    @RequestMapping(value="/secure/processCardPayment.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> processCardPayment(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();
        
        try {
            Order order = requestHelper.getOrderFromSession(request);

            //TODO get credit card details and handle payment processing/result
            order.setCardTransactionId("12345");
            order.setCardTransactionStatus(Order.CARD_TRANSACTION_AUTHORIZED);
            orderRepository.saveOrder(order);
            
            // Send notification to restaurant
            notificationService.sendOrderNotificationToRestaurant(order);

            // Send notification email to customer
            notificationService.sendOrderConfirmationToCustomer(order);
            
            // Update order status
            orderRepository.updateOrderStatus(order.getOrderId(),Order.AWAITING_RESTAURANT);

            // Set status to success
            model.put("success",true);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        return ResponseEntityUtils.buildResponse(model);

    }



}
