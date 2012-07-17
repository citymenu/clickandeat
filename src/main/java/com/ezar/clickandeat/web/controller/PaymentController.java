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
public class PaymentController {
    
    private static final Logger LOGGER = Logger.getLogger(PaymentController.class);

    @Autowired
    private OrderRepository orderRepository;


    @RequestMapping(value="/secure/payment.html", method= RequestMethod.GET)
    public String payment(HttpServletRequest request) throws Exception {
        return "payment";
    }

}
