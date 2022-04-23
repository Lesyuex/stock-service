package com.jobeth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.util.HttpClientUtil;
import com.jobeth.util.JsonUtil;
import com.jobeth.util.RestTemplateUtil;
import com.jobeth.vo.ResultVo;
import com.jobeth.vo.StockDetailVo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ShareController {
    public static void main(String[] args) throws Exception {

        String url = "https://proxy.finance.qq.com/ifzqgtimg/appstock/app/dealinfo/getMingxiV2?code=sh000001&limit=6&direction=1&_callback=jQuery1124014319639015035346_1650137039942&_=1650137039992";
        Map<String, String> params = new HashMap<>();
        params.put("list","sh605086,sh605086");
        //String s = HttpClientUtil.sendGet(url, params);

        String singleUrl = "http://qt.gtimg.cn/q=s_";
        StringBuilder builder = new StringBuilder();
        String realUrl = null;
        for (int i = 600000; i < 700000; i++) {
            builder.append("sh");
            builder.append(i);
            builder.append(",");
            if (i%100 == 0){
                realUrl = singleUrl + builder.toString();;
                String s1 = HttpClientUtil.sendGet(realUrl, params);
                System.out.println(s1);
                builder.setLength(0);
                Thread.sleep(2000);
            }
        }
        realUrl = singleUrl + builder.toString();
        String s1 = HttpClientUtil.sendGet(realUrl, params);
        System.out.println(s1);
    }

    @GetMapping("/getShareDetailInfo")
    public ResultVo<StockDetailVo> getShareById(@RequestParam String id) throws IllegalAccessException {
        //"http://qt.gtimg.cn/q=sh605086,sh603138"; 腾讯批量
        String url = "http://qt.gtimg.cn/q=" + id;
        String info =  RestTemplateUtil.request(url, String.class);
        String[] arr = info.split("~");
        StockDetailVo shareVo = StockDetailVo.generateStockByStrArr(arr);
        return ResultVo.success(shareVo);
    }

    @GetMapping("/getSingleInfo")
    public ResultVo<StockDetailVo> getSingleInfo(String id) throws IllegalAccessException {
        String url = "http://qt.gtimg.cn/q=s_" + id;
        String info = RestTemplateUtil.request(url, String.class);
        String[] arr = info.split("~");
        StockDetailVo shareVo = StockDetailVo.generateStockByStrArr(arr);
        JsonUtil.toPrettyFormat(shareVo);
        return ResultVo.success(shareVo);

    }
    @GetMapping("/getminutes")
    public ResultVo<Object> getminutes(String market,String id) throws IllegalAccessException {
        // sh 是0开头的 sz是1开头的 网易财经
        String url = "http://img1.money.126.net/data/"+market+"/time/today/" + id + ".json";
        String body = RestTemplateUtil.request(url, String.class);
        JSONObject jsonObject = JSON.parseObject(body);
        //分时图数据 [时间、价格、均价、成交量]
        JSONArray minutesData = (JSONArray) jsonObject.get("data");
        BigDecimal yestPrice = new BigDecimal (jsonObject.get("yestclose").toString());
        BigDecimal bigDecimal100 = new BigDecimal(100);
        for (int i = 0; i < minutesData.size(); i++) {
            JSONArray o = (JSONArray) minutesData.get(i);
            // 格式化date 0930 => 09:30
            String dateStr = o.get(0).toString();
            StringBuffer stringBuffer = new StringBuffer(dateStr);
            stringBuffer.insert(2,":");
            o.set(0,stringBuffer.toString());
            BigDecimal minutesPrice = new BigDecimal(o.get(1).toString());
            BigDecimal diffPrice = minutesPrice.subtract(yestPrice);
            Double v = diffPrice.divide(yestPrice, MathContext.DECIMAL128).multiply(bigDecimal100).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
            o.add(v);
        }
        return ResultVo.success(jsonObject);

    }
    // 雪球分时数据
    @GetMapping("/getStockMinutesJson")
    public ResultVo<Object> getStockMinutesJson(String market,String id) throws IllegalAccessException {
        // sh 是0开头的 sz是1开头的
        String url = "https://stock.xueqiu.com/v5/stock/chart/minute.json?symbol="+market+ id + "&period=1d";
        String body = RestTemplateUtil.request(url, String.class);
        return ResultVo.success(body);

    }

}
