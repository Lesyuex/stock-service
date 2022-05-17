package com.jobeth.common.util;

import com.jobeth.common.NumberContext;
import com.jobeth.vo.StockKLineVo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/15 1:45:45
 * Description: -
 */
public class CalcUtils {
    public static Map<String, Object> calcYaxisInfo(BigDecimal yesClose, double maxPercent) {
        HashMap<String, Object> y = new HashMap<>(3);
        BigDecimal up = new BigDecimal(100d + maxPercent);
        BigDecimal down = new BigDecimal(100d - maxPercent);
        BigDecimal maxPrice = yesClose.multiply(up)
                .divide(NumberContext.HUNDREN, MathContext.DECIMAL128)
                .setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal minPrice = yesClose.multiply(down)
                .divide(NumberContext.HUNDREN, MathContext.DECIMAL128)
                .setScale(2, RoundingMode.HALF_DOWN);
        y.put("maxYaxisPercent", maxPercent);
        y.put("maxYaxisPrice", maxPrice);
        y.put("minYaxisPrice", minPrice);
        return y;
    }


    public static String format(String value) {
        DecimalFormat df = new DecimalFormat();
        // 0:位置上无数字显示0
        df.applyPattern("0.00");
        return df.format(new BigDecimal(value));
    }

    /**
     * @param maArr [5,10,20,30,60,120]
     * @param list  [day1,day2,day3]
     */
    public static void calcMa(int[] maArr, List<StockKLineVo> list) {
        BigDecimal zero = new BigDecimal(0);
        for (int ma : maArr) {
            String key = "MA" + ma;
            BigDecimal day = new BigDecimal(ma);
            for (int index = 0; index < list.size(); index++) {
                StockKLineVo stockKLineVo = list.get(index);
                if (index < ma) {
                    // list[index].MA5 = "-"
                    stockKLineVo.getMaMap().put(key, "-");
                    continue;
                }
                BigDecimal sum = zero;
                for (int preIndex = 0; preIndex < ma; preIndex++) {
                    StockKLineVo pre = list.get(index - preIndex);
                    sum = sum.add(pre.getClose());
                }
                BigDecimal value = sum.divide(day, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN);
                stockKLineVo.getMaMap().put(key, value);
            }
        }
    }
}
