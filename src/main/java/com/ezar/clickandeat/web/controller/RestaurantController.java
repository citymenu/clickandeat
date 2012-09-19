package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.Restaurant;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.CuisineProvider;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.validator.RestaurantValidator;
import com.ezar.clickandeat.validator.ValidationErrors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private OrderRepository orderRepository;

    @Autowired
    private CuisineProvider cuisineProvider;

    @Autowired
    private JSONUtils jsonUtils;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    @Autowired
    private RestaurantValidator restaurantValidator;
    
            
    @RequestMapping(value="/restaurant.html", method = RequestMethod.GET )
    public ModelAndView get(@RequestParam(value = "restaurantId") String restaurantId, HttpServletRequest request) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Retrieving restaurant with id [" + restaurantId + "]");
        }

        Map<String,Object> model = getModel();
        HttpSession session = request.getSession(true);
        Restaurant restaurant = repository.findByRestaurantId(restaurantId);
        model.put("restaurant",restaurant);

        // If there is no order in the session, create one now
        String orderId = (String)session.getAttribute("orderid");
        if( orderId == null ) {
            assignOrderToSession(session, restaurant);
        }
        else {
            // If there is no restaurant id in the session, get the order and assign this restaurant to it
            if( session.getAttribute("restaurantid") == null ) {
                Order order = orderRepository.findByOrderId(orderId);
                if( order == null ) {
                    assignOrderToSession(session,restaurant);
                }
                else {
                    order.setRestaurantId(restaurantId);
                    order.setRestaurant(restaurant);
                    order.updateCosts();
                    orderRepository.saveOrder(order);
                }
            }
        }

        // Update the restaurant session id
        String restaurantSessionId = (String)session.getAttribute("restaurantid");
        if( restaurantSessionId == null || !(restaurantSessionId.equals(restaurantId))) {
            session.setAttribute("restaurantid", restaurantId);
        }

        return new ModelAndView("restaurant",model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/admin/restaurants/list.ajax", method = RequestMethod.GET )
    public ResponseEntity<byte[]> list(@RequestParam(value = "page") int page, @RequestParam(value = "start") int start,
                                       @RequestParam(value = "limit") int limit, @RequestParam(value="sort", required = false) String sort ) throws Exception {

        PageRequest request;

        if( StringUtils.hasText(sort)) {
            List<Map<String,String>> sortParams = (List<Map<String,String>>)jsonUtils.deserialize(sort);
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
        return responseEntityUtils.buildResponse(model);
    }


    @RequestMapping(value="/admin/restaurants/edit.html", method = RequestMethod.GET )
    public ModelAndView edit(@RequestParam(value = "restaurantId", required = false) String restaurantId) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing restaurant with id [" + restaurantId + "]");
        }

        Map<String,Object> model = getModel();
        model.put("restaurantId",restaurantId);
        return new ModelAndView("admin/editRestaurant",model);
    }


    @ResponseBody
    @RequestMapping(value="/admin/restaurants/create.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> create() throws Exception {
        Map<String,Object> model = getModel();
        Restaurant restaurant = repository.create();
        model.put("success",true);
        model.put("restaurant", jsonUtils.serializeAndEscape(restaurant));
        return responseEntityUtils.buildResponse(model);
    }


    @ResponseBody
    @RequestMapping(value="/admin/restaurants/load.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> load(@RequestParam(value = "restaurantId") String restaurantId) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            Restaurant restaurant = repository.findByRestaurantId(restaurantId);
            model.put("success",true);
            model.put("id",restaurant.getId());
            model.put("restaurant",jsonUtils.serializeAndEscape(restaurant));
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return responseEntityUtils.buildResponse(model);
    }


    @ResponseBody
    @RequestMapping(value="/admin/restaurants/save.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> save(@RequestParam(value = "body") String body) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            Restaurant restaurant = jsonUtils.deserialize(Restaurant.class,body);
            
            // Validate restaurant
            ValidationErrors errors = restaurantValidator.validate(restaurant);
            if( errors.hasErrors()) {
                model.put("success",false);
                model.put("message",errors.getErrorSummary());
            }
            else {
                restaurant = repository.saveRestaurant(restaurant);
                model.put("success",true);
                model.put("id",restaurant.getId());
                model.put("restaurant",restaurant);
            }
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return responseEntityUtils.buildResponse(model);
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
        return responseEntityUtils.buildResponse(model);
    }


    /**
     * @param session
     * @param restaurant
     */

    private void assignOrderToSession( HttpSession session, Restaurant restaurant ) {
        Order order = orderRepository.create();
        order.setRestaurantId(restaurant.getRestaurantId());
        order.setRestaurant(restaurant);
        order.updateCosts();
        orderRepository.save(order);
        session.setAttribute("orderid",order.getOrderId());
        session.removeAttribute("completedorderid");
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
