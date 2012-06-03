package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.RestaurantRepository;
import flexjson.JSONSerializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RestaurantController {

    private static final Logger LOGGER = Logger.getLogger(RestaurantController.class);
    
    @Autowired
    private RestaurantRepository repository;

    private final JSONSerializer serializer = new JSONSerializer();
    
    @ResponseBody
    @RequestMapping(value="/admin/restaurants/list.ajax", method = RequestMethod.GET )
    public ResponseEntity<byte[]> list(@RequestParam(value = "page") int page, @RequestParam(value = "start") int start,
                                       @RequestParam(value = "limit") int limit ) throws Exception {

        PageRequest request = new PageRequest(page - 1, limit - start );
        Page<Restaurant> restaurants = repository.findAll(request);

        Map<String,Object> model = new HashMap<String,Object>();
        model.put("restaurants",restaurants.getContent());
        model.put("count",repository.count());
        String json = serializer.deepSerialize(model);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    @RequestMapping(value="/admin/restaurants/create.html", method = RequestMethod.GET )
    public ModelAndView create() {
        Map<String,Object> model = new HashMap<String, Object>();
        Restaurant restaurant = new Restaurant();
        model.put("restaurant",restaurant);
        model.put("json",Restaurant.toJSON(restaurant));
        return new ModelAndView("admin/editRestaurant",model);
    }


    @RequestMapping(value="/admin/restaurants/edit.html", method = RequestMethod.GET )
    public ModelAndView edit(@RequestParam(value = "restaurantId") String restaurantId) {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing restaurant with id [" + restaurantId + "]");
        }
        
        Map<String,Object> model = new HashMap<String, Object>();
        Restaurant restaurant = repository.findByRestaurantId(restaurantId);
        model.put("restaurant",restaurant);
        model.put("json",Restaurant.toJSON(restaurant));
        return new ModelAndView("admin/editRestaurant",model);
    }


}
