package com.cobub.bigdata.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by weifeng on 2015/8/13.
 */
public class CacheInfo {

    public static Map<String,Set<String>> deviceConstant = new HashMap<String, Set<String>>();

    static {
        Set<String> values = new HashSet<String>();
        values.add("");
        deviceConstant.put("devicename",values);
    }

}
