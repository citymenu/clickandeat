package com.ezar.clickandeat.converter;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

import java.lang.reflect.Type;

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

