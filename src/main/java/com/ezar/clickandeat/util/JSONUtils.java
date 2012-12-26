package com.ezar.clickandeat.util;

import com.ezar.clickandeat.converter.*;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class JSONUtils {
    
    private final ConcurrentMap<Class,JSONDeserializer> deserializerMap = new ConcurrentHashMap<Class, JSONDeserializer>();
    
    private JSONSerializer serializer;

    private JSONSerializer htmlEscapingSerializer;

    private JSONDeserializer deserializer = new JSONDeserializer();

    private final Map<String,String> escapeMap = new HashMap<String,String>();

    public JSONUtils() {
        this.serializer = new JSONSerializer()
                .transform(new DateTimeTransformer(), DateTime.class)
                .transform(new LocalDateTransformer(), LocalDate.class)
                .transform(new LocalTimeTransformer(), LocalTime.class)
                .transform(new NullIdStringTransformer(), String.class)
                .exclude("restaurants.menu")
                .exclude("restaurants.specialOffers")
                .exclude("restaurants.discounts");

        this.htmlEscapingSerializer = new JSONSerializer()
                .transform(new DateTimeTransformer(), DateTime.class)
                .transform(new LocalDateTransformer(), LocalDate.class)
                .transform(new LocalTimeTransformer(), LocalTime.class)
                .transform(new NullIdHtmlEscapingStringTransformer(), String.class);

        escapeMap.put("'","###");
    }


    /**
     * @param excludes
     * @return
     */

    public JSONSerializer buildSerializer(String ... excludes ) {
        return new JSONSerializer()
                .transform(new DateTimeTransformer(), DateTime.class)
                .transform(new LocalDateTransformer(), LocalDate.class)
                .transform(new LocalTimeTransformer(), LocalTime.class)
                .transform(new NullIdStringTransformer(), String.class)
                .exclude(excludes);
    }
    
    
    /**
     * @param obj
     * @return
     */

    public String serialize(Object obj) {
        return serializer.deepSerialize(obj);
    }


    /**
     * @param obj
     * @return
     */
    
    public String serializeAndEscape(Object obj) {
        return escapeQuotes(serializer.deepSerialize(obj));
    }


    /**
     * @param serializer
     * @param obj
     * @return
     */

    public String serializeAndEscape(JSONSerializer serializer, Object obj) {
        return escapeQuotes(serializer.deepSerialize(obj));
    }


    /**
     * @param obj
     * @return
     */

    public String serializeAndEscapeHtml(Object obj) {
        return escapeQuotes(htmlEscapingSerializer.deepSerialize(obj));
    }


    /**
     * @param klass
     * @param json
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Class<T> klass, String json) {
        JSONDeserializer<T> deserializer = deserializerMap.get(klass);
        if( deserializer == null ) {
            deserializer = new JSONDeserializer<T>()
                    .use(DateTime.class, new DateTimeTransformer())
                    .use(LocalDate.class, new LocalDateTransformer())
                    .use(LocalTime.class, new LocalTimeTransformer())
                    .use(String.class, new NullIdStringTransformer())
                    .use(Double.class, new DoubleTransformer())
                    .use(Integer.class, new IntegerTransformer());
            deserializerMap.put(klass,deserializer);
        }
        return deserializer.deserialize(json);        
    }


    /**
     * @param json
     * @return
     */

    public Object deserialize(String json) {
        return deserializer.deserialize(json);
    }
    
    /**
     * @param json
     * @return
     */
    
    public String escapeQuotes(String json) {
        for(Map.Entry<String,String> entry: escapeMap.entrySet()) {
            json = json.replaceAll(entry.getKey(),entry.getValue());
        }
        return json;
    }

}
