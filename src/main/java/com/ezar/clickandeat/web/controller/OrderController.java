package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.repository.OrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {
    
    private static final Logger LOGGER = Logger.getLogger(OrderController.class);

    @Autowired
    private OrderRepository repository;

    @ResponseBody
    @RequestMapping(value="/order/update.ajax", method = RequestMethod.GET )
    public ResponseEntity<byte[]> addToOrder(@RequestParam(value = "restaurantId") String restaurantId, @RequestParam(value = "menuCategoryId") String menuCategoryId,
                                       @RequestParam(value = "menuItemId") String menuItemId, @RequestParam(value="menuItemType", required = false) String menuItemType,
                                       @RequestParam(value = "quantity") int quantity, @RequestParam(value = "action") String action ) throws Exception {

        try {
            
        }
        catch(Exception ex ) {

        }
        
        String json = null;

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


}
