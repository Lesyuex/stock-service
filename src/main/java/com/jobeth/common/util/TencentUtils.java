package com.jobeth.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.common.NumberContext;
import com.jobeth.vo.MinutesVo;
import com.jobeth.vo.StockDetailVo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class TencentUtils {

    /**
     * 解析腾讯分时数据
     *
     * @param minuStrArr ["0930 13.39 313 419107.00","0931 13.39 313 419107.00"] 每分钟的数据都是json字符串
     * @return
     */
    public static void initMinutesList(JSONArray minuStrArr, int stockType, Map<String, Object> stockMap) throws Exception {
        int length = minuStrArr.size();
        List<MinutesVo> minutesVoList = new ArrayList<>(length);
        BigDecimal yesterdayPrice = BigDecimal.valueOf((Double) stockMap.get("yesterdayPrice"));
        double maxPercent = 0;
        double maxVolume = 0;
        BigDecimal indexClinch = stockType == 2 ? new BigDecimal(0) : null;
        for (int i = 0; i < length; i++) {
            String minuStr = minuStrArr.getString(i);
            MinutesVo minutesVo = ReflectionUtils.createDataByStrArr(minuStr.split(" "), MinutesVo.class);
            StringBuilder builder = new StringBuilder(minutesVo.getTime());
            if (builder.length() == 3) {
                builder.insert(0, "0");
            }
            builder.insert(2, ":");
            minutesVo.setTime(builder.toString());
            // 分时成交量 (累计成交量-上一分钟成交量)
            if (i > 0) {
                MinutesVo preMinu = minutesVoList.get(i - 1);
                minutesVo.setMinuVolume(minutesVo.getVolume().subtract(preMinu.getVolume()));
            } else {
                minutesVo.setMinuVolume(minutesVo.getVolume());
            }
            // 股票
            if (stockType == 1) {
                // 计算均价 （成交额除以成交量） （股票返回的是累计成交额）
                BigDecimal volume = minutesVo.getVolume();
                BigDecimal clinch = minutesVo.getClinch();
                BigDecimal average = volume.compareTo(NumberContext.ZERO) == 0 ? NumberContext.ZERO :
                        clinch.divide(volume, MathContext.DECIMAL128)
                                .divide(NumberContext.HUNDREN, MathContext.DECIMAL128)
                                .setScale(2, RoundingMode.HALF_DOWN);
                minutesVo.setAveragePrice(average);
            }
            // 指数
            else if (stockType == 2) {
                //分时成交量
                BigDecimal minuVolume = minutesVo.getMinuVolume();
                // 总成交额 = 当前成交价×当前成交量+之前累计成交额 （指数返回的是分时成交额）
                indexClinch = minutesVo.getPrice().multiply(minuVolume).add(indexClinch);
                // 平均价=当前总成交额 （累计成交额）÷总成交量
                BigDecimal average = minutesVo.getVolume().compareTo(NumberContext.ZERO) == 0 ? NumberContext.ZERO :
                        indexClinch.divide(minutesVo.getVolume(), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN);
                minutesVo.setAveragePrice(average);
            }

            //最新涨跌幅
            BigDecimal changeValue = minutesVo.getPrice().subtract(yesterdayPrice);
            minutesVo.setChangeValue(changeValue);
            BigDecimal minuPercent = changeValue
                    .divide(yesterdayPrice, MathContext.DECIMAL128)
                    .multiply(NumberContext.HUNDREN)
                    .setScale(2, RoundingMode.HALF_DOWN);
            minutesVo.setPercent(minuPercent);
            maxPercent = Math.max(Math.abs(minuPercent.doubleValue()), maxPercent);
            maxVolume = Math.max(minutesVo.getMinuVolume().doubleValue(), maxVolume);
            minutesVoList.add(minutesVo);
        }
        stockMap.put("minutesList", minutesVoList);
        stockMap.put("maxVolume", maxVolume);
        Map<String, Object> y = CalcUtils.calcYaxisInfo(yesterdayPrice, maxPercent);
        stockMap.putAll(y);
    }

    public static JSONObject getStock(String url, String code) {
        String res = RestTemplateUtils.request(url, String.class);
        JSONObject resObj = JSON.parseObject(res);
        return resObj.getJSONObject("data").getJSONObject(code);
    }

    public static StockDetailVo getStockDetail(JSONObject stock, String code) throws Exception {
        // 解析数据
        JSONObject qt = stock.getJSONObject("qt");
        String[] newestInfo = qt.getObject(code, String[].class);
        return ReflectionUtils.createDataByStrArr(newestInfo, StockDetailVo.class);
    }

    public static String getStatus(JSONObject stock, String code) {
        // 解析数据
        JSONObject qt = stock.getJSONObject("qt");
        String marketStr = qt.getJSONArray("market").getString(0);
        String market = code.substring(0, 2).toUpperCase();
        // 判断是休市还是交易
        return marketStr.indexOf(market + "_open") > 0 ? "is_open" : "is_close";
    }
}
