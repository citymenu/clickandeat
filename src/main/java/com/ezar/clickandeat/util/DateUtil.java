package com.ezar.clickandeat.util;

import com.ezar.clickandeat.config.MessageFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
    
    private static final List<LocalDate> bankHolidays;
    
    static {
        bankHolidays = new ArrayList<LocalDate>();
        String holidayText = MessageFactory.getMessage("config.bankHolidays", false);
        for( String holiday: StringUtils.commaDelimitedListToStringArray(holidayText)) {
            bankHolidays.add(formatter.parseLocalDate(holiday));
        }
    }


    /**
     * @param dt
     * @return
     */

    public static boolean isBankHoliday(LocalDate dt) {
        return dt != null && bankHolidays.contains(dt);
    }
}
