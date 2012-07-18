package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Address;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Person;
import com.ezar.clickandeat.model.Restaurant;
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
import java.util.HashMap;
import java.util.Map;

@Controller
public class CheckoutController {
    
    private static final Logger LOGGER = Logger.getLogger(CheckoutController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PersonValidator personValidator;

    @Autowired
    private AddressValidator addressValidator;

    @Autowired
    private RequestHelper requestHelper;
    
    private String timeZone;

    @RequestMapping(value="/secure/checkout.html", method= RequestMethod.GET)
    public ModelAndView checkout(HttpServletRequest request) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();

        Order order = requestHelper.getOrderFromSession(request);
        model.put("order",order);
        
        // Set the standard delivery time onto the request
        Restaurant restaurant = order.getRestaurant();
        Double deliveryTime = restaurant.getDeliveryOptions().getDeliveryTimeMinutes();
        model.put("deliveryTimeMinutes", deliveryTime == null? 0d: deliveryTime);

        LocalDate today = new LocalDate(DateTimeZone.forID(timeZone));
        LocalTime now = new LocalTime(DateTimeZone.forID(timeZone));

        // Set the current delivery type of the order
        model.put("deliveryType",order.getDeliveryType());

        // Confirm that the restaurant is open for delivery and collection
        model.put("currentlyOpenForDelivery", restaurant.isOpenForDelivery(today,now));
        model.put("currentlyOpenForCollection", restaurant.isOpenForCollection(today, now));
        
        return new ModelAndView("checkout",model);
    }


    @ResponseBody
    @RequestMapping(value="/updateOrder.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> updateOrder(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract person and address from request
            Person person = buildPerson(body);
            Address deliveryAddress = buildDeliveryAddress(body);

            // Get the order out of the session
            Order order = requestHelper.getOrderFromSession(request);

            // Update order delivery details
            order.setCustomer(person);
            order.setDeliveryAddress(deliveryAddress);
            order.setRequestedDeliveryTime(new DateTime(DateTimeZone.forID(timeZone)));
            order.setRequestedCollectionTime(new DateTime(DateTimeZone.forID(timeZone)));
            orderRepository.save(order);

            // Mark order updated successfully
            model.put("success",true);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        // Return success status
        return ResponseEntityUtils.buildResponse(model);
    }
    
    

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/secure/proceedToPayment.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> proceedToPayment(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();
        
        try {
            // Extract person and address from request
            Person person = buildPerson(body);
            Address deliveryAddress = buildDeliveryAddress(body);

            // Validate the person object
            BindException personErrors = new BindException(person,"person");
            personValidator.validate(person,personErrors);

            // Validate the address object
            BindException addressErrors = new BindException(deliveryAddress,"address");
            addressValidator.validate(deliveryAddress,addressErrors);

            // Get the order out of the session
            Order order = requestHelper.getOrderFromSession(request);

            // Update order delivery details
            order.setCustomer(person);
            order.setDeliveryAddress(deliveryAddress);
            order.setRequestedDeliveryTime(new DateTime(DateTimeZone.forID(timeZone)));
            order.setRequestedCollectionTime(new DateTime(DateTimeZone.forID(timeZone)));
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

    
    /**
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")    
    private Person buildPerson(String json) {

        Map<String,Object> params = (Map<String,Object>) JSONUtils.deserialize(json);
        Map<String,Object> personParams = (Map<String,Object>) params.get("person");

        // Extract person details
        String firstName = (String)personParams.get("firstName");
        String lastName = (String)personParams.get("lastName");
        String telephone = (String)personParams.get("telephone");
        String email = (String)personParams.get("email");

        return new Person(firstName,lastName,telephone,email);
    }


    /**
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    private Address buildDeliveryAddress(String json) {

        Map<String,Object> params = (Map<String,Object>) JSONUtils.deserialize(json);
        Map<String,Object> deliveryAddressParams = (Map<String,Object>) params.get("deliveryAddress");

        String address1 = (String)deliveryAddressParams.get("address1");
        String address2 = (String)deliveryAddressParams.get("address2");
        String address3 = (String)deliveryAddressParams.get("address3");
        String town = (String)deliveryAddressParams.get("town");
        String region = (String)deliveryAddressParams.get("region");
        String postCode = (String)deliveryAddressParams.get("postCode");

        return new Address(address1,address2,address3,town,region,postCode);
    }
    

    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

}
