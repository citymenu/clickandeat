package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.converter.DateTimeTransformer;
import com.ezar.clickandeat.converter.LocalDateTransformer;
import com.ezar.clickandeat.converter.LocalTimeTransformer;
import com.ezar.clickandeat.converter.NullIdStringTransformer;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.SequenceGenerator;
import flexjson.JSONSerializer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController implements InitializingBean {
    
    private static final Logger LOGGER = Logger.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private JSONUtils jsonUtils;

    private JSONSerializer serializer;

    private String timeZone;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.serializer = new JSONSerializer()
                .transform(new DateTimeTransformer(), DateTime.class)
                .transform(new LocalDateTransformer(), LocalDate.class)
                .transform(new LocalTimeTransformer(), LocalTime.class)
                .transform(new NullIdStringTransformer(), String.class)
                .include("order.restaurant.name")
                .exclude("order.restaurant.*");
    }



    @RequestMapping(value="/buildOrder.html", method = RequestMethod.GET )
    public ModelAndView get(HttpServletRequest request) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Redirecting to current order details page");
        }

        HttpSession session = request.getSession(true);
        String orderid = (String)session.getAttribute("orderid");
        String restaurantid = (String)session.getAttribute("restaurantid");
        Search search = (Search)session.getAttribute("search");

        if( orderid != null ) {
            Order order = orderRepository.findByOrderId(orderid);
            if( order != null ) {
                restaurantid = order.getRestaurantId();
            }
        }
        
        if( restaurantid == null ) {
            if( search == null ) {
                return new ModelAndView("redirect:/home.html",null);
            }
            else {
                return new ModelAndView("redirect:/search.html" + search.getQueryString());
            }
        }
        else {
            return new ModelAndView("redirect:/restaurant.html?restaurantId=" + restaurantid);
        }
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/getOrder.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> getOrder(HttpServletRequest request) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order = null;
            if( orderId != null ) {
                order = orderRepository.findByOrderId(orderId);
            }
            model.put("success",true);
            model.put("order",order);
        }
        catch(Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return buildOrderResponse(model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/addItem.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> addToOrder(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding to order: " + body);
        }
        
        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>)jsonUtils.deserialize(body);
            String restaurantId = (String)params.get("restaurantId");
            String itemId = (String)params.get("itemId");
            String itemType = (String)params.get("itemType");
            List<String> additionalItems = (List<String>)params.get("additionalItems");
            Integer quantity = Integer.valueOf(params.get("quantity").toString());

            // Get the restaurant object
            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
            MenuItem menuItem = restaurant.getMenuItem(itemId);

            // Build new order item
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemNumber(menuItem.getNumber());
            orderItem.setMenuItemId(itemId);
            orderItem.setMenuItemTitle(menuItem.getTitle());
            orderItem.setMenuItemTypeName(itemType);
            orderItem.setAdditionalItems(additionalItems);
            orderItem.setQuantity(quantity);

            // Build the cost of the item
            if( StringUtils.hasText(itemType)) {
                MenuItemTypeCost menuItemTypeCost = menuItem.getMenuItemTypeCost(itemType);
                double additionalItemCost = menuItemTypeCost.getAdditionalItemCost() == null? 0d: menuItemTypeCost.getAdditionalItemCost();
                orderItem.setCost(menuItemTypeCost.getCost() + additionalItemCost * additionalItems.size());
            }
            else {
                double additionalItemCost = menuItem.getAdditionalItemCost() == null? 0d: menuItem.getAdditionalItemCost();
                orderItem.setCost(menuItem.getCost() + additionalItemCost * additionalItems.size());
            }

            // Get the order out of the session            
            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order;
            if( orderId == null ) {
                order = buildAndRegister(session,restaurantId);
            }
            else {
                order = orderRepository.findByOrderId(orderId);
                if( order == null ) {
                    order = buildAndRegister(session,restaurantId);
                    session.setAttribute("orderid",order.getOrderId());
                }
                else if( !restaurantId.equals(order.getRestaurantId())) {
                    order.setRestaurantId(restaurantId);
                    order.setRestaurant(restaurant);
                    order.getOrderItems().clear();
                    order.getOrderDiscounts().clear();
                }
            }
            
            // Add new order item to order and update
            order.addOrderItem(orderItem);
            order = orderRepository.saveOrder(order);

            // Return success
            model.put("success",true);
            model.put("order",order);
        }
        catch(Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return buildOrderResponse(model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/removeItem.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> removeFromOrder(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing from order: " + body);
        }

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>)jsonUtils.deserialize(body);
            String orderItemId = (String)params.get("orderItemId");
            Integer quantity = (Integer)params.get("quantity");

            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order = null;
            if( orderId != null ) {
                order = orderRepository.findByOrderId(orderId);
                if( order != null ) {
                    order.removeOrderItem(orderItemId,quantity);
                    order = orderRepository.saveOrder(order);
                }
            }
            model.put("success",true);
            model.put("order",order);
        }
        catch(Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return buildOrderResponse(model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/updateDeliveryType.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> updateOrderDeliveryType(HttpServletRequest request, @RequestParam(value = "deliveryType") String deliveryType,
                                                          @RequestParam(value = "restaurantId") String restaurantId) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating order delivery type to: " + deliveryType);
        }
        
        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract request parameters
            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order = null;
            if( orderId != null ) {
                order = orderRepository.findByOrderId(orderId);
                if( order != null ) {
                    order.setDeliveryType(deliveryType);
                    order.updateCosts();
                    order = orderRepository.saveOrder(order);
                }
            }
            if( order == null ) {
                order = buildAndRegister(session, restaurantId);
                order.setDeliveryType(deliveryType);
                order = orderRepository.saveOrder(order);
            }
            
            model.put("success",true);
            model.put("order",order);
        }
        catch(Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return buildOrderResponse(model);
    }



    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/updateFreeItem.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> updateFreeItem(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating free item: " + body);
        }

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>)jsonUtils.deserialize(body);
            String discountId = (String)params.get("discountId");
            String freeItem = (String)params.get("freeItem");

            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order = null;
            if( orderId != null ) {
                order = orderRepository.findByOrderId(orderId);
                if( order != null ) {
                    OrderDiscount orderDiscount = order.getOrderDiscount(discountId);
                    if( orderDiscount != null ) {
                        orderDiscount.setSelectedFreeItem(freeItem);
                        order = orderRepository.save(order);
                    }
                }
            }
            model.put("success",true);
            model.put("order",order);
        }
        catch(Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return buildOrderResponse(model);
    }

    

    /**
     * @param session
     * @param restaurantId
     * @return
     */
    
    private Order buildAndRegister(HttpSession session, String restaurantId) {
        Order order = orderRepository.create();
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
        order.setRestaurantId(restaurantId);
        order.setRestaurant(restaurant);
        order = orderRepository.save(order);
        session.setAttribute("orderid",order.getOrderId());
        session.removeAttribute("completedorderid");
        return order;
    }


    /**
     * @param model
     * @return
     * @throws Exception
     */

    private ResponseEntity<byte[]> buildOrderResponse(Map<String,Object> model ) throws Exception {
        String json = serializer.deepSerialize(model);
        String escaped = jsonUtils.escapeQuotes(json);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(escaped.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    @Required
    @Value(value="${timezone}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    
}
