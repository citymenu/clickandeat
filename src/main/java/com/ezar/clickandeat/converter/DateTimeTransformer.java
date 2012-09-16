package com.ezar.clickandeat.converter;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;

public class DateTimeTransformer extends AbstractTransformer implements ObjectFactory {

    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    public void transform(Object object) {
        if( object == null ) {
            getContext().write("");
        }
        else {
            DateTime dateTime = (DateTime)object;
            getContext().writeQuoted(formatter.print(dateTime));
        }
    }


    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if( value == null) {
            return null;
        }
        String timeStr = value.toString();
        if( !StringUtils.hasText(timeStr)) {
            return null;
        }
        else {
            return formatter.parseDateTime(timeStr);
        }
    }
}
