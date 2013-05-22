package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.*;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.RestaurantRepository;
import com.ezar.clickandeat.repository.VoucherRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.Pair;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.util.SequenceGenerator;
import com.ezar.clickandeat.web.controller.helper.Filter;
import com.ezar.clickandeat.web.controller.helper.FilterUtils;
import com.ezar.clickandeat.web.controller.helper.FilterValueDecorator;
import com.ezar.clickandeat.workflow.OrderWorkflowEngine;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
public class OrderController {
    
    private static final Logger LOGGER = Logger.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OrderWorkflowEngine orderWorkflowEngine;
    
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;

    @Autowired
    private JSONUtils jsonUtils;

    private final Map<String,FilterValueDecorator> filterDecoratorMap = new HashMap<String, FilterValueDecorator>();

    private final String[] excludes = new String[]{"order.restaurant.name","order.restaurant.*"};
    
    public OrderController() {
        filterDecoratorMap.put("orderStatus",new FilterValueDecorator() {
            @Override
            public String[] decorateValues(String[] values) {
                List<String> ret = new ArrayList<String>();
                for( String value: values ) {
                    ret.add(("ORDER STATUS " + value).replaceAll(" ","_"));
                }
                return ret.toArray(new String[ret.size()]);
            }
        });
        filterDecoratorMap.put("orderNotificationStatus",new FilterValueDecorator() {
            @Override
            public String[] decorateValues(String[] values) {
                List<String> ret = new ArrayList<String>();
                for( String value: values ) {
                    ret.add(value.replaceAll(" ","_"));
                }
                return ret.toArray(new String[ret.size()]);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/admin/orders/list.ajax", method = RequestMethod.GET )
    public ResponseEntity<byte[]> list(@RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit,
                                       @RequestParam(value="sort", required = false) String sort,
                                       @RequestParam(value = "query", required = false) String query, HttpServletRequest req) throws Exception {

        PageRequest request;


        if( StringUtils.hasText(sort)) {
            List<Map<String,String>> sortParams = (List<Map<String,String>>)jsonUtils.deserialize(sort);
            Map<String,String> sortProperties = sortParams.get(0);
            String direction = sortProperties.get("direction");
            String property = sortProperties.get("property");
            request = new PageRequest(page - 1, limit, ( "ASC".equals(direction)? ASC : DESC ), property );
        }
        else {
            request = new PageRequest(page - 1, limit );
        }

        List<Filter> filters = FilterUtils.extractFilters(req,filterDecoratorMap);
        List<Order> orders = orderRepository.pageByOrderId(request,query,filters);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("orders",orders);
        model.put("count",orderRepository.count(query, filters));
        String[] excludes = new String[]{"orders.orderDiscounts"};
        return responseEntityUtils.buildResponse(model,excludes);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/admin/orders/acceptedOrders.ajax", method = RequestMethod.GET )
    public ResponseEntity<byte[]> listAcceptedOrders() throws Exception {
        List<Order> orders = orderRepository.findAllAcceptedOrders();
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("success",true);
        model.put("orders",orders);
        model.put("count",orders.size());
        String[] excludes = new String[]{"orders.orderDiscounts"};
        return responseEntityUtils.buildResponse(model,excludes);
    }


    @ResponseBody
    @RequestMapping(value="/admin/orders/export.html", method = RequestMethod.GET )
    public ResponseEntity<byte[]> export() throws Exception {
        Map<String,Object> model = new HashMap<String, Object>();
        List<Order> orders = orderRepository.export();
        model.put("orders",orders);
        XLSTransformer transformer = new XLSTransformer();
        Resource resource = new ClassPathResource("/template/OrderExport.xlsx");
        Workbook workbook = transformer.transformXLS(resource.getInputStream(), model);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set("Content-Disposition","attachment;Filename=OrderExport.xlsx");
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>(baos.toByteArray(), headers, HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value="/admin/orders/cancel.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> cancelOrder(@RequestParam(value = "orderId") String orderId) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();
        
        try {
            Order order = orderRepository.findByOrderId(orderId);
            orderWorkflowEngine.processAction(order, OrderWorkflowEngine.ACTION_SYSTEM_CANCELS);
            model.put("success",true);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return responseEntityUtils.buildResponse(model);
    }


    @ResponseBody
    @RequestMapping(value="/admin/orders/addOrderAmendment.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> addOrderAmendment(@RequestParam(value = "orderId") String orderId, @RequestParam(value = "description", required = false) String description,
                                                    @RequestParam(value = "restaurantCost") Double restaurantCost, @RequestParam(value = "totalCost", required = false) Double totalCost ) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            Order order = orderRepository.findByOrderId(orderId);
            
            // Build new amendment object
            OrderAmendment amendment = new OrderAmendment();
            amendment.setCreated(new DateTime());
            amendment.setDescription(description);
            amendment.setPreviousRestaurantCost(order.getRestaurantCost());
            amendment.setPreviousTotalCost(order.getTotalCost());
            amendment.setRestaurantCost(restaurantCost);
            amendment.setTotalCost(totalCost);
            
            // Add amendment to order and update order costs
            order.getOrderAmendments().add(amendment);
            order.setRestaurantCost(restaurantCost);
            order.setTotalCost(totalCost);
            orderRepository.saveOrder(order);
            model.put("success",true);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return responseEntityUtils.buildResponse(model);
    }


    @RequestMapping(value="/buildOrder.html", method = RequestMethod.GET )
    public ModelAndView get(HttpServletRequest request) {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Redirecting to current order details page");
        }

        HttpSession session = request.getSession(true);
        String orderrestaurantid = (String)session.getAttribute("orderrestaurantid");
        String restaurantid = (String)session.getAttribute("restaurantid");
        Search search = (Search)session.getAttribute("search");

        if( orderrestaurantid != null ) {
            restaurantid = orderrestaurantid;
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
            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantid);
            return new ModelAndView("redirect:/" + restaurant.getUrl());
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
            String restaurantId = (String)session.getAttribute("restaurantid");
            Order order = null;
            if( orderId != null ) {
                order = orderRepository.findByOrderId(orderId);

                // If the current order has no items but is linked to another restauarant, update it now
                if( order.getOrderItems().size() == 0 && !order.getRestaurantId().equals(restaurantId)) {
                    order.setRestaurant(restaurantRepository.findByRestaurantId(restaurantId));
                }
                order.updateCosts();

                // Update can checkout status of order
                session.setAttribute("cancheckout", order.getCanCheckout());
                session.setAttribute("cansubmitpayment", order.getCanSubmitPayment());
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
    @RequestMapping(value="/order/getCompletedOrder.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> getCompletedOrder(@RequestParam(value = "orderId") String orderId) throws Exception {
        Map<String,Object> model = new HashMap<String, Object>();
        Order order = orderRepository.findByOrderId(orderId);
        model.put("success",true);
        model.put("order",order);
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

            // Update order restaurant id session attribute if any items present
            if( order.getOrderItems().size() > 0 ) {
                session.setAttribute("orderrestaurantid", order.getRestaurantId());
                session.setAttribute("orderrestauranturl", order.getRestaurant().getUrl());
            } else {
                session.removeAttribute("orderrestaurantid");
                session.removeAttribute("orderrestauranturl");
            }

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
            List<String> itemChoiceCosts = (List<String>)params.get("itemChoiceCosts");
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
                double additionalCost = 0d;
                for( String itemChoiceCost: itemChoiceCosts ) {
                    additionalCost += Double.valueOf(itemChoiceCost);
                }
                orderItem.setCost(specialOffer.getCost() + additionalCost);

                // Add new order item to order and update
                order.addOrderItem(orderItem);
                order = orderRepository.saveOrder(order);

                // Update can checkout status of order
                session.setAttribute("cancheckout", order.getCanCheckout());

                // Update order restaurant id session attribute if any items present
                if( order.getOrderItems().size() > 0 ) {
                    session.setAttribute("orderrestaurantid", order.getRestaurantId());
                    session.setAttribute("orderrestauranturl", order.getRestaurant().getUrl());
                } else {
                    session.removeAttribute("orderrestaurantId");
                    session.removeAttribute("orderrestauranturl");
                }

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
    @RequestMapping(value="/order/updateItemQuantity.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> updateItemQuantity(HttpServletRequest request, @RequestParam(value = "body") String body ) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating order items: " + body);
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
                    order.updateItemQuantity(orderItemId, quantity);
                    order = orderRepository.saveOrder(order);
                    // Update can checkout status of order
                    session.setAttribute("cancheckout", order.getCanCheckout());

                    // Update order restaurant id session attribute if any items present
                    if( order.getOrderItems().size() > 0 ) {
                        session.setAttribute("orderrestaurantid", order.getRestaurantId());
                        session.setAttribute("orderrestauranturl", order.getRestaurant().getUrl());
                    } else {
                        // If the restaurant session id does not match the order restaurant id, update the order
                        String restaurantId = (String)session.getAttribute("restaurantid");
                        if( !order.getRestaurantId().equals(restaurantId)) {
                            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
                            order.setRestaurant(restaurant);
                            order = orderRepository.save(order);
                        }
                        session.removeAttribute("orderrestaurantid");
                        session.removeAttribute("orderrestauranturl");
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


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/applyVoucher.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> applyVoucher( HttpServletRequest request, @RequestParam(value = "voucherId") String voucherId) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Applying voucher to order");
        }

        Map<String,Object> model = new HashMap<String, Object>();
        voucherId = voucherId == null? "": voucherId.trim();

        try {

            boolean success = true;
            String reason = null;

            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order = null;
            if( orderId != null ) {
                order = orderRepository.findByOrderId(orderId);
            }

            if( order != null ) {
                if( order.getVoucher() != null ) {
                    success = false;
                    reason = "voucher-already-applied";
                }
                else {
                    Voucher voucher = voucherRepository.findByVoucherId(voucherId);
                    if( voucher == null ) {
                        success = false;
                        reason = "voucher-not-found";
                    }
                    else {
                        if( voucher.isUsed()) {
                            success = false;
                            reason = "voucher-already-used";
                        }
                        else {
                            order.setVoucher(voucher);
                            order = orderRepository.saveOrder(order);
                        }
                    }
                }
            }

            // Return processed status
            model.put("success",success);
            model.put("order",order);
            model.put("reason",reason);

        }
        catch(Exception ex ) {
            LOGGER.error("",ex);
            model.put("success",false);
            model.put("reason","error");
            model.put("message",ex.getMessage());
        }
        return buildOrderResponse(model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/order/removeVoucher.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> removeVoucher(HttpServletRequest request) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing voucher from order");
        }

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            HttpSession session = request.getSession(true);
            String orderId = (String)session.getAttribute("orderid");
            Order order = null;
            if( orderId != null ) {
                order = orderRepository.findByOrderId(orderId);
            }

            if( order != null && order.getVoucher() != null ) {
                order.setVoucherId(null);
                order.setVoucher(null);
                orderRepository.saveOrder(order);
                order = orderRepository.findByOrderId(orderId);
            }

            // Return processed status
            model.put("order",order);
            model.put("success",true);
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
    @RequestMapping(value="/order/updateAdditionalInstructions.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> updateAdditionalInstructions(@RequestParam(value = "orderId") String orderId,
                                                    @RequestParam(value = "additionalInstructions") String additionalInstructions) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating additional instructions for orderId: " + orderId);
        }

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            Order order = orderRepository.findByOrderId(orderId);
            order.setAdditionalInstructions(additionalInstructions);
            order = orderRepository.saveOrder(order);
            model.put("success",true);
            model.put("order",order);
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
    @RequestMapping(value="/order/clearOrder.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> clearOrder(@RequestParam(value = "orderId") String orderId,
                                                               @RequestParam(value = "restaurantId") String restaurantId) throws Exception {

        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Clearing order for orderId: " + orderId);
        }

        Map<String,Object> model = new HashMap<String, Object>();

        try {
            Order order = orderRepository.findByOrderId(orderId);
            Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
            order.setRestaurant(restaurant);
            order.getOrderItems().clear();
            order.getOrderDiscounts().clear();
            order = orderRepository.saveOrder(order);
            model.put("success",true);
            model.put("order",order);
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

            // Store if the restaurant is currently open
            boolean isOpen = restaurant.isOpen(currentTime);

            List<Integer> days = new ArrayList<Integer>();
            List<Set<LocalTime>> deliveryTimes = new ArrayList<Set<LocalTime>>();
            List<Set<LocalTime>> collectionTimes = new ArrayList<Set<LocalTime>>();

            // Get the remaining options for today first
            days.add(now.getDayOfWeek());
            DateTime[] openingAndClosingTimes = restaurant.getOpeningAndClosingTimes(now);
            DateTime earlyOpeningTime = openingAndClosingTimes[0] == null? now :  openingAndClosingTimes[0];
            DateTime lateOpeningTime = openingAndClosingTimes[2] == null? now :  openingAndClosingTimes[2];

            // For delivery times push the current time past the delivery time in minutes
            int deliveryTimeMinutes = restaurant.getDeliveryTimeMinutes();
            Pair<Set<LocalTime>,Set<LocalTime>> earlyDeliveryTimesPair = getTimeOptions(now.isBefore(earlyOpeningTime)? earlyOpeningTime: now, openingAndClosingTimes[1], deliveryTimeMinutes);
            Pair<Set<LocalTime>,Set<LocalTime>> lateDeliveryTimesPair = getTimeOptions(now.isBefore(lateOpeningTime)? lateOpeningTime: now, openingAndClosingTimes[3], deliveryTimeMinutes);
            earlyDeliveryTimesPair.first.addAll(lateDeliveryTimesPair.first);
            earlyDeliveryTimesPair.second.addAll(lateDeliveryTimesPair.second);

            // For delivery times push the current time past the collection time in minutes
            int collectionTimeMinutes = restaurant.getCollectionTimeMinutes();
            Pair<Set<LocalTime>,Set<LocalTime>> earlyCollectionTimesPair = getTimeOptions(now.isBefore(earlyOpeningTime)? earlyOpeningTime: now, openingAndClosingTimes[1], collectionTimeMinutes);
            Pair<Set<LocalTime>,Set<LocalTime>> lateCollectionTimesPair = getTimeOptions(now.isBefore(lateOpeningTime)? lateOpeningTime: now, openingAndClosingTimes[3], collectionTimeMinutes);
            earlyCollectionTimesPair.first.addAll(lateCollectionTimesPair.first);
            earlyCollectionTimesPair.second.addAll(lateCollectionTimesPair.second);

            // Add today's opening times
            deliveryTimes.add(earlyDeliveryTimesPair.first);
            collectionTimes.add(earlyCollectionTimesPair.first);

            // Now get the rest of the options for the remaining times
            for( int i = 0; i < 3; i++ ) {
                
                // Add any times after midnight from the previous list
                Set<LocalTime> deliveryTimesSet = new TreeSet<LocalTime>();
                Set<LocalTime> collectionTimesSet = new TreeSet<LocalTime>();
                deliveryTimesSet.addAll(earlyDeliveryTimesPair.second);
                collectionTimesSet.addAll(earlyCollectionTimesPair.second);

                // Now get the next set of opening and closing times
                now = now.plusDays(1);
                days.add(now.getDayOfWeek());

                openingAndClosingTimes = restaurant.getOpeningAndClosingTimes(now);
                earlyDeliveryTimesPair = getTimeOptions(openingAndClosingTimes[0], openingAndClosingTimes[1], deliveryTimeMinutes);
                lateDeliveryTimesPair = getTimeOptions(openingAndClosingTimes[2], openingAndClosingTimes[3], deliveryTimeMinutes);
                earlyDeliveryTimesPair.first.addAll(lateDeliveryTimesPair.first);
                earlyDeliveryTimesPair.second.addAll(lateDeliveryTimesPair.second);

                earlyCollectionTimesPair = getTimeOptions(openingAndClosingTimes[0], openingAndClosingTimes[1], collectionTimeMinutes);
                lateCollectionTimesPair = getTimeOptions(openingAndClosingTimes[2], openingAndClosingTimes[3], collectionTimeMinutes);
                earlyCollectionTimesPair.first.addAll(lateCollectionTimesPair.first);
                earlyCollectionTimesPair.second.addAll(lateCollectionTimesPair.second);

                // Add this day's time options to the list
                deliveryTimesSet.addAll(earlyDeliveryTimesPair.first);
                collectionTimesSet.addAll(earlyCollectionTimesPair.first);
                
                // Add these lists to the return
                deliveryTimes.add(deliveryTimesSet);
                collectionTimes.add(collectionTimesSet);
            }
            
            // Add the time options to the model
            model.put("days", days);
            model.put("deliveryTimes", deliveryTimes);
            model.put("collectionTimes", collectionTimes);
            model.put("collectionOnly", restaurant.getCollectionOnly());
            model.put("open", isOpen);
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

            order = orderRepository.saveOrder(order);

            // Update order restaurant id session attribute if any items present
            HttpSession session = request.getSession(true);
            if( order.getOrderItems().size() > 0 ) {
                session.setAttribute("orderrestaurantid", order.getRestaurantId());
                session.setAttribute("orderrestauranturl", order.getRestaurant().getUrl());
            } else {
                session.removeAttribute("orderrestaurantId");
                session.removeAttribute("orderrestauranturl");
            }
            
            // Update can checkout status of order
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

                    // Update order restaurant id session attribute if any items present
                    if( order.getOrderItems().size() > 0 ) {
                        session.setAttribute("orderrestaurantid", order.getRestaurantId());
                        session.setAttribute("orderrestauranturl", order.getRestaurant().getUrl());
                    } else {
                        session.removeAttribute("orderrestaurantid");
                        session.removeAttribute("orderrestauranturl");
                    }

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


    /**
     * @param from
     * @param to
     * @param offsetMinutes
     * @return
     */

    private Pair<Set<LocalTime>,Set<LocalTime>> getTimeOptions(DateTime from, DateTime to, int offsetMinutes ) {
        Set<LocalTime> first = new TreeSet<LocalTime>();
        Set<LocalTime> second = new TreeSet<LocalTime>();
        if( from == null || to == null ) {
            return new Pair<Set<LocalTime>, Set<LocalTime>>(first,second);
        }
        LocalTime startTime = from.toLocalTime();
        from = from.plusMinutes(offsetMinutes);
        int minuteInterval = from.getMinuteOfHour() % 15;
        from = from.plusMinutes(minuteInterval == 0? 0: 15 - minuteInterval);
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
        return new Pair<Set<LocalTime>, Set<LocalTime>>(first,second);
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

    private ResponseEntity<byte[]> buildOrderResponse(Map<String,Object> model) throws Exception {
        return responseEntityUtils.buildResponse(model,excludes);
    }

}
