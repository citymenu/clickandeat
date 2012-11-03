package com.ezar.clickandeat.web.controller.helper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class FilterUtils {

    /**
     * @param request
     * @return
     */

    public static List<Filter> extractFilters(HttpServletRequest request) {

        List<Filter> filters = new ArrayList<Filter>();
        int index = 0;
        String fieldName = request.getParameter("filter[" + index + "][field]");
        while (fieldName != null) {
            Filter filter = new Filter();
            filter.setField(fieldName);
            filter.setType(request.getParameter("filter[" + index + "][data][type]"));
            filter.setComparison(request.getParameter("filter[" + index + "][data][comparison]"));
            filter.setValues(request.getParameterValues("filter[" + index + "][data][value]"));
            filters.add(filter);
            fieldName = request.getParameter("filter[" + ++index + "][field]");
        }
        return filters;
    }

}
