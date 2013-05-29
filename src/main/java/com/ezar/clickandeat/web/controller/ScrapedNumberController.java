package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.util.ScrapedNumberProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Controller
public class ScrapedNumberController {
    
    @Autowired
    private ScrapedNumberProvider scrapedNumberProvider;
    
    @Autowired
    private MongoOperations mongoOperations;
    
    @ResponseBody
    @RequestMapping(value="/**/scrapercontact/{phone}/{sms}/{email:.*}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> updateScraperContacts(@PathVariable("phone") String phone,
                                        @PathVariable("sms") String sms,
                                        @PathVariable("email") String email) throws Exception {
        scrapedNumberProvider.setNotificationPhoneNumber(phone);
        scrapedNumberProvider.setNotificationSMSNumber(sms);
        scrapedNumberProvider.setNotificationEmail(email);

        List<Restaurant> scrapedRestaurants = mongoOperations.find(new Query(where("externalId").ne(null)),Restaurant.class);
        for(Restaurant restaurant:scrapedRestaurants) {
            restaurant.getNotificationOptions().setNotificationPhoneNumber(phone);
            restaurant.getNotificationOptions().setNotificationSMSNumber(sms);
            restaurant.getNotificationOptions().setNotificationEmailAddress(email);
            mongoOperations.save(restaurant);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>("Updated notification details".getBytes("utf-8"), headers, HttpStatus.OK);
        

    }
    
}
