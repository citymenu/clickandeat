package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller 
public class PageController {
	
    private int counter = 0;
    
	@RequestMapping(value="/home.html", method=RequestMethod.GET)
	public String home(HttpServletRequest request) {
		return "home";
	}


    @RequestMapping(value="/secure/login.html", method=RequestMethod.GET)
    public String login() {
        return "login";
    }


    @RequestMapping(value="/secure/register.html", method = RequestMethod.GET)
    public ModelAndView register() {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("user",new User());
        return new ModelAndView("register",map);
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
