package com.ezar.clickandeat.converter;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;

public class LocalTimeTransformer extends AbstractTransformer implements ObjectFactory {

    private final DateTimeFormatter formatter;

    public LocalTimeTransformer( String timeZone ) {
        this.formatter = DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.forID(timeZone));
    }
    
    @Override
    public void transform(Object object) {
        if( object == null ) {
            getContext().write("");
        }
        else {
            LocalTime localTime = (LocalTime)object;
            getContext().writeQuoted(formatter.print(localTime));
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
            return formatter.parseLocalTime(timeStr);
        }
    }
}
