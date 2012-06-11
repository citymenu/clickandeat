package com.ezar.clickandeat.converter;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;

public class NullIdStringTransformer extends AbstractTransformer implements ObjectFactory {

    @Override
    public void transform(Object object) {
        if( object == null ) {
            getContext().write("null");
        }
        else {
            getContext().writeQuoted(object.toString());
        }
    }


    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        return( value == null || "".equals(value) || "null".equals(value))? null: value.toString();
    }
}
