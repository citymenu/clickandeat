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
    private GeoLocationService geoLocationService;
    
    @Autowired
    private RequestHelper requestHelper;

    @Autowired
    private GeoLocationService locationService;
    
    @Autowired
    private JSONUtils jsonUtils;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;


    /**
     * Mappings to auto fill delivery address
     */
    private String autofillAddress1;
    private String autofillTown;
    private String autofillRegion;
    private String autofillPostCode;
    

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
        
        // If the restaurant takes telephone orders only, send to call summary page
        if( restaurant.getPhoneOrdersOnly()) {
            return new ModelAndView(MessageFactory.getLocaleString() + "/callNowSummary",model);
        }
        else {
            return new ModelAndView("checkout",model);
        }
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/updateOrder.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> updateOrder(HttpServletRequest request, @RequestParam(value = "body") String body, @RequestParam(value="updateLocation") Boolean updateLocation ) throws Exception {


        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>) jsonUtils.deserialize(body);
            
            // Extract person and address from request
            Person person = buildPerson(params);
            Address deliveryAddress = buildDeliveryAddress(params);
            String additionalInstructions = (String)params.get("additionalInstructions");
            Boolean termsAndConditionsAccepted = (Boolean)params.get("termsAndConditionsAccepted"); 

            // Get the order out of the session
            Order order = requestHelper.getOrderFromSession(request);

            // Update order delivery details
            order.setCustomer(person);

            // Update delivery address if a valid address is entered
            if( updateLocation ) {
                GeoLocation geoLocation = geoLocationService.getLocation(deliveryAddress);
                if( geoLocation != null ) {
                    deliveryAddress.setLocation(geoLocation.getLocation());
                    deliveryAddress.setRadius(geoLocation.getRadius());
                    deliveryAddress.setRadiusWarning(geoLocation.getRadiusWarning());
                    order.setDeliveryAddress(deliveryAddress);
                }
            }
            
            order.setAdditionalInstructions(additionalInstructions);
            order.setTermsAndConditionsAccepted(termsAndConditionsAccepted);
            order = orderRepository.save(order);

            // Update can checkout status of order
            HttpSession session = request.getSession(true);
            session.setAttribute("cancheckout", order.getCanCheckout());
            session.setAttribute("cansubmitpayment", order.getCanSubmitPayment());
            
            // Mark order updated successfully if we are updating the location
            if( updateLocation ) {
                model.put("success",true);
                model.put("restaurantId",order.getRestaurantId());
            }
            else {
                model.put("deliveryType", order.getDeliveryType());
                model.put("deliveryAddress",order.getDeliveryAddress());
                if( order.getCanSubmitPayment()) {
                    model.put("success",true);
                }
                else {
                    model.put("success",false);
                    if( !order.getRestaurantWillDeliver()) {
                        model.put("reason","checkout-restaurant-wont-deliver");
                    }
                    else if( order.getExtraSpendNeededForDelivery() > 0 ) {
                        model.put("reason","extra-spend-needed-for-delivery");
                    }
                }
            }
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
            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>) jsonUtils.deserialize(body);

            boolean success = true;
            String reason = null;
            
            // Extract person and address from request
            Person person = buildPerson(params);
            String additionalInstructions = (String)params.get("additionalInstructions");
            Boolean termsAndConditionsAccepted = (Boolean)params.get("termsAndConditionsAccepted");

            // Get the order out of the session
            Order order = requestHelper.getOrderFromSession(request);

            // Update order details
            order.setCustomer(person);
            order.setAdditionalInstructions(additionalInstructions);
            order.setTermsAndConditionsAccepted(termsAndConditionsAccepted);
            order.updateRestaurantIsOpen();

            // Update order delivery address details
            Address deliveryAddress = buildDeliveryAddress(params);
            GeoLocation deliveryLocation = locationService.getLocation(deliveryAddress);
            if( deliveryLocation == null ) {
                success = false;
                reason = "checkout-location-not-found";
            }
            else {
                deliveryAddress.setLocation(deliveryLocation.getLocation());
                deliveryAddress.setRadius(deliveryLocation.getRadius());
                deliveryAddress.setRadiusWarning(deliveryLocation.getRadiusWarning());
                order.setDeliveryAddress(deliveryAddress);
            }
            
            // If the restaurant is not open, return an error
            if( !order.getRestaurantIsOpen()) {
                success = false;
                reason = "checkout-restaurant-closed";
            }

            // Update the order object
            order = orderRepository.saveOrder(order);

            // If the restaurant does not deliver to this location, return an error
            if( !order.getRestaurantWillDeliver()) {
                success = false;
                reason = "checkout-restaurant-wont-deliver";
            }
            else if( order.getExtraSpendNeededForDelivery() > 0 ) {
                success = false;
                reason = "extra-spend-needed-for-delivery";
            }

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
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")    
    private Person buildPerson(Map<String,Object> params) {

        Map<String,Object> personParams = (Map<String,Object>) params.get("person");

        // Extract person details
        String firstName = (String)personParams.get("firstName");
        String lastName = (String)personParams.get("lastName");
        String telephone = (String)personParams.get("telephone");
        String email = (String)personParams.get("email");

        return new Person(firstName,lastName,telephone,email);
    }


    /**
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    private Address buildDeliveryAddress(Map<String,Object> params) {

        Map<String,Object> deliveryAddressParams = (Map<String,Object>) params.get("deliveryAddress");

        String address1 = (String)deliveryAddressParams.get("address1");
        String town = (String)deliveryAddressParams.get("town");
        String region = (String)deliveryAddressParams.get("region");
        String postCode = (String)deliveryAddressParams.get("postCode");

        Address address = new Address(address1,town,region, postCode);

        GeoLocation location = geoLocationService.getLocation(address);
        if( location != null ) {
            address.setLocation(location.getLocation());
            address.setRadius(location.getRadius());
            address.setRadiusWarning(location.getRadiusWarning());
        }
        
        return address;
    }


    @Required
    @Value(value="${autofill.address1}")
    public void setAutofillAddress1(String autofillAddress1) {
        this.autofillAddress1 = autofillAddress1;
    }

    @Required
    @Value(value="${autofill.town}")
    public void setAutofillTown(String autofillTown) {
        this.autofillTown = autofillTown;
    }

    @Required
    @Value(value="${autofill.region}")
    public void setAutofillRegion(String autofillRegion) {
        this.autofillRegion = autofillRegion;
    }

    @Required
    @Value(value="${autofill.postCode}")
    public void setAutofillPostCode(String autofillPostCode) {
        this.autofillPostCode = autofillPostCode;
    }
}
