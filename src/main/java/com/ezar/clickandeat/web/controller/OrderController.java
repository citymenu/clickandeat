package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.converter.DoubleTransformer;
import com.ezar.clickandeat.converter.IntegerTransformer;
import com.ezar.clickandeat.model.Order;
import com.ezar.clickandeat.model.OrderItem;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.SequenceGenerator;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OrderController {
    
    private static final Logger LOGGER = Logger.getLogger(OrderController.class);

    @Autowired
    private OrderRepository repository;
    
    @Autowired
    private SequenceGenerator sequenceGenerator;

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/addItem.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> addToOrder(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>)JSONUtils.deserialize(body);
            String restaurantId = (String)params.get("restaurantId");
            String itemId = (String)params.get("itemId");
            String itemName = (String)params.get("itemName");
            Double itemCost = Double.valueOf(params.get("itemCost").toString());
            Integer quantity = (Integer)params.get("quantity");
            
            // Build new order item
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(itemId);
            orderItem.setMenuItemTitle(itemName);
            orderItem.setCost(itemCost);
            orderItem.setQuantity(quantity);
            
            // Get the order out of the session            
            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order;
            if( orderId == null ) {
                order = buildAndRegister(session,restaurantId);
            }
            else {
                order = repository.findByOrderId(orderId);
                if( order == null ) {
                    order = buildAndRegister(session,restaurantId);
                }
                else if( !restaurantId.equals(order.getRestaurantId())) {
                    order.setRestaurantId(restaurantId);
                    order.getOrderItems().clear();
                }
            }
            
            // Add new order item to order and update
            order.addOrderItem(orderItem);
            order = repository.saveOrder(order);

            // Return success
            model.put("success",true);
            model.put("order",order);
        }
        catch(Exception ex ) {
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        
        String json = JSONUtils.serialize(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/removeItem.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> removeFromOrder(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>)JSONUtils.deserialize(body);
            String itemId = (String)params.get("itemId");
            Integer quantity = (Integer)params.get("quantity");

            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order = null;
            if( orderId != null ) {
                order = repository.findByOrderId(orderId);
                if( order != null ) {
                    order.removeOrderItem(itemId,quantity);
                    order = repository.saveOrder(order);
                }
            }
            model.put("success",true);
            model.put("order",order);
        }
        catch(Exception ex ) {
            model.put("success",false);
            model.put("message",ex.getMessage());
        }

        String json = JSONUtils.serialize(model);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<byte[]>(json.getBytes("utf-8"), headers, HttpStatus.OK);

    }


    /**
     * @param session
     * @param restaurantId
     * @return
     */
    
    private Order buildAndRegister(HttpSession session, String restaurantId ) {
        Order order = repository.create();
        order.setRestaurantId(restaurantId);
        order = repository.save(order);
        session.setAttribute("orderid",order.getOrderId());
        return order;
    } 
    
}
