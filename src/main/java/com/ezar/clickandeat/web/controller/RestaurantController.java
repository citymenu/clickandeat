package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.JSONUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
public class RestaurantController {

    private static final Logger LOGGER = Logger.getLogger(RestaurantController.class);
    
    @Autowired
    private RestaurantRepository repository;

    @Autowired
    private CuisineProvider cuisineProvider;
    

    @RequestMapping(value="/restaurant.html", method = RequestMethod.GET )
    public ModelAndView get(@RequestParam(value = "restaurantId") String restaurantId) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Retrieving restaurant with id [" + restaurantId + "]");
        }

        Map<String,Object> model = getModel();
        Restaurant restaurant = repository.findByRestaurantId(restaurantId);
        model.put("restaurant",restaurant);
        return new ModelAndView("restaurant",model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/admin/restaurants/list.ajax", method = RequestMethod.GET )
    public ResponseEntity<byte[]> list(@RequestParam(value = "page") int page, @RequestParam(value = "start") int start,
                                       @RequestParam(value = "limit") int limit, @RequestParam(value="sort", required = false) String sort ) throws Exception {

        PageRequest request;
        
        if( StringUtils.hasText(sort)) {
            List<Map<String,String>> sortParams = (List<Map<String,String>>)JSONUtils.deserialize(sort);
            Map<String,String> sortProperties = sortParams.get(0);
            String direction = sortProperties.get("direction");
            String property = sortProperties.get("property");
            request = new PageRequest(page - 1, limit - start, ( "ASC".equals(direction)? ASC : DESC ), property );
        }
        else {
            request = new PageRequest(page - 1, limit - start );
        }
        
        Page<Restaurant> restaurants = repository.findAll(request);

        Map<String,Object> model = new HashMap<String,Object>();
        model.put("restaurants",restaurants.getContent());
        model.put("count",repository.count());
        String json = JSONUtils.serialize(model);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    @RequestMapping(value="/admin/restaurants/create.html", method = RequestMethod.GET )
    public ModelAndView create() {
        Map<String,Object> model = getModel();
        Restaurant restaurant = repository.create();
        model.put("restaurant", restaurant);
        model.put("json",JSONUtils.serialize(restaurant));
        return new ModelAndView("admin/editRestaurant",model);
    }


    @RequestMapping(value="/admin/restaurants/edit.html", method = RequestMethod.GET )
    public ModelAndView edit(@RequestParam(value = "restaurantId") String restaurantId) {
        
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing restaurant with id [" + restaurantId + "]");
        }
        
        Map<String,Object> model = getModel();
        Restaurant restaurant = repository.findByRestaurantId(restaurantId);
        model.put("restaurant",restaurant);
        model.put("json",JSONUtils.serialize(restaurant));
        return new ModelAndView("admin/editRestaurant",model);
    }


    @ResponseBody
    @RequestMapping(value="/admin/restaurants/save.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> save(@RequestParam(value = "body") String body) throws Exception {
        Map<String,Object> model = new HashMap<String, Object>();

        try {
            Restaurant restaurant = JSONUtils.deserialize(Restaurant.class,body);
            restaurant = repository.saveRestaurant(restaurant);
            model.put("success",true);
            model.put("id",restaurant.getId());
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        String json = JSONUtils.serialize(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value="/admin/restaurants/delete.ajax", method = RequestMethod.GET )
    public ResponseEntity<byte[]> delete(@RequestParam(value = "restaurantId") String restaurantId) throws Exception {
        Map<String,Object> model = new HashMap<String, Object>();
        try {
            Restaurant restaurant = repository.findByRestaurantId(restaurantId);
            repository.delete(restaurant);
            model.put("success",true);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        String json = JSONUtils.serialize(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    /**
     * Returns standard model
     * @return
     */
    
    private Map<String,Object> getModel() {
        Map<String,Object> model = new HashMap<String, Object>();
        Set<String> cuisines = cuisineProvider.getCuisineList();
        String cuisineArrayList = StringUtils.collectionToDelimitedString(cuisines,"','");
        model.put("cuisinesArray","'" + cuisineArrayList + "'");
        return model;
    }


 
}
