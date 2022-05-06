package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.common.util.PropertiesUtils;
import com.jobeth.common.util.ReflectionUtils;
import com.jobeth.common.util.RestTemplateUtils;
import com.jobeth.service.IndexService;
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
            StockDetailVo stockVo = StockDetailVo.generateStockByStrArr(arr);
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
        String txMinutes = PropertiesUtils.getByKey("txMinutes");
        String realUrl = String.format("%s%s", txMinutes, code);
        String res = RestTemplateUtils.request(realUrl, String.class);
        // 解析数据
        JSONObject jsonObject = JSON.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject stock = data.getJSONObject(code);
        String[] newestInfo = stock.getJSONObject("qt").getObject(code, String[].class);

        StockDetailVo stockDetailVo = ReflectionUtils.createDataByStrArr(newestInfo, StockDetailVo.class);
        // 分时图数据 [时间、价格、成交量、成交额] => [时间、价格、总成交量、总成交额]
        JSONArray minutesData = stock.getJSONObject("data").getJSONArray("data");
        BigDecimal yestclose = BigDecimal.valueOf(stockDetailVo.getYesterdayPrice());
        BigDecimal bigDecimal100 = new BigDecimal(100);
        List<Object[]> newMinutesData = new ArrayList<>(minutesData.size());
        for (int i = 0; i < minutesData.size(); i++) {
            Object[] objects = new Object[7];
            String json = "[" + minutesData.getString(i).replaceAll(" ", ",") + "]";
            JSONArray o = JSON.parseObject(json, JSONArray.class);
            // 格式化date 0930 => 09:30
            String dateStr = o.getString(0);
            if (dateStr.length() == 3) {
                dateStr = "0" + dateStr;
            }
            StringBuilder stringBuilder = new StringBuilder(dateStr);
            stringBuilder.insert(2, ":");
            objects[0] = stringBuilder.toString();
            BigDecimal minutesPrice = o.getBigDecimal(1);
            objects[1] = minutesPrice;
            // 计算均价 （成交额除以成交量）//累计成交量//累计成交额
            BigDecimal volume = o.getBigDecimal(2);
            BigDecimal clinch = o.getBigDecimal(3);
            objects[2] = volume;
            objects[3] = clinch;
            double minutesVolume = volume.doubleValue();
            // 9.31 -> 14.57
            if (1 <= i && i <= 238) {
                String preStr = "[" + minutesData.getString(i - 1).replaceAll(" ", ",") + "]";
                JSONArray pre = JSON.parseObject(preStr, JSONArray.class);
                minutesVolume = minutesVolume - pre.getInteger(2);
            } else if (i == 239 || i == 240) {
                minutesVolume = 0;
            } else if (i == 241) {
                String preStr = "[" + minutesData.getString(238).replaceAll(" ", ",") + "]";
                JSONArray pre = JSON.parseObject(preStr, JSONArray.class);
                minutesVolume = minutesVolume - pre.getDouble(2);
            }
            objects[4] = minutesVolume;
            //均价
            double average = clinch.divide(volume, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            objects[5] = average;
            //最新涨跌幅
            BigDecimal diffPrice = minutesPrice.subtract(yestclose);
            double v = diffPrice.divide(yestclose, MathContext.DECIMAL128).multiply(bigDecimal100).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            objects[6] = v;
            newMinutesData.add(objects);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("newestInfo", stockDetailVo);
        map.put("newestMinutes", newMinutesData);
        map.put("yestclose", yestclose);
        return map;
    }
}
