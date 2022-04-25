package com.jobeth.util;

import com.jobeth.enums.IndexEnum;
import com.jobeth.enums.StockTypeEnums;

import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 13:19:19
 * Description: -
 */
public class StringUtil {

    public static void main(String[] args) {
        System.out.println(underlineToCase("A_STOCK_CODE"));
    }
    /**
     * A_STOCK_CODE => aStockCode
     * @param underline
     * @return
     */
    public static String underlineToCase(String underline){
        underline = underline.toLowerCase(Locale.ROOT);
        String[] arr = underline.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : arr) {
            char[] chars = s.toCharArray();
            chars[0] = charToUpperCase(chars[0]);
            builder.append(chars);
        }
        char[] chars = builder.toString().toCharArray();
        chars[0] = charToLowCase(chars[0]);
        return String.valueOf(chars);
    }
    public static char charToUpperCase(char c){
        return c ^= 32;
    }
    public static char charToLowCase(char c){
        return c ^= 32;
    }

    /**
     * 将 000001，603138 => sz000001,sh603138 或者 s_sz000001,s_sh603138
     *
     * @param codes 000001，603138 类型
     * @return sz000001, sh603138 类型
     */
    public static String formatStockCode(String codes) {
        codes = codes.replaceAll(" +","");
        String[] codeArr = codes.split(",");
        return formatStockCodeWithArr(false,codeArr);

    }

    public static String formatStockCodeWithArr(Boolean single,String[] ...codeArr){
        StringBuilder formatCodes = new StringBuilder();
        for (String[] arr : codeArr) {
            for (String code : arr) {
                String prefix = getStockPrefix(code);
                if (single){
                    formatCodes.append("s_");
                }
                formatCodes.append(prefix);
                formatCodes.append(code);
                formatCodes.append(",");
            }
        }
        int index = formatCodes.lastIndexOf(",");
        return  formatCodes.deleteCharAt(index).toString();
    }

    public static String getStockPrefix(String code) {
        if (code.startsWith("6") || code.startsWith("9")) {
            // 以6、9开头，查询股票详情时候拼接前缀“sh”
            return StockTypeEnums.CHINA_STOCK_SH.getPrefix();
        } else if (code.startsWith("0") || code.startsWith("2") || code.startsWith("3")) {
            //A股-深证 6位纯数字，以0、2、3开头，查询股票详情时候拼接前缀“sz”
            return StockTypeEnums.CHINA_STOCK_SZ.getPrefix();
        } else {
            return "";
        }
    }


    /**
     * 将 000001，399006 => sh000001,sz399006
     *
     * @param codes 000001，399006 类型
     * @return sh000001,sz399006 类型
     */
    public static String formatIndexCode(String codes) {
        codes = codes.replaceAll(" +","");
        String[] codeArr = codes.split(",");
        StringBuilder formatCodes = new StringBuilder();
        for (String code : codeArr) {
            String prefix = getIndexPrefix(code);
            formatCodes.append(prefix);
            formatCodes.append(code);
            formatCodes.append(",");
        }
        int index = formatCodes.lastIndexOf(",");
        return  formatCodes.deleteCharAt(index).toString();
    }

    public static String getIndexPrefix(String code) {
        if (code.startsWith("0")) {
            return IndexEnum.CHINA_INDEX_SH.getPrefix();
        } else if (code.startsWith("3")) {
            return IndexEnum.CHINA_INDEX_SZ.getPrefix();
        } else {
            return "";
        }
    }
}
