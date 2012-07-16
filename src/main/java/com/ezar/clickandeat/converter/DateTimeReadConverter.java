package com.ezar.clickandeat.converter;

import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

public class DateTimeReadConverter implements Converter<DBObject,DateTime> {

    @Override
    public DateTime convert(DBObject source) {
        int year = (Integer)source.get("year");
        int month = (Integer)source.get("month");
        int day = (Integer)source.get("day");
        int hour = (Integer)source.get("hour");
        int minute = (Integer)source.get("minute");
        int second = (Integer)source.get("second");
        return new DateTime(year,month,day,hour,minute,second);
    }
}
