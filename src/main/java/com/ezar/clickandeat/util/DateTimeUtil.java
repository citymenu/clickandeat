package com.ezar.clickandeat.util;

import com.ezar.clickandeat.config.MessageFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class DateTimeUtil {

    private static DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");

    private static Locale locale;

    static {
        String[] localeArray = MessageFactory.getLocaleString().split("_");
        locale = new Locale(localeArray[0],localeArray[1]);
    }
    
    /**
     * @param orderDateTime
     * @return
     */
    
    public static String formatOrderDate(DateTime orderDateTime) {
        if( orderDateTime == null ) {
            return MessageFactory.getMessage("weekday.today", false) + " - " + MessageFactory.getMessage("time.asap",false);    
        }
        StringBuilder sb = new StringBuilder();
        LocalDate today = new LocalDate();
        LocalDate orderDate = orderDateTime.toLocalDate();
        if( orderDate.equals(today)) {
            sb.append(MessageFactory.getMessage("weekday.today", false));
        }
        else {
            sb.append(orderDate.dayOfWeek().getAsText(locale));
        }
        sb.append(" - ");
        sb.append(timeFormat.print(orderDateTime));
        return sb.toString();
    }
    
    
}
