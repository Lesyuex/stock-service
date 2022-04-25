package com.jobeth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jobeth.entity.StockInfo;
import com.jobeth.mapper.StockInfoMapper;
import com.jobeth.service.CountService;
import com.jobeth.util.*;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockSingleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/23 19:43:43
 * Description: -
 */
@Service
public class CountServiceImpl implements CountService {
    public static void main(String[] args) {
        String txBatchSingle = PropertiesUtil.getByKey("txBatchDetail");
        String pre = "s_sh";
        StringBuilder builder = new StringBuilder();

        for (int i = 600000; i < 600700; i++) {
            builder.append(pre);
            builder.append(i);
            builder.append(",");
        }
        int i = builder.lastIndexOf(",");
        String substring = builder.substring(0, i - 1);
        System.out.println(substring);
        String s = HttpClientUtil.sendGet(txBatchSingle + substring, null);
        System.out.println(s);
    }

    @Autowired
    private StockInfoMapper stockInfoMapper;

    /**
     * 市场总览
     *
     * @return Map
     */
    @Override
    public Map<String, Integer> countUpAndDown() throws Exception {
        HashMap<String, Integer> map = new HashMap<>();
        String txBatchSingle = PropertiesUtil.getByKey("txBatchDetail");
        // 先查找所有股票代码
        QueryWrapper<StockInfo> query = new QueryWrapper<>();
        query.select("code");
        query.eq("AB_FLAG", "A");
        List<StockInfo> aStockList = stockInfoMapper.selectList(query);
        QueryWrapper<StockInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("code");
        queryWrapper.eq("AB_FLAG", "B");
        List<StockInfo> bStockList = stockInfoMapper.selectList(queryWrapper);
        List<String> collect = aStockList.stream().map(StockInfo::getCode).collect(Collectors.toList());
        List<String> collect1 = bStockList.stream().map(StockInfo::getCode).collect(Collectors.toList());
        collect.addAll(collect1);
        int size = collect.size();
        String codes = "";
        StringBuilder body = new StringBuilder();
        int num = size % 600 ==0 ? size/600 : size/600 + 1;
        for (int i = 0; i < num; i++) {
            int endSize = ((i + 1) * 600) > size ? collect.size() : (i + 1) * 600;
            List<String> strings = collect.subList(i * 600, endSize);


            String[] allArr = strings.toArray(new String[strings.size()]);
            codes = StringUtil.formatStockCodeWithArr(false, allArr);
            String realUrl = String.format("%s%s", txBatchSingle, codes);
            String s = RestTemplateUtil.request(realUrl, String.class);
            body.append(s);
        }


        String str = body.toString().replaceAll("\\n", "");
        String[] stockSingleStrArr = str.split(";");
        int percentMax = 0, gtSeven = 0, gtFive = 0, gtTwo = 0, gtZero = 0, eqZero = 0, ltZero = 0, ltNegaTwo = 0, ltNegaFive = 0, ltNegaSeven = 0, percentMin = 0;
        for (String stockStr : stockSingleStrArr) {
            int begin = stockStr.indexOf("=\"") + 2;
            int end = stockStr.lastIndexOf("\"");
            String useStr = stockStr.substring(begin, end);
            String[] arr = useStr.split("~");
            StockDetailVo singleVo = ReflectionUtils.createDataByStrArr(arr, StockDetailVo.class);
            double currentPrice = Double.parseDouble(singleVo.getCurrentPrice());
            double dailyLimitPrice = Double.parseDouble(singleVo.getDailyLimitPrice());
            double limitDownPrice = Double.parseDouble(singleVo.getLimitDownPrice());
            double percent = Double.parseDouble(singleVo.getUpDownPercent());
            if (percent == 0) {
                eqZero++;
            } else if (percent > 0) {
                if (currentPrice == dailyLimitPrice) {
                    percentMax++;
                } else if (percent > 7) {
                    gtSeven++;
                } else if (percent > 5) {
                    gtFive++;
                } else if (percent > 2) {
                    gtTwo++;
                } else {
                    gtZero++;
                }
            } else {
                if (currentPrice == limitDownPrice) {
                    percentMin++;
                } else if (percent < -7) {
                    ltNegaSeven++;
                } else if (percent < -5) {
                    ltNegaFive++;
                } else if (percent < -2) {
                    ltNegaTwo++;
                } else {
                    ltZero++;
                }
            }
        }
        map.put("percentMax", percentMax);
        map.put("gtSeven", gtSeven);
        map.put("gtFive", gtFive);
        map.put("gtTwo", gtTwo);
        map.put("gtZero", gtZero);
        map.put("eqZero", eqZero);
        map.put("ltZero", ltZero);
        map.put("ltNegaTwo", ltNegaTwo);
        map.put("ltNegaFive", ltNegaFive);
        map.put("ltNegaSeven", ltNegaSeven);
        map.put("percentMin", percentMin);
        return map;
    }
}
