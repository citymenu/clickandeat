package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Person;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.validator.PersonValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private PersonValidator personValidator;

    @RequestMapping(value="/secure/checkout.html", method= RequestMethod.GET)
    public String checkout(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String checkoutstage = (String)session.getAttribute("checkoutstage");
        return checkoutstage == null? "checkout": checkoutstage;
    }

    @ResponseBody
    @RequestMapping(value="/secure/signin.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> signin(@RequestParam(value = "emailaddress", required = false) String email, @RequestParam(value = "password", required = false) String password ) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/secure/setcustomerdetails.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> setCustomerDetails(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();
        
        try {
            // Extract attributes from request
            Map<String,Object> params = (Map<String,Object>) JSONUtils.deserialize(body);
            String firstName = (String)params.get("firstName");
            String lastName = (String)params.get("lastName");
            String email = (String)params.get("email");
            String confirmEmail = (String)params.get("firstName");
            String telephone = (String)params.get("telephone");
            String mobile = (String)params.get("mobile");
            
            // Build new person object
            Person person = new Person();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setEmail(email);
            person.setConfirmEmail(confirmEmail);
            person.setTelephone(telephone);
            person.setMobile(mobile);
            
            // Validate the person object
            BindException errors = new BindException(person,"person");
            personValidator.validate(person,errors);

            HttpSession session = request.getSession(true);
            String orderid = (String)session.getAttribute("orderid");
            if( orderid == null ) {
                throw new Exception("No order associated with session");
            }
            Order order = orderRepository.findByOrderId(orderid);
            
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        return ResponseEntityUtils.buildResponse(model);
    }

}
