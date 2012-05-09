package com.ezar.clickandeat.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

}
