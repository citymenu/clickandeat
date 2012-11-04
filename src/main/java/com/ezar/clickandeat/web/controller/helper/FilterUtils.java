package com.ezar.clickandeat.web.controller.helper;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterUtils {


    /**
     * @param request
     * @return
     */

    public static List<Filter> extractFilters(HttpServletRequest request) {
        return extractFilters(request, new HashMap<String,FilterValueDecorator>());
    }
    
    
    /**
     * @param request
     * @return
     */

    public static List<Filter> extractFilters(HttpServletRequest request,Map<String,FilterValueDecorator> decoratorMap ) {

        Map<String,Filter> filters = new HashMap<String,Filter>();
        int index = 0;
        String fieldName = request.getParameter("filter[" + index + "][field]");
        while (fieldName != null) {
            Filter filter = filters.get(fieldName);
            if( filter == null ) {
                filter = new Filter();
                filter.setField(fieldName);
                filter.setType(request.getParameter("filter[" + index + "][data][type]"));
                filters.put(fieldName, filter);
            }

            filter.getComparisons().add(request.getParameter("filter[" + index + "][data][comparison]"));

            String[] values = request.getParameterValues("filter[" + index + "][data][value]"); 
            FilterValueDecorator decorator = decoratorMap.get(fieldName);
            if( decorator != null ) {
                values = decorator.decorateValues(values);
            }
            filter.getValues().add(StringUtils.arrayToDelimitedString(values,"#"));
            fieldName = request.getParameter("filter[" + ++index + "][field]");
        }
        return new ArrayList<Filter>(filters.values());
    }

}
