package com.ezar.clickandeat.util;

import java.util.Map;

public class MapUtil {
    
    public static boolean getBooleanMapValue(Map<String,Object> map, String key) {
        Object obj = map.get(key);
        if( obj == null || !(obj instanceof Boolean)) {
            return false;
        }
        return (Boolean)obj;
    }
}
