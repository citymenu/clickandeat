package com.ezar.clickandeat.converter;

import com.mongodb.DBObject;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.chrono.ISOChronology;
import org.springframework.core.convert.converter.Converter;

import java.util.TimeZone;

public class LocalTimeReadConverter implements Converter<DBObject,LocalTime> {

    @Override
    public LocalTime convert(DBObject source) {
        int hour = (Integer)source.get("hour");
        int minute = (Integer)source.get("minute");
        return new LocalTime(hour, minute);
    }
}
