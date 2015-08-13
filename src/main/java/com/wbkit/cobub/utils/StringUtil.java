package com.wbkit.cobub.utils;

import com.wbkit.cobub.json.JSONObject;

public class StringUtil {

  public static String hdfs2Hive(String[] strs, JSONObject jsonObj) {
    StringBuilder sb = new StringBuilder();

    for (String str : strs) {
      sb.append(jsonObj.optString(str));
      sb.append("\001");
    }
    sb.delete(sb.lastIndexOf("\001"), sb.length());
    return sb.toString();
  }

  public static Integer str2Int(String value,Integer def) {
    Integer intValue = def;
    if (value != null && !"".equals(value.trim())){
      try {
        intValue = Integer.valueOf(value.trim());
      } catch (NumberFormatException e){
        e.printStackTrace();
      }
    }
    return intValue;
  }

}
