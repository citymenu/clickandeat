package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.converter.DateTimeTransformer;
import com.ezar.clickandeat.converter.LocalDateTransformer;
import com.ezar.clickandeat.converter.LocalTimeTransformer;
import com.ezar.clickandeat.converter.NullIdStringTransformer;
import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.util.SequenceGenerator;
import flexjson.JSONSerializer;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
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
import java.util.ArrayList;
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
            String itemSubType = (String)params.get("itemSubType");
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
            orderItem.setMenuItemSubTypeName(itemSubType);
            orderItem.setAdditionalItems(additionalItems);
            orderItem.setQuantity(quantity);

            // Work out the cost of any additional Items
            double additionalItemCost = 0d;
            for( String additionalItemName: additionalItems ) {
                if( StringUtils.hasText(itemType)) {
                    MenuItemTypeCost menuItemTypeCost = menuItem.getMenuItemTypeCost(itemType);
                    additionalItemCost += menuItemTypeCost.getAdditionalItemCost() == null? 0d: menuItemTypeCost.getAdditionalItemCost();
                }
                else if( menuItem.getAdditionalItemCost() != null ) {
                    additionalItemCost += menuItem.getAdditionalItemCost();
                }
                else {
                    MenuItemAdditionalItemChoice additionalItemChoice = menuItem.getMenuItemAdditionalItemChoice(additionalItemName);
                    additionalItemCost += additionalItemChoice.getCost() == null? 0d: additionalItemChoice.getCost();
                }
            }

            // Build the cost of the item
            if( StringUtils.hasText(itemType)) {
                MenuItemTypeCost menuItemTypeCost = menuItem.getMenuItemTypeCost(itemType);
                orderItem.setCost(menuItemTypeCost.getCost() + additionalItemCost);
            }
            else if( StringUtils.hasText(itemSubType)) {
                MenuItemSubType menuItemSubType = menuItem.getMenuItemSubType(itemSubType);
                orderItem.setCost(menuItemSubType.getCost() + additionalItemCost);
            }
            else {
                orderItem.setCost(menuItem.getCost() + additionalItemCost);
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

            // Update can checkout status of order
            session.setAttribute("cancheckout", order.getCanCheckout());
            
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
    @RequestMapping(value="/order/checkSpecialOffer.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> checkSpecialOfferAvailability(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Checking if special offer is applicable for order");
        }

        Map<String,Object> model = new HashMap<String, Object>();

        try {

            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>)jsonUtils.deserialize(body);
            String restaurantId = (String)params.get("restaurantId");
            String orderId = (String)params.get("orderId");
            String specialOfferId = (String)params.get("specialOfferId");

            // Get the restaurant object
            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
            SpecialOffer specialOffer = restaurant.getSpecialOffer(specialOfferId);
            
            // Get the order object
            if( orderId != null ) {
                Order order = orderRepository.findByOrderId(orderId);
                model.put("success",true);
                model.put("applicable",specialOffer.isApplicableTo(order));
            }
            else {
                model.put("success",true);
                model.put("applicable",specialOffer.isAvailableAt(new DateTime()));
            }
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
    @RequestMapping(value="/order/addSpecialOffer.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> addSpecialOfferToOrder(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding special offer to order: " + body);
        }

        Map<String,Object> model = new HashMap<String, Object>();

        try {

            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>)jsonUtils.deserialize(body);
            String restaurantId = (String)params.get("restaurantId");
            String specialOfferId = (String)params.get("specialOfferId");
            List<String> itemChoices = (List<String>)params.get("itemChoices");
            Integer quantity = Integer.valueOf(params.get("quantity").toString());

            // Get the restaurant object
            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
            SpecialOffer specialOffer = restaurant.getSpecialOffer(specialOfferId);

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
                }
            }

            // Check if the special offer is applicable to this order
            if( !specialOffer.isApplicableTo(order)) {
                model.put("success",true);
                model.put("applicable",false);
            }
            else {
                // Wipe existing order if a new restaurant is selected
                if( !restaurantId.equals(order.getRestaurantId())) {
                    order.setRestaurantId(restaurantId);
                    order.setRestaurant(restaurant);
                    order.getOrderItems().clear();
                    order.getOrderDiscounts().clear();
                }

                // Build new order item
                OrderItem orderItem = new OrderItem();
                orderItem.setMenuItemNumber(specialOffer.getNumber());
                orderItem.setMenuItemId(specialOfferId);
                orderItem.setMenuItemTitle(specialOffer.getTitle());
                orderItem.setAdditionalItems(itemChoices);
                orderItem.setQuantity(quantity);
                orderItem.setCost(specialOffer.getCost());

                // Add new order item to order and update
                order.addOrderItem(orderItem);
                order = orderRepository.saveOrder(order);

                // Update can checkout status of order
                session.setAttribute("cancheckout", order.getCanCheckout());

                // Return success
                model.put("success",true);
                model.put("applicable",true);
                model.put("order",order);
            }
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

            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order = null;
            if( orderId != null ) {
                order = orderRepository.findByOrderId(orderId);
                if( order != null ) {
                    order.removeOrderItem(orderItemId, 1);
                    order = orderRepository.saveOrder(order);
                    // Update can checkout status of order
                    session.setAttribute("cancheckout", order.getCanCheckout());
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
    @RequestMapping(value="/order/deliveryEdit.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> buildDeliveryEdit(@RequestParam(value = "orderId") String orderId) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Building delivery options for orderId: " + orderId);
        }

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            Order order = orderRepository.findByOrderId(orderId);
            Restaurant restaurant = order.getRestaurant();

            // Get opening times for today and the next three days
            DateTime currentTime = new DateTime();
            DateTime now = new DateTime(currentTime.getYear(), currentTime.getMonthOfYear(), currentTime.getDayOfMonth(), currentTime.getHourOfDay(), currentTime.getMinuteOfHour(), 0, 0);

            // Store if the restaurant is open for collection and delivery
            boolean isOpenForDelivery = restaurant.isOpenForDelivery(currentTime);
            boolean isOpenForCollection = restaurant.isOpenForCollection(currentTime);
            
            List<Integer> days = new ArrayList<Integer>();
            List<List<LocalTime>> deliveryTimes = new ArrayList<List<LocalTime>>();
            List<List<LocalTime>> collectionTimes = new ArrayList<List<LocalTime>>();

            // Get the remaining options for today first
            days.add(now.getDayOfWeek());
            DateTime deliveryOpeningTime = restaurant.getDeliveryOpeningTime(now);
            DateTime collectionOpeningTime = restaurant.getCollectionOpeningTime(now);

            // For delivery times push the current time past the delivery time in minutes
            int deliveryTimeMinutes = restaurant.getDeliveryTimeMinutes();
            Pair<List<LocalTime>,List<LocalTime>> deliveryTimesPair;
            if( deliveryOpeningTime != null ) {
                deliveryTimesPair = getTimeOptions(now.isBefore(deliveryOpeningTime)? deliveryOpeningTime.plusMinutes(deliveryTimeMinutes):
                        now.plusMinutes(deliveryTimeMinutes), restaurant.getDeliveryClosingTime(now));
            }
            else {
                deliveryTimesPair = getTimeOptions(deliveryOpeningTime, restaurant.getDeliveryClosingTime(now));
            }

            // For collection times push the current time past the collection time in minutes
            int collectionTimeMinutes = restaurant.getCollectionTimeMinutes();
            Pair<List<LocalTime>,List<LocalTime>> collectionTimesPair;
            if( collectionOpeningTime != null ) {
                collectionTimesPair = getTimeOptions(now.isBefore(collectionOpeningTime)? collectionOpeningTime.plusMinutes(collectionTimeMinutes):
                        now.plusMinutes(collectionTimeMinutes), restaurant.getCollectionClosingTime(now));            }
            else {
                collectionTimesPair = getTimeOptions(collectionOpeningTime, restaurant.getCollectionClosingTime(now));
            }

            deliveryTimes.add(deliveryTimesPair.first);
            collectionTimes.add(collectionTimesPair.first);

            // Now get the rest of the options for the remaining times
            for( int i = 0; i < 3; i++ ) {
                
                // Add any times after midnight from the previous list
                List<LocalTime> deliveryTimesList = new ArrayList<LocalTime>();
                List<LocalTime> collectionTimesList = new ArrayList<LocalTime>();
                deliveryTimesList.addAll(deliveryTimesPair.second);
                collectionTimesList.addAll(collectionTimesPair.second);
                                
                // Now get the next set of opening and closing times
                now = now.plusDays(1);
                days.add(now.getDayOfWeek());

                DateTime[] openingAndClosingTimes = restaurant.getOpeningAndClosingTimes(now);
                deliveryTimesPair = getTimeOptions(openingAndClosingTimes[2], openingAndClosingTimes[3]);
                collectionTimesPair = getTimeOptions(openingAndClosingTimes[0], openingAndClosingTimes[1]);
                
                // Add this day's time options to the list
                deliveryTimesList.addAll(deliveryTimesPair.first);
                collectionTimesList.addAll(collectionTimesPair.first);
                
                // Add these lists to the return
                deliveryTimes.add(deliveryTimesList);
                collectionTimes.add(collectionTimesList);
            }
            
            // Add the time options to the model
            model.put("days", days);
            model.put("deliveryTimes", deliveryTimes);
            model.put("collectionTimes", collectionTimes);
            model.put("openForDelivery", isOpenForDelivery);
            model.put("openForCollection", isOpenForCollection);
            model.put("success",true);
            
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return buildOrderResponse(model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/updateOrderDelivery.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> updateOrderDelivery(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating order delivery type");
        }
        
        Map<String,Object> model = new HashMap<String, Object>();

        try {
            // Extract request parameters
            Map<String,Object> params = (Map<String,Object>)jsonUtils.deserialize(body);
            String orderId = (String)params.get("orderId");
            String deliveryType = (String)params.get("deliveryType");
            Integer dayOffset = Integer.valueOf((String)params.get("dayIndex"));
            String time = (String)params.get("time");
            
            // Get the order and update the delivery type
            Order order = orderRepository.findByOrderId(orderId);
            order.setDeliveryType(deliveryType);
            
            // Clear existing expected delivery/collection times
            order.setExpectedCollectionTime(null);
            order.setExpectedDeliveryTime(null);

            // If the selected time is 'ASAP' no need to do any processing
            if( !"ASAP".equals(time)) {
                DateTime now = new DateTime().plusDays(dayOffset);
                MutableDateTime expectedDate = new MutableDateTime(now.getYear(),now.getMonthOfYear(),now.getDayOfMonth(),0,0,0,0);
                Integer hour = Integer.valueOf(time.split(":")[0]);
                Integer minute = Integer.valueOf(time.split(":")[1]);
                expectedDate.setHourOfDay(hour);
                expectedDate.setMinuteOfHour(minute);
                if( Order.DELIVERY.equals(deliveryType)) {
                    order.setExpectedDeliveryTime(expectedDate.toDateTime());
                }
                else {
                    order.setExpectedCollectionTime(expectedDate.toDateTime());
                }
            }

            // Update order costs and save
            order.updateCosts();
            order = orderRepository.saveOrder(order);

            // Update can checkout status of order
            HttpSession session = request.getSession(true);
            session.setAttribute("cancheckout", order.getCanCheckout());

            // All worked ok
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

            // Update can checkout status of order
            session.setAttribute("cancheckout", order.getCanCheckout());

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
     * @param from
     * @param to
     * @return
     */

    private Pair<List<LocalTime>,List<LocalTime>> getTimeOptions(DateTime from, DateTime to) {
        List<LocalTime> first = new ArrayList<LocalTime>();
        List<LocalTime> second = new ArrayList<LocalTime>();
        if( from == null || to == null ) {
            return new Pair<List<LocalTime>, List<LocalTime>>(first,second);
        }
        LocalTime startTime = from.toLocalTime();
        int minuteInterval = from.getMinuteOfHour() % 15;
        from = from.plusMinutes(15 - minuteInterval);
        while(!from.isAfter(to)) {
            LocalTime nextTime = from.toLocalTime();
            if( !nextTime.isAfter(startTime)) {
                second.add(nextTime);
            }
            else {
                first.add(nextTime);
            }
            from = from.plusMinutes(15);
        }
        return new Pair<List<LocalTime>, List<LocalTime>>(first,second);
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
        order.updateCosts();
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

    private ResponseEntity<byte[]> buildOrderResponse(Map<String,Object> model) throws Exception {
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
