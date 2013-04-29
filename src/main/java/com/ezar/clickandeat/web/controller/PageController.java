package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.repository.RestaurantRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;


@Controller
public class PageController {
	
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

    @RequestMapping(value="/legal.html", method = RequestMethod.GET )
    public String legal() {
        return MessageFactory.getLocaleString() + "/legal";
    }


    @RequestMapping(value="/help.html", method = RequestMethod.GET )
    public String help() {
        return MessageFactory.getLocaleString() + "/help";
    }

    @RequestMapping(value="/dataConfidentiality.html", method = RequestMethod.GET )
    public String dataConfidentiality() {
        return MessageFactory.getLocaleString() + "/dataConfidentiality";
    }

    @RequestMapping(value="/admin/dashboard.html", method = RequestMethod.GET)
    public String dashboard() {
        return "admin/dashboard";
    }


    @RequestMapping(value="/admin/restaurants.html", method = RequestMethod.GET)
    public String restaurants() {
        return "admin/restaurants";
    }

    @RequestMapping(value="/admin/orders.html", method = RequestMethod.GET)
    public String orders() {
        return "admin/orders";
    }

    @RequestMapping(value="/admin/registrations.html", method = RequestMethod.GET)
    public String registrations() {
        return "admin/registrations";
    }

    @RequestMapping(value="/admin/reporting.html", method = RequestMethod.GET)
    public String reporting() {
        return "admin/reporting";
    }

}
