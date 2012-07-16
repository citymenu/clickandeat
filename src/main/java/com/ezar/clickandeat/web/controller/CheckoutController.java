package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.validator.AddressValidator;
import com.ezar.clickandeat.validator.PersonValidator;
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
import java.util.Map;

@Controller
public class CheckoutController implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(CheckoutController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PersonValidator personValidator;

    @Autowired
    private AddressValidator addressValidator;

    private DateTimeZone dateTimeZone;
    
    private String timeZone;

    @Override
    public void afterPropertiesSet() throws Exception {
        dateTimeZone = DateTimeZone.forID(timeZone);
    }

    @RequestMapping(value="/secure/checkout.html", method= RequestMethod.GET)
    public ModelAndView checkout(HttpServletRequest request) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();

        HttpSession session = request.getSession(true);
        String orderid = (String)session.getAttribute("orderid");
        if( orderid == null ) {
            throw new Exception("No order associated with session");
        }
        Order order = orderRepository.findByOrderId(orderid);
        Restaurant restaurant = order.getRestaurant();
        
        // Set the order in the context
        model.put("order",order);
        
        // Set the standard delivery time onto the request
        Double deliveryTime = restaurant.getDeliveryOptions().getDeliveryTimeMinutes();
        model.put("deliveryTimeMinutes", deliveryTime == null? 0d: deliveryTime);

        LocalDate today = new LocalDate(dateTimeZone);
        LocalTime now = new LocalTime(dateTimeZone);

        // Set the current delivery type of the order
        model.put("deliveryType",order.getDeliveryType());

        // Confirm that the restaurant is open for delivery and collection
        model.put("currentlyOpenForDelivery", restaurant.isOpenForDelivery(today,now));
        model.put("currentlyOpenForCollection", restaurant.isOpenForCollection(today, now));
        
        return new ModelAndView("checkout",model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/secure/checkout.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> setCustomerDetails(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();
        
        try {
            // Extract attributes from request
            Map<String,Object> params = (Map<String,Object>) JSONUtils.deserialize(body);
            Map<String,Object> personParams = (Map<String,Object>) params.get("person");
            Map<String,Object> deliveryAddressParams = (Map<String,Object>) params.get("deliveryAddress");
            String nextPage = (String)params.get("nextPage");

            // Extract person details
            String firstName = (String)personParams.get("firstName");
            String lastName = (String)personParams.get("lastName");
            String telephone = (String)params.get("telephone");
            String email = (String)personParams.get("email");
            Person person = new Person(firstName,lastName, email,telephone);

            // Validate the person object
            BindException personErrors = new BindException(person,"person");
            personValidator.validate(person,personErrors);

            // Extract delivery details
            String address1 = (String)deliveryAddressParams.get("address1");
            String address2 = (String)deliveryAddressParams.get("address2");
            String address3 = (String)deliveryAddressParams.get("address3");
            String town = (String)deliveryAddressParams.get("town");
            String region = (String)deliveryAddressParams.get("region");
            String postCode = (String)deliveryAddressParams.get("postCode");
            Address deliveryAddress = new Address(address1,address2,address3,town,region,postCode);

            // Validate the address object
            BindException addressErrors = new BindException(deliveryAddress,"address");
            addressValidator.validate(deliveryAddress,addressErrors);

            // Get the order out of the session
            HttpSession session = request.getSession(true);
            String orderid = (String)session.getAttribute("orderid");
            if( orderid == null ) {
                throw new Exception("No order associated with session");
            }
            Order order = orderRepository.findByOrderId(orderid);
            if( order == null ) {
                throw new Exception("No order found for orderId: " + orderid);
            }
        
            // Update order delivery details
            order.setCustomer(person);
            order.setDeliveryAddress(deliveryAddress);
            order.setRequestedDeliveryTime(new DateTime(dateTimeZone));
            order.setRequestedCollectionTime(new DateTime(dateTimeZone));
            orderRepository.save(order);
            
            // Mark order updated successfully
            model.put("success",true);
        }
        
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        return ResponseEntityUtils.buildResponse(model);
    }


    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
