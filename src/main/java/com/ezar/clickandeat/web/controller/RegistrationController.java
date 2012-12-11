package com.ezar.clickandeat.web.controller;

import com.ezar.clickandeat.model.Search;
import com.ezar.clickandeat.model.UserRegistration;
import com.ezar.clickandeat.model.Voucher;
import com.ezar.clickandeat.repository.OrderRepository;
import com.ezar.clickandeat.repository.UserRegistrationRepository;
import com.ezar.clickandeat.repository.VoucherRepository;
import com.ezar.clickandeat.util.JSONUtils;
import com.ezar.clickandeat.util.ResponseEntityUtils;
import com.ezar.clickandeat.web.controller.helper.Filter;
import com.ezar.clickandeat.web.controller.helper.FilterUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
public class RegistrationController {

    private static final Logger LOGGER = Logger.getLogger(RegistrationController.class);
    
    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private JSONUtils jsonUtils;

    @Autowired
    private ResponseEntityUtils responseEntityUtils;


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/admin/registrations/list.ajax", method = RequestMethod.GET )
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

        List<Filter> filters = FilterUtils.extractFilters(req);
        List<UserRegistration> registrations = userRegistrationRepository.page(request, filters);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("registrations",registrations);
        model.put("count",userRegistrationRepository.count(filters));
        return responseEntityUtils.buildResponse(model);

    }



    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/register/registerCustomer.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> registerLocation(HttpServletRequest request, @RequestParam(value="email") String email,
                                                   @RequestParam(value="discount") Double discount ) throws Exception {

        try {
            HttpSession session = request.getSession(true);
            Search search = (Search)session.getAttribute("search");
            String orderId = (String)session.getAttribute("orderid");
            UserRegistration userRegistration = new UserRegistration();
            userRegistration.setEmailAddress(email);
            userRegistration.setRequestedDiscount(discount);
            userRegistration.setRemoteIpAddress(request.getRemoteAddr());
            if( search != null ) {
                userRegistration.setLocation(search.getLocation());
            }
            if( orderId != null ) {
                userRegistration.setOrder(orderRepository.findByOrderId(orderId));
            }
            userRegistrationRepository.saveUserRegistration(userRegistration);
        }
        catch( Exception ex ) {
            LOGGER.error("",ex);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return new ResponseEntity<byte[]>("".getBytes("utf-8"), headers, HttpStatus.OK);    
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/admin/registrations/generateEmail.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> registerLocation(@RequestParam(value="registrationId") String registrationId ) throws Exception {
        
        Map<String,Object> model = new HashMap<String,Object>();
        
        try {
            UserRegistration userRegistration = userRegistrationRepository.findByRegistrationId(registrationId);
            Double discount = userRegistration.getRequestedDiscount();
            Voucher voucher = voucherRepository.createVoucher(discount);
            
            model.put("success",true);
            model.put("email",userRegistration.getEmailAddress());
            model.put("voucher",voucher.getVoucherId());
            model.put("discount",discount);
        }
        catch( Exception ex ) {
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return responseEntityUtils.buildResponse(model);
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value="/admin/registrations/markEmailSent.ajax", method = RequestMethod.POST )
    public ResponseEntity<byte[]> markEmailSent(@RequestParam(value="registrationId") String registrationId ) throws Exception {

        Map<String,Object> model = new HashMap<String,Object>();

        try {
            UserRegistration userRegistration = userRegistrationRepository.findByRegistrationId(registrationId);
            userRegistration.setEmailSent(true);
            userRegistrationRepository.saveUserRegistration(userRegistration);
            model.put("success",true);
        }
        catch( Exception ex ) {
            model.put("success",false);
            model.put("message",ex.getMessage());
        }
        return responseEntityUtils.buildResponse(model);
    }

}
