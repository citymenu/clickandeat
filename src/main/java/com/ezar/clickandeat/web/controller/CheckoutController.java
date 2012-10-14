package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.maps.GeoLocationService;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.web.controller.helper.RequestHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
public class CheckoutController {
    
    private static final Logger LOGGER = Logger.getLogger(CheckoutController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RequestHelper requestHelper;

    @Autowired
    private GeoLocationService locationService;
    
    @Autowired
    private JSONUtils jsonUtils;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    private int maxRadiusMetres;
    

    @RequestMapping(value="/checkout.html", method= RequestMethod.GET)
    public ModelAndView checkout(HttpServletRequest request) throws Exception {
        
        Map<String,Object> model = new HashMap<String, Object>();
        HttpSession session = request.getSession(true);
        
        Order order = requestHelper.getOrderFromSession(request);
        if( order == null ) {
            if( session.getAttribute("restaurantid") != null ) {
                return new ModelAndView("redirect:/restaurant.html?restaurantId=" + session.getAttribute("restaurantid"),model);
            }
            else {
                return new ModelAndView("redirect:/home.html");
            }
        }
        else {
            model.put("order",order);
        }

        // Confirm if we can checkout this order
        boolean canCheckout = order.getCanCheckout();
        session.setAttribute("cancheckout", canCheckout);
        if( !canCheckout ) {
            return new ModelAndView("redirect:/restaurant.html?restaurantId=" + order.getRestaurant().getRestaurantId());
        }

        // Set the standard delivery time onto the request
        Restaurant restaurant = order.getRestaurant();
        model.put("restaurant",restaurant);

        // Put the system locale on the response
        model.put("validatorLocale", MessageFactory.getLocaleString().split("_")[0]);
        
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
            String additionalInstructions = buildAdditionalInstructions(body);

            // Get the order out of the session
            Order order = requestHelper.getOrderFromSession(request);

            // Update order delivery details
            order.setCustomer(person);
            order.setDeliveryAddress(deliveryAddress);
            order.setAdditionalInstructions(additionalInstructions);
            orderRepository.save(order);

            // Update can checkout status of order
            HttpSession session = request.getSession(true);
            session.setAttribute("cancheckout", order.getCanCheckout());

            // Mark order updated successfully
            model.put("success",true);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        // Return success status
        return responseEntityUtils.buildResponse(model);
    }
    
    

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/proceedToPayment.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> proceedToPayment(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();
        
        try {
            boolean success = true;
            String reason = null;
            
            // Extract person and address from request
            Person person = buildPerson(body);
            Address deliveryAddress = buildDeliveryAddress(body);
            String additionalInstructions = buildAdditionalInstructions(body); 

            // Get the order out of the session
            Order order = requestHelper.getOrderFromSession(request);

            // Update order delivery details
            order.setCustomer(person);
            order.setDeliveryAddress(deliveryAddress);
            order.setAdditionalInstructions(additionalInstructions);
            order.updateRestaurantIsOpen();

            // If the restaurant is not open, return an error
            if( !order.getRestaurantIsOpen()) {
                success = false;
                reason = "checkout-restaurant-closed";
            }
            else {
                // If the order is for delivery, check that the delivery address can be determined
                if( Order.DELIVERY.equals(order.getDeliveryType())) {
                    GeoLocation deliveryLocation = locationService.getLocation(order.getDeliveryAddress());
                    if( deliveryLocation == null || deliveryLocation.getRadius() > maxRadiusMetres ) {
                        success = false;
                        reason = "checkout-location-not-found";
                    }
                    else if( !restaurantRepository.willDeliverToLocationOrPostCode(order.getRestaurant(), deliveryLocation.getLocation(), deliveryAddress.getPostCode())) {
                        success = false;
                        reason = "checkout-restaurant-wont-deliver";
                    }
                    else {
                        order.getDeliveryAddress().setLocation(deliveryLocation.getLocation());
                    }
                }
            }

            // Update the order object
            orderRepository.saveOrder(order);

            // Indicate if the data is all valid
            model.put("success",success);
            model.put("reason",reason);
        }
        
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        return responseEntityUtils.buildResponse(model);
    }

    
    /**
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")    
    private Person buildPerson(String json) {

        Map<String,Object> params = (Map<String,Object>) jsonUtils.deserialize(json);
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

        Map<String,Object> params = (Map<String,Object>) jsonUtils.deserialize(json);
        Map<String,Object> deliveryAddressParams = (Map<String,Object>) params.get("deliveryAddress");

        String address1 = (String)deliveryAddressParams.get("address1");
        String address2 = (String)deliveryAddressParams.get("address2");
        String address3 = (String)deliveryAddressParams.get("address3");
        String town = (String)deliveryAddressParams.get("town");
        String region = (String)deliveryAddressParams.get("region");
        String postCode = (String)deliveryAddressParams.get("postCode");

        return new Address(address1,address2,address3,town,region,postCode);
    }


    /**
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    private String buildAdditionalInstructions(String json) {
        Map<String,Object> params = (Map<String,Object>) jsonUtils.deserialize(json);
        return (String)params.get("additionalInstructions");
    }


    @Required
    @Value(value="${location.maxRadiusMetres}")
    public void setMaxRadiusMetres(int maxRadiusMetres) {
        this.maxRadiusMetres = maxRadiusMetres;
    }
    
}
