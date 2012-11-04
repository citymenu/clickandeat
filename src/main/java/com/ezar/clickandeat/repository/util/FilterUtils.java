package com.ezar.clickandeat.repository.util;

import com.ezar.clickandeat.web.controller.helper.Filter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;


public class FilterUtils {

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");


    /**
     * @param query
     * @param filters
     */

    public static void applyFilters(Query query, List<Filter> filters) {

        for (Filter filter : filters) {
            String fieldName = filter.getField();
            String type = filter.getType();
            List<String> comparisons = filter.getComparisons();
            List<String> values = filter.getValues();
            
            if (type.equals("list")) {
                query.addCriteria(where(fieldName).in(StringUtils.delimitedListToStringArray(values.get(0),"#")));
            }
            else if (type.equals("boolean")) {
                query.addCriteria(where(fieldName).is(Boolean.valueOf(filter.getValues().get(0))));
            }
            else if (type.equals("string")) {
                query.addCriteria(where(fieldName).regex(filter.getValues().get(0),"i"));
            }
            else if (type.equals("numeric")) {
                List<Criteria> criteriaList = new ArrayList<Criteria>();
                for( int i = 0; i < comparisons.size(); i++ ) {
                    String comparison = comparisons.get(i);
                    Double value = Double.valueOf(values.get(i));
                    if (comparison.equals("gt")) {
                        criteriaList.add(new Criteria(fieldName).gt(value));
                    }
                    else if (comparison.equals("lt")) {
                        criteriaList.add(new Criteria(fieldName).lt(value));
                    }
                    else {
                        criteriaList.add(new Criteria(fieldName).is(value));
                    }
                }
                if( criteriaList.size() == 1 ) {
                    query.addCriteria(criteriaList.get(0));
                }
                else {
                    query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
                }
            }
            else if (type.equals("date")) {
                List<Criteria> criteriaList = new ArrayList<Criteria>();
                for( int i = 0; i < comparisons.size(); i++ ) {
                    String comparison = comparisons.get(i);
                    DateTime dt = formatter.parseDateTime(values.get(i));
                    if (comparison.equals("gt")) {
                        criteriaList.add(new Criteria(fieldName + ".millis").gt(dt.getMillis()));
                    }
                    else if (comparison.equals("lt")) {
                        criteriaList.add(new Criteria(fieldName + ".millis").lt(dt.getMillis()));
                    }
                    else {
                        criteriaList.add(new Criteria(fieldName + ".millis").gte(dt.getMillis()));
                        criteriaList.add(new Criteria(fieldName + ".millis").lte(dt.plusDays(1).getMillis()));
                    }
                }
                if( criteriaList.size() == 1 ) {
                    query.addCriteria(criteriaList.get(0));
                }
                else {
                    query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
                }
            }
        }
    }

    
}
