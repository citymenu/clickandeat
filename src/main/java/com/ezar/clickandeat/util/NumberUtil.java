package com.ezar.clickandeat.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {

    private static final NumberFormat formatter;

    static {
        formatter = DecimalFormat.getInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
    }


    /**
     * @param in
     * @return
     */

    public static String format(Double in) {
        return in == null? "": formatter.format(in);
    }

}
