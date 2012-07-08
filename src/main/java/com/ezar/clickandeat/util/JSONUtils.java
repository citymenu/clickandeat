package com.ezar.clickandeat.util;

import com.ezar.clickandeat.converter.*;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JSONUtils {
    
    private static final ConcurrentMap<Class,JSONDeserializer> DESERIALIZER_MAP = new ConcurrentHashMap<Class, JSONDeserializer>();
    
    private static final JSONSerializer SERIALIZER = new JSONSerializer()
            .transform(new LocalDateTransformer(), LocalDate.class)
            .transform(new LocalTimeTransformer(), LocalTime.class)
            .transform(new NullIdStringTransformer(), String.class);

    private static final JSONDeserializer DESERIALIZER = new JSONDeserializer();

    private static final Map<String,String> ESCAPE_MAP = new HashMap<String,String>();
    
    static {
        ESCAPE_MAP.put("'", "###");
    }
    

    /**
     * @param obj
     * @return
     */
    
    public static String serialize(Object obj) {
        return escapeQuotes(SERIALIZER.deepSerialize(obj));
    }

    
    /**
     * @param klass
     * @param json
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Class<T> klass, String json) {
        JSONDeserializer<T> deserializer = DESERIALIZER_MAP.get(klass);
        if( deserializer == null ) {
            deserializer = new JSONDeserializer<T>()
                    .use(LocalDate.class, new LocalDateTransformer())
                    .use(LocalTime.class, new LocalTimeTransformer())
                    .use(String.class, new NullIdStringTransformer())
                    .use(Double.class, new DoubleTransformer())
                    .use(Integer.class, new IntegerTransformer());
            DESERIALIZER_MAP.put(klass,deserializer);
        }
        return deserializer.deserialize(json);        
    }


    /**
     * @param json
     * @return
     */

    public static Object deserialize(String json) {
        return DESERIALIZER.deserialize(json);
    }
    
    /**
     * @param json
     * @return
     */
    
    private static String escapeQuotes(String json) {
        for(Map.Entry<String,String> entry: ESCAPE_MAP.entrySet()) {
            json = json.replaceAll(entry.getKey(),entry.getValue());
        }
        return json;
    }
    
}
