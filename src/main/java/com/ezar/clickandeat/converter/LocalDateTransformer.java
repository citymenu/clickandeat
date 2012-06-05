package com.ezar.clickandeat.converter;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;

public class LocalDateTransformer extends AbstractTransformer implements ObjectFactory {

    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

    @Override
    public void transform(Object object) {
        if( object == null ) {
            getContext().write("");
        }
        else {
            LocalDate localDate = (LocalDate)object;
            getContext().writeQuoted(formatter.print(localDate));
        }
    }


    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if( value == null) {
            return null;
        }
        String dateStr = value.toString();
        if( !StringUtils.hasText(dateStr)) {
            return null;
        }
        else {
            return formatter.parseLocalDate(dateStr);
        }
    }
}
