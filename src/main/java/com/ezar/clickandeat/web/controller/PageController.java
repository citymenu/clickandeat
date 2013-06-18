package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.config.MessageFactory;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.SitemapProvider;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;


@Controller
public class PageController {
	
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CuisineProvider cuisineProvider;
    
    @Autowired
    private SitemapProvider sitemapProvider;
    
    
	@RequestMapping(value="/home.html", method=RequestMethod.GET)
	public ModelAndView home() {
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("recommendations", restaurantRepository.getRecommended());
        model.put("locationprimary",cuisineProvider.getLocationPrimary());
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

    @RequestMapping(value="/sitemap.xml", method = RequestMethod.GET)
    public ResponseEntity<byte[]> sitemap() throws Exception {
        String sitemap = sitemapProvider.getSitemap();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(sitemap.getBytes("utf-8"), headers, HttpStatus.OK);
    }

    @RequestMapping(value="/google1794732305a7d580.html", method = RequestMethod.GET )
    @ResponseBody
    public ResponseEntity<byte[]> validateGoogle() throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setCacheControl("no-cache");
        String validation = "google-site-verification: google1794732305a7d580.html";
        return new ResponseEntity<byte[]>(validation.getBytes("utf-8"), headers, HttpStatus.OK);
    }

}
