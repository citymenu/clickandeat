package com.ezar.clickandeat.converter;

import com.mongodb.DBObject;
import org.joda.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

public class LocalDateReadConverter implements Converter<DBObject,LocalDate> {

    @Override
    public LocalDate convert(DBObject source) {
        int year = (Integer)source.get("year");
        int month = (Integer)source.get("month");
        int day = (Integer)source.get("day");
        return new LocalDate(year,month,day);
    }
}
