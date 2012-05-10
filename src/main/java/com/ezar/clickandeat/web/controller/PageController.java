package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller 
public class PageController {
	
	@RequestMapping(value="/home.html", method=RequestMethod.GET)
	public String home() {
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
    
}
