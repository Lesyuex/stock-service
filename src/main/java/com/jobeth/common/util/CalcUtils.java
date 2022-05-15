package com.jobeth.common.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/15 1:45:45
 * Description: -
 */
public class CalcUtils {
    private final static BigDecimal BIG_DECIMAL100 = new BigDecimal(100);
    public static Map<String, Object> calcYaxisInfo(BigDecimal yesClose, double absMaxPercent){
        HashMap<String, Object> y = new HashMap<>(3);
        BigDecimal down = new BigDecimal(100d - absMaxPercent);
        BigDecimal up = new BigDecimal(100d + absMaxPercent);
        BigDecimal yMaxValue = yesClose.multiply(up).divide(BIG_DECIMAL100, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal yMinValue = yesClose.multiply(down).divide(BIG_DECIMAL100, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN);
        y.put("maxPercent",absMaxPercent);
        y.put("maxValue",yMaxValue);
        y.put("minValue",yMinValue);
        return y;
    }


    public static String format(String value){
        DecimalFormat df = new DecimalFormat();
        // 0:位置上无数字显示0
        df.applyPattern("0.00");
        return df.format(new BigDecimal(value));
    }
}
