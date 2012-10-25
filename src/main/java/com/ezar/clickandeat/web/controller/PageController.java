package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller 
public class PageController {
	
    @Autowired
    private CuisineProvider cuisineProvider;

    @Autowired
    private RestaurantRepository restaurantRepository;

    
	@RequestMapping(value="/home.html", method=RequestMethod.GET)
	public ModelAndView home() {
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("recommendations", restaurantRepository.getRecommended());
    	return new ModelAndView(MessageFactory.getLocaleString() + "/home", model);
	}


    @RequestMapping(value="/termsAndConditions.html", method = RequestMethod.GET )
    public String termsAndConditions() {
        return MessageFactory.getLocaleString() + "/termsAndConditions";
    }


    @RequestMapping(value="/admin/dashboard.html", method = RequestMethod.GET)
    public String dashboard() {
        return "admin/dashboard";
    }


    @RequestMapping(value="/admin/restaurants.html", method = RequestMethod.GET)
    public String restaurants() {
        return "admin/restaurants";
    }

}
