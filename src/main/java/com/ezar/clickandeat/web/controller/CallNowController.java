package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.web.controller.helper.RequestHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CallNowController {
    
    private static final Logger LOGGER = Logger.getLogger(CallNowController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RequestHelper requestHelper;


    @RequestMapping(value="/callnow.html" )
    public ModelAndView callNow(HttpServletRequest request ) throws Exception {

        LOGGER.info("Displaying the call now page");
        Map<String,Object> model = new HashMap<String, Object>();

        // Clear session attributes
        HttpSession session = request.getSession(true);
        Order order = requestHelper.getOrderFromSession(request);

        session.setAttribute("completedorderid",order.getOrderId());
        //Joe doesn't want to remove the attributes at this point. He want's to do it when we send the email with the cupon
        /*
        session.removeAttribute("orderid");
        session.removeAttribute("orderrestaurantid");
        session.removeAttribute("restaurantid");
        session.removeAttribute("cancheckout");
        */

        //Decide if we save the Order with any details

        // Send redirect to the the callNow page
        return new ModelAndView("callNow",model);
    }

}
