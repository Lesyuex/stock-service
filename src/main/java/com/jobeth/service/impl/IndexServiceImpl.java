package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.common.util.CalcUtils;
import com.jobeth.common.util.PropertiesUtils;
import com.jobeth.common.util.ReflectionUtils;
import com.jobeth.common.util.RestTemplateUtils;
import com.jobeth.service.IndexService;
import com.jobeth.vo.MinutesVo;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockSingleVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/6 21:25:25
 * Description: -
 */
@Service
public class IndexServiceImpl implements IndexService {
    private static final BigDecimal ZERO = new BigDecimal(0);
    private static final BigDecimal HUNDREN = new BigDecimal(100);
    /**
     * 根据传入的股票代码查询股票当前详细信息（可批量603138，000001）
     *
     * @param codes code
     * @return 股票详细信息
     * @throws Exception Exception
     */
    @Override
    public List<StockDetailVo> getDetail(String codes) throws Exception {
        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtils.getByKey("txBatchDetail");
        String url = String.format("%s%s", txBatch, codes);
        // 解析数据
        String body = RestTemplateUtils.request(url, String.class);
        String str = body.replaceAll("\\n", "");
        String[] stockDetailArr = str.split(";");
        List<StockDetailVo> stockDetailVoList = new ArrayList<>(stockDetailArr.length);
        for (String stockDetail : stockDetailArr) {
            String[] arr = stockDetail.split("~");
            StockDetailVo stockVo = ReflectionUtils.createDataByStrArr(arr,StockDetailVo.class);
            stockDetailVoList.add(stockVo);
        }
        return stockDetailVoList;
    }


    /**
     * 查询指数简单信息（可批量上证指数，深证成指 000001，399001）
     *
     * @param codes codes
     * @return 指数信息
     * @throws Exception Exception
     */
    @Override
    public List<StockSingleVo> getSingle(String codes) throws Exception {

        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtils.getByKey("txBatchSingle");
        String[] codeArr = codes.split(",");
        StringBuilder builder = new StringBuilder();
        for (String code : codeArr) {
            builder.append("s_");
            builder.append(code);
            builder.append(",");
        }
        String substring = builder.substring(0, builder.length() - 1);
        String url = String.format("%s%s", txBatch, substring);
        String body = RestTemplateUtils.request(url, String.class);
        String str = body.replaceAll("\\n", "");
        String[] stockSingleStrArr = str.split(";");
        List<StockSingleVo> stockVoList = new ArrayList<>(stockSingleStrArr.length);
        for (String stockStr : stockSingleStrArr) {
            int begin = stockStr.indexOf("=\"") + 2;
            int end = stockStr.lastIndexOf("\"");
            String useStr = stockStr.substring(begin, end);
            String[] arr = useStr.split("~");
            StockSingleVo stockSingleVo = ReflectionUtils.createDataByStrArr(arr, StockSingleVo.class);
            stockVoList.add(stockSingleVo);
        }
        return stockVoList;
    }

    @Override
    public Map<String, Object> queryMinutes(String code) throws Exception {
        String txMinutes = null;
        if (code.startsWith("hk")) {
            txMinutes = PropertiesUtils.getByKey("hkMinutes");
        } else {
            txMinutes = PropertiesUtils.getByKey("txMinutes");
        }

        String realUrl = String.format("%s%s", txMinutes, code);
        String res = RestTemplateUtils.request(realUrl, String.class);
        // 解析数据
        JSONObject jsonObject = JSON.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject stock = data.getJSONObject(code);
        JSONObject qt = stock.getJSONObject("qt");
        // 判断是休市还是交易
        String marketStr = qt.getJSONArray("market").getString(0);
        String market = code.substring(0, 2).toUpperCase();
        boolean marketOpen = marketStr.indexOf(market + "_open") > 0;
        // 股票最新信息
        String[] newestInfo = qt.getObject(code, String[].class);
        StockDetailVo stockDetailVo = ReflectionUtils.createDataByStrArr(newestInfo, StockDetailVo.class);
        // 分时图数据 [时间、价格、成交量、成交额] => [时间、价格、总成交量、总成交额]
        JSONArray minutesData = stock.getJSONObject("data").getJSONArray("data");
        BigDecimal yestclose = BigDecimal.valueOf(stockDetailVo.getYesterdayPrice());

        List<MinutesVo> newMinutesData = new ArrayList<>(minutesData.size());
        double absMaxPercent = 0;
        BigDecimal clinch = new BigDecimal(0);
      /*  for (int i = 0; i < minutesData.size(); i++) {
            String minuStr = minutesData.getString(i);
            MinutesVo minutesVo = ReflectionUtils.createDataByStrArr(minuStr.split(" "), MinutesVo.class);
            // 每分钟的数据都是json字符串
            StringBuilder builder = new StringBuilder(minutesVo.getTime());
            if (builder.length() == 3) {
                builder.insert(0, "0");
            }
            builder.insert(2, ":");
            minutesVo.setTime(builder.toString());
            // 计算均价 （成交额除以成交量）//累计成交量//累计成交额
            Object[] objectList = new Object[7];
            // 格式化date 0930 => 09:30
            String dateStr = detail.getString(0);
            if (dateStr.length() == 3) {
                dateStr = "0" + dateStr;
            }
            StringBuilder stringBuilder = new StringBuilder(dateStr);
            stringBuilder.insert(2, ":");
            objectList[0] = stringBuilder.toString();
            //当前价
            BigDecimal minutesPrice = detail.getBigDecimal(1);
            objectList[1] = minutesPrice;
            // 累计成交量
            BigDecimal volume = detail.getBigDecimal(2);
            objectList[2] = volume;
            // 总成交额
            objectList[3] = "-";
            // 每分钟的成交量
            BigDecimal currentMinuVolume = volume;
            objectList[4] = volume;
            if (i > 0) {
                // 取上一分钟的总成交量
                String preJson = "[" + minutesData.getString(i - 1).replaceAll(" ", ",") + "]";
                JSONArray preDetail = JSON.parseObject(preJson, JSONArray.class);
                currentMinuVolume = volume.subtract(preDetail.getBigDecimal(2));
                objectList[4] = currentMinuVolume;
            }
            //均价
            //总成交
            clinch = minutesPrice.multiply(currentMinuVolume).add(clinch);
            BigDecimal average = volume.compareTo(ZERO) == 0 ? ZERO : clinch.divide(volume, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN);
            objectList[5] = average;
            //最新涨跌幅
            BigDecimal diffPrice = minutesPrice.subtract(yestclose);
            BigDecimal v = diffPrice.divide(yestclose, MathContext.DECIMAL128).multiply(HUNDREN).setScale(2, RoundingMode.HALF_DOWN);
            double percentVal = v.doubleValue();
            double abs = Math.abs(percentVal);
            if (abs > absMaxPercent) {
                absMaxPercent = abs;
            }
            objectList[6] = v;
            newMinutesData.add(objectList);
        }*/
        HashMap<String, Object> map = new HashMap<>();
        map.put("newestInfo", stockDetailVo);
        map.put("newestMinutes", newMinutesData);
        map.put("yestclose", yestclose);
        map.put("marketOpen", marketOpen);
        Map<String, Object> y = CalcUtils.calcYaxisInfo(yestclose, absMaxPercent);
        map.putAll(y);
        return map;
    }
}
