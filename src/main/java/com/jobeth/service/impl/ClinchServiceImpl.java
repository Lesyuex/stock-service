package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.service.ClinchService;
import com.jobeth.common.util.PropertiesUtils;
import com.jobeth.common.util.ReflectionUtils;
import com.jobeth.common.util.RestTemplateUtils;
import com.jobeth.common.util.StockUtils;
import com.jobeth.vo.ClinchDetailVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClinchServiceImpl implements ClinchService {

    @Override
    public List<ClinchDetailVo> queryMingxi(String code, int size) throws Exception {
        String txMingxi = PropertiesUtils.getByKey("txMingxi");
        String url = txMingxi.replace("codePlace", code).replace("sizePlace", String.valueOf(size));
        String res = RestTemplateUtils.request(url, String.class);
        JSONObject resObj = JSON.parseObject(res);
        JSONArray array = resObj.getJSONObject("data").getJSONArray("data");
        List<ClinchDetailVo> list = new ArrayList<>(size);
        for (Object o : array) {
            String[] strArr = String.valueOf(o).split("/");
            ClinchDetailVo dataByStrArr = ReflectionUtils.createDataByStrArr(strArr, ClinchDetailVo.class);
            list.add(dataByStrArr);
        }
        return list;
    }
}
