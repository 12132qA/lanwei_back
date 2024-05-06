package org.example.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * 这个工具类有问题
 * */

public class DateUtil {

    /**
     * 返回月份
     * */
    public static String format(Date date,String format) throws ParseException {
//        int year = date.getYear()+1900;
        int month = date.getMonth();
        return Integer.toString(month);
//        System.out.println("year: "+year);
//        String s = Integer.toString(year) + (month<=9?"0"+month:month);

//        System.out.println(s);
//        DateFormat dateFormat = null;
//
//        String pattern = "yyyyMM";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        Date parse = simpleDateFormat.parse(s);
//        System.out.println(parse);

//        return s;
    }
}
