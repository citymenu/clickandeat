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

/*

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

  */



    @RequestMapping(value="/callnow.html" )
    public ModelAndView callNow(HttpServletRequest request ) throws Exception {

        LOGGER.info("Displaying the call now page");
        Map<String,Object> model = new HashMap<String, Object>();

        // Clear session attributes
        HttpSession session = request.getSession(true);
        Order order = requestHelper.getOrderFromSession(request);

        session.setAttribute("completedorderid",order.getOrderId());
        session.removeAttribute("orderid");
        session.removeAttribute("orderrestaurantid");
        session.removeAttribute("restaurantid");
        session.removeAttribute("cancheckout");


        //Decide if we save the Order with any details

        // Send redirect to the the callNow page
        return new ModelAndView("callNow",model);
    }

}
