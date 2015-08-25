package com.wbkit.cobub.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

  private final static SimpleDateFormat sdf_d = new SimpleDateFormat(
      "yyyyMMdd");
  private final static SimpleDateFormat sdf_dh = new SimpleDateFormat(
          "yyyyMMddHH");
  
  public static synchronized Date str2Date(String date) {
    Date d = null;
    try {
      d =  sdf_d.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return d;
  }
  
  public static synchronized String dateFormat(Date date){
    return sdf_d.format(date);
  }
  
  public static int getGapDays(String s1, String s2){
    Date d1 = str2Date(s1);
    Date d2 = str2Date(s2);
    long days = (d1.getTime() - d2.getTime()) / (1000 * 3600 * 24);
    System.out.println(days);
    return (int)days;
  }
  
  public static boolean compareDate(String s1, String s2, Integer count){
    boolean flag = false;
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(str2Date(s1));
    cal2.setTime(str2Date(s2));
    cal2.add(Calendar.DAY_OF_MONTH, count);
    if (cal1.compareTo(cal2) == 1) {
      flag = true;
    }
    return flag;
  }
  
  public static String getDate(String date, Integer days)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(str2Date(date));
    cal.add(Calendar.DAY_OF_YEAR, days);
    return dateFormat(cal.getTime());
  }
  public static synchronized String formatStrDate(String date){
    Calendar cal = Calendar.getInstance();
    cal.setTime(str2Date(date));
    return sdf_dh.format(cal.getTime());
  }

}
