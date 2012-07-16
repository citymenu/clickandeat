package com.ezar.clickandeat.converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

public class DateTimeWriteConverter implements Converter<DateTime, DBObject> {

    @Override
    public DBObject convert(DateTime source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("year", source.getYear());
        dbo.put("month", source.getMonthOfYear());
        dbo.put("day", source.getDayOfMonth());
        dbo.put("hour", source.getHourOfDay());
        dbo.put("minute", source.getMinuteOfHour());
        dbo.put("second", source.getSecondOfMinute());
        return dbo;
    }
}
