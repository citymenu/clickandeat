package com.ezar.clickandeat.converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.joda.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

public class LocalDateWriteConverter implements Converter<LocalDate, DBObject> {

    private final String timeZone;

    public LocalDateWriteConverter( String timeZone ) {
        this.timeZone = timeZone;
    }

    @Override
    public DBObject convert(LocalDate source) {
        DBObject dbo = new BasicDBObject();
        dbo.put("year", source.getYear());
        dbo.put("month", source.getMonthOfYear());
        dbo.put("day", source.getDayOfMonth());
        dbo.put("timeZone", timeZone );
        return dbo;
    }
}
