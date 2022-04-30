package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.service.FundingService;
import com.jobeth.common.util.PropertiesUtils;
import com.jobeth.common.util.ReflectionUtils;
import com.jobeth.common.util.RestTemplateUtils;
import com.jobeth.vo.FundingVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FundingServiceImpl implements FundingService {
    @Override
    public Map<String, List<FundingVo>> getMinutes() throws Exception{
        String eastFunding = PropertiesUtils.getByKey("eastFunding");
        String response = RestTemplateUtils.request(eastFunding, String.class);
        JSONObject resObj = JSON.parseObject(response);
        JSONObject data = resObj.getJSONObject("data");
        //北向资金
        JSONArray s2n = data.getJSONArray("s2n");
        List<FundingVo> s2nList = new ArrayList<>();
        for (Object o : s2n) {
            String[] arr = String.valueOf(o).replaceAll("\"","").split(",");
            FundingVo fundingVo = ReflectionUtils.createDataByStrArr(arr,FundingVo.class);
            s2nList.add(fundingVo);
        }
        List<FundingVo> n2sList = new ArrayList<>();
        // 南向资金
        JSONArray n2s = data.getJSONArray("n2s");
        for (Object o : n2s) {
            String[] arr = String.valueOf(o).replaceAll("\"","").split(",");
            FundingVo fundingVo = ReflectionUtils.createDataByStrArr(arr,FundingVo.class);
            n2sList.add(fundingVo);
        }
        HashMap<String, List<FundingVo>> map = new HashMap<>();
        map.put("s2n",s2nList);
        map.put("n2s",n2sList);
        return map;
    }
}
