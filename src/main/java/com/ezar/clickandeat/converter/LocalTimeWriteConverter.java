package com.ezar.clickandeat.converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.joda.time.LocalTime;
import org.springframework.core.convert.converter.Converter;

public class LocalTimeWriteConverter implements Converter<LocalTime, DBObject> {

    private final String timeZone;
    
    public LocalTimeWriteConverter( String timeZone ) {
        this.timeZone = timeZone;
    }
    
    @Override
    public DBObject convert(LocalTime source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("hour", source.getHourOfDay());
        dbo.put("minute", source.getMinuteOfHour());
        dbo.put("timeZone", timeZone);
        return dbo;
    }
}
