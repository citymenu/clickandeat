package com.ezar.clickandeat.converter;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import flexjson.transformer.StringTransformer;
import org.joda.time.LocalTime;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DoubleTransformer implements ObjectFactory {

    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if( ( value == null || "".equals(value) || "null".equals(value))) {
            return null;
        }
        else {
            return Double.valueOf(value.toString());
        }
    }
}

