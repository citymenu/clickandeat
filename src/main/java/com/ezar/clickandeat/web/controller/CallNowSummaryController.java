package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.repository.OrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CallNowSummaryController {
    
    private static final Logger LOGGER = Logger.getLogger(CallNowSummaryController.class);

    @Autowired
    private OrderRepository orderRepository;
    
    @RequestMapping(value="/callNowSummary.html", method= RequestMethod.GET)
    public ModelAndView orderSummary(HttpServletRequest request ) throws Exception {
        Map<String,Object> model = new HashMap<String, Object>();

        String completedOrderId = (String)request.getSession(true).getAttribute("completedorderid");
        if( completedOrderId == null ) {
            return new ModelAndView("redirect:/home.html",model);
        }
        else {
            // Put the system locale on the response
            model.put("validatorLocale", MessageFactory.getLocaleString().split("_")[0]);

            model.put("order",orderRepository.findByOrderId(completedOrderId));
            return new ModelAndView(MessageFactory.getLocaleString() + "/callNowSummary",model);
        }
    }

}
