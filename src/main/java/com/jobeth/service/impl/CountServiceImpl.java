package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.common.util.HttpClientUtils;
import com.jobeth.common.util.PropertiesUtils;
import com.jobeth.service.CountService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/23 19:43:43
 * Description: -
 */
@Service
public class CountServiceImpl implements CountService {
    /**
     * 市场总览
     *
     * @return Map
     */
    @Override
    public Map<String, Integer> countUpAndDown() throws Exception {
        HashMap<String, Integer> map = new HashMap<>();

        String clsCount = PropertiesUtils.getByKey("clsCount");
        String s = HttpClientUtils.sendGet(clsCount, null);
        JSONObject resObj = JSON.parseObject(s);
        JSONObject data = resObj.getJSONObject("data").getJSONObject("up_down_dis");
        map.put("percentMax", data.getInteger("up_num"));
        map.put("gtEight", data.getInteger("up_10"));
        map.put("gtSix", data.getInteger("up_8"));
        map.put("gtFour", data.getInteger("up_6"));
        map.put("gtTwo", data.getInteger("up_4"));
        map.put("gtZero", data.getInteger("up_2"));
        map.put("eqZero", data.getInteger("flat_num"));
        map.put("ltZero", data.getInteger("down_2"));
        map.put("ltNegaTwo", data.getInteger("down_4"));
        map.put("ltNegaFour", data.getInteger("down_6"));
        map.put("ltNegaSix", data.getInteger("down_8"));
        map.put("ltNegaEight", data.getInteger("down_10"));
        map.put("percentMin", data.getInteger("down_num"));
        map.put("stopNum",data.getInteger("suspend_num"));
        map.put("flatNum",data.getInteger("flat_num"));
        map.put("allUpNum",data.getInteger("rise_num"));
        map.put("allDownNum",data.getInteger("fall_num"));
        return map;
    }
}
