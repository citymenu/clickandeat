package com.ezar.clickandeat.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {

    private static final NumberFormat formatter;
    private static final NumberFormat paymentFormatter;

    static {
        formatter = DecimalFormat.getInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        paymentFormatter = DecimalFormat.getInstance();
        paymentFormatter.setMinimumFractionDigits(0);
        paymentFormatter.setMaximumFractionDigits(0);
        paymentFormatter.setGroupingUsed(false);
    }


    /**
     * @param in
     * @return
     */

    public static String format(Double in) {
        return in == null? "": formatter.format(in);
    }

    public static String formatForCardPayment(Double in) {
        return in == null? "": paymentFormatter.format(in * 100);
    }
    
}