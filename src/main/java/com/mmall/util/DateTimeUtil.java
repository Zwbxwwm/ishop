package com.mmall.util;

import com.mmall.common.ServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {
    public static  final  String  STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";


    public static Date strToDate(String dataTimeStr,String formaStr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(formaStr);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dataTimeStr);
        return  dateTime.toDate();
    }
    public static String dateToStr(Date date,String formaStr){
        if(date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formaStr);
    }


    public static Date strToDate(String dataTimeStr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dataTimeStr);
        return  dateTime.toDate();
    }
    public static String dateToStr(Date date){
        if(date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
