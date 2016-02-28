package com.scholarscore.util;

import java.util.HashMap;
import java.util.Map;

/**
 * User: jordan
 * Date: 2/28/16
 * Time: 3:48 PM
 */
public class MapUtil {

    // starting with an existing map, return a new map where the keys in the returned map are the values from the existing map,
    // and the values in the returned map are the keys from the existing map.
    public static <T,V> Map<T, V> buildReverseMap(Map<V, T> originalMap) {
        HashMap<T,V> toReturn = new HashMap<>();
        for (V key : originalMap.keySet()) {
            if (toReturn.containsKey(originalMap.get(key))) {
                // if, in the original hashmap, there are multiple records that have the same VALUE, 
                // this becomes a problem as we try to make these the key.
                throw new MapUtilException("CANNOT reverse map -- original map contains duplicate VALUE " + originalMap.get(key) + " which leads to a key collision");
            }
            toReturn.put(originalMap.get(key), key);
        }
        return toReturn;
    }
    
    static class MapUtilException extends RuntimeException { 
        public MapUtilException(String msg) { super(msg); }
    }

}
