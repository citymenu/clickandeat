package com.ezar.clickandeat.converter;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import flexjson.transformer.StringTransformer;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class NullIdStringTransformer extends StringTransformer implements ObjectFactory {

    final Map<String,String> escapeMap = new HashMap<String, String>();
    
    public NullIdStringTransformer() {
        escapeMap.put("\u200B","");
    }
    
    
    @Override
    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        if( ( value == null || "".equals(value) || "null".equals(value))) {
            return null;
        }
        else {
            String str = value.toString();
            for(Map.Entry<String,String> entry: escapeMap.entrySet()) {
                str = str.replaceAll(entry.getKey(),entry.getValue());
            }
            return str;
        }
    }
}

