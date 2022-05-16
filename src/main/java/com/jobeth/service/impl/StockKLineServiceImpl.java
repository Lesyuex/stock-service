package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.common.util.*;
import com.jobeth.dto.KLineDto;

import com.jobeth.service.StockKLineService;

import com.jobeth.vo.FivedayVo;
import com.jobeth.vo.MinutesVo;
import com.jobeth.vo.StockKLineVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 日k数据 服务实现类
 * </p>
 *
 * @author jobeth
 * @since 2022-04-30
 */
@Service
@Slf4j
public class StockKLineServiceImpl implements StockKLineService {

    /**
     * 获取 k线图
     *
     * @param dto dto
     * @return List<StockDayInfo>
     */
    @Override
    public List<StockKLineVo> queryK(KLineDto dto) {
        String kLine = PropertiesUtils.getByKey("kLine");
        String url = kLine
                .replace("kname", dto.getKname())
                .replace("codePlace", dto.getCode())
                .replace("startDatePlace", dto.getStartDate())
                .replace("endDatePlace", dto.getEndDate());
        String res = RestTemplateUtils.request(url, String.class);
        // {code: 0,msg: "",data: {sh603138: {qfqday: [k数据],qt:{sh603138:{最新数据}}}}}
        JSONObject jsonObject = JSON.parseObject(res);
        JSONArray jsonArray;
        if (dto.getStockType() == 0) {
            jsonArray = jsonObject
                    .getJSONObject("data").
                    getJSONObject(dto.getCode()).
                    getJSONArray(String.format("%s%s", "qfq", dto.getKname()));
        } else {
            // 指数不带qfq前缀
            jsonArray = jsonObject
                    .getJSONObject("data")
                    .getJSONObject(dto.getCode())
                    .getJSONArray(dto.getKname());
        }
        List<StockKLineVo> list = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray day = jsonArray.getJSONArray(i);
            StockKLineVo stockKLineVo = new StockKLineVo();
            stockKLineVo.setDate(day.getObject(0, LocalDate.class));
            stockKLineVo.setOpen(day.getBigDecimal(1));
            stockKLineVo.setClose(day.getBigDecimal(2));
            stockKLineVo.setHighest(day.getBigDecimal(3));
            stockKLineVo.setLowest(day.getBigDecimal(4));
            stockKLineVo.setTurnover(day.getBigDecimal(5));
            stockKLineVo.setTurnoverRate(day.getBigDecimal(7));
            stockKLineVo.setVolume(day.getDouble(8));
            list.add(stockKLineVo);
        }

        int[] maArr ={5,10,20,30,60,120};
        CalcUtils.calcMa(maArr,list);
        return list;
    }

    @Override
    public Map<String, Object> getFiveday(String code) throws Exception {
        try {
            String fiveDay = PropertiesUtils.getByKey("fiveDay") + code;
            String res = RestTemplateUtils.request(fiveDay, String.class);
            JSONObject obj = JSONObject.parseObject(res);
            JSONObject data = obj.getJSONObject("data");
            JSONArray realData = data.getJSONObject(code).getJSONArray("data");
            BigDecimal bigDecimal = new BigDecimal(100);
            BigDecimal beginPrice = realData.getJSONObject(realData.size() - 1).getBigDecimal("prec");
            Map<String, Object> map = new HashMap<>(2);
            //五日数据
            List<FivedayVo> fivedayVoList = new ArrayList<>(realData.size());
            // 计算刻度线最大值
            double absMaxPercent = 0;
            for (Object o : realData) {
                JSONObject day = (JSONObject) o;
                FivedayVo fivedayVo = new FivedayVo();
                fivedayVo.setDate(day.getObject("date", LocalDate.class));
                fivedayVo.setYesclose(day.getBigDecimal("prec"));
                List<MinutesVo> minutesVoList = new ArrayList<>();
                JSONArray minuArr = day.getJSONArray("data");
                for (int i = 0; i < minuArr.size(); i++) {
                    String minuStr = minuArr.getString(i);
                    MinutesVo minutesVo = ReflectionUtils.createDataByStrArr(minuStr.split(" "), MinutesVo.class);
                    StringBuilder builder = new StringBuilder(minutesVo.getTime());
                    if (builder.length() == 3) {
                        builder.insert(0, "0");
                    }
                    builder.insert(2, ":");
                    minutesVo.setTime(builder.toString());
                    // 计算均价 （成交额除以成交量）//累计成交量//累计成交额
                    BigDecimal volume = minutesVo.getVolume();
                    BigDecimal clinch = minutesVo.getClinch();
                    // 分时成交量
                    if (i > 0) {
                        MinutesVo preMinu = minutesVoList.get(i - 1);
                        minutesVo.setMinuVolume(minutesVo.getVolume().subtract(preMinu.getVolume()));
                    } else {
                        minutesVo.setMinuVolume(minutesVo.getVolume());
                    }
                    //均价
                    BigDecimal average = clinch.divide(volume, MathContext.DECIMAL128).divide(bigDecimal, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN);
                    minutesVo.setAveragePrice(average);
                    // 涨跌情况 涨跌幅
                    BigDecimal minutesPrice = minutesVo.getPrice();
                    BigDecimal changeValue = minutesPrice.subtract(beginPrice);
                    minutesVo.setChangeValue(changeValue);
                    BigDecimal percent = changeValue.divide(beginPrice, MathContext.DECIMAL128).multiply(bigDecimal).setScale(2, RoundingMode.HALF_DOWN);
                    minutesVo.setPercent(percent);
                    minutesVoList.add(minutesVo);
                    double percentVal = percent.doubleValue();
                    double abs = Math.abs(percentVal);
                    if (abs > absMaxPercent) {
                        absMaxPercent = abs;
                    }
                }
                fivedayVo.setMinutesVoList(minutesVoList);
                fivedayVoList.add(fivedayVo);
            }
            map.put("fiveday", fivedayVoList);
            Map<String, Object> y = CalcUtils.calcYaxisInfo(beginPrice, absMaxPercent);
            map.putAll(y);
            return map;
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    @Override
    public List<StockKLineVo> getMinuK(KLineDto dto) {
        String kLine = PropertiesUtils.getByKey("otherMinu");
        String url = kLine
                .replace("codePlace", dto.getCode())
                .replace("kname", dto.getKname())
                .replace("endDatePlace", dto.getEndDate()== null ? "" : dto.getEndDate());
        String res = RestTemplateUtils.request(url, String.class);
        // {code: 0,msg: "",data: {sh603138: {typePlace: [k数据],qt:{sh603138:{最新数据}}}}}
        JSONObject jsonObject = JSON.parseObject(res);
        JSONArray jsonArray = jsonObject
                    .getJSONObject("data")
                    .getJSONObject(dto.getCode())
                    .getJSONArray(dto.getKname());
        List<StockKLineVo> list = new ArrayList<>(jsonArray.size());
        jsonArray.forEach(o -> {
            JSONArray arr = (JSONArray) o;
            StockKLineVo stockKLineVo = new StockKLineVo();
            stockKLineVo.setDate(arr.getObject(0, LocalDate.class));
            stockKLineVo.setOpen(arr.getBigDecimal(1));
            stockKLineVo.setClose(arr.getBigDecimal(2));
            stockKLineVo.setHighest(arr.getBigDecimal(3));
            stockKLineVo.setLowest(arr.getBigDecimal(4));
            stockKLineVo.setVolume(arr.getDouble(5));
            stockKLineVo.setTurnoverRate(arr.getBigDecimal(7));
            list.add(stockKLineVo);
        });
        return list;
    }

}
