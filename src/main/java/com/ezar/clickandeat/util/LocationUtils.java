package com.ezar.clickandeat.util;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.geo.Metrics;

import java.util.Arrays;

public class LocationUtils {

    private static final Logger LOGGER = Logger.getLogger(LocationUtils.class);

    private static final double DIVISOR = Metrics.KILOMETERS.getMultiplier();


    /**
     * @param location1
     * @param location2
     * @return
     */

    public static double getDistance(double[] location1, double[] location2 ) {
        if( LOGGER.isDebugEnabled()) {
            LOGGER.debug("Determining distance between locations " +
                    Arrays.toString(location1) + " and " + Arrays.toString(location2));
        }

        double dLon = Math.toRadians(location1[0] - location2[0]);
        double dLat = Math.toRadians(location1[1]-location2[1]);

        double lat1 = Math.toRadians(location1[1]);
        double lat2 = Math.toRadians(location2[1]);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) *
                        Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return DIVISOR * c;
    }

}
