package com.jobeth.common.util;

import java.time.LocalDate;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/30 20:51:51
 * Description: -
 */
public class DateUtils {
    public static void main(String[] args) {
        getDateAfter640(formatDate("20170306"));


        getDateAfter640(formatDate("20190819"));
    }

    /**
     * 20220429 => 2022-04-29
     * @param date date
     * @return String
     */
    public static String formatDate(String date){
        StringBuilder builder = new StringBuilder(date);
        builder.insert(4,"-");
        builder.insert(7,"-");
        return builder.toString();
    }

    /**
     * 640个交易日（没算周末）
     * @return getDateAfter640
     */
    public static String getDateAfter640(String date){
        long day = 640 + 256;
        LocalDate parse = LocalDate.parse(date);
        System.out.println(parse);
        LocalDate afterDay = parse.plusDays(day);
        System.out.println("640个交易日："+afterDay);


        return null;
    }
}
