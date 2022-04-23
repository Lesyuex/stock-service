package com.jobeth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.enums.RequestUrlEnums;
import com.jobeth.util.HttpClientUtil;
import com.jobeth.util.JsonUtil;
import com.jobeth.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/21 16:04:04
 * Description: 行业
 */
public class IndustryController {
    public static void main(String[] args) throws Exception {
        String s = StringUtil.formatStockCode("603138");
        String realUrl = "http://91.push2.eastmoney.com/api/qt/clist/get?cb=jQuery1124006352096437219323_1650528064076&pn=1&pz=100&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:90+t:2+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152,f124,f107,f104,f105,f140,f141,f207,f208,f209,f222&_=1650528064077%E2%80%98";
        String s1 = HttpClientUtil.sendGet(realUrl, null);
        System.out.println(s1);
        int i = s1.indexOf("\"data\":");
        int i1 = s1.indexOf("});");
        String json = s1.substring(i+7,i1);
        System.out.println(json);
        JSONObject jsonObject = JSON.parseObject(json);
        Integer total = jsonObject.getInteger("total");
        System.out.println(total);
        JSONArray diffArr = jsonObject.getJSONArray("diff");
        System.out.println(diffArr.size());
        Map<String,String> map = new HashMap<>();
        diffArr.forEach(diff->{
            JSONObject industry = (JSONObject) diff;
            String code = industry.getString("f12");
            String name = industry.getString("f14");
           map.put(code,name);
        });
        JsonUtil.toPrettyFormat(map);

        String bkListUrl = "http://23.push2.eastmoney.com/api/qt/clist/get?cb=jQuery1124010292195371102975_1650533536164&pn=1&pz=200&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=b:BK0437+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152,f45&_=1650533536233";
        String listJson = HttpClientUtil.sendGet(bkListUrl, null);
        System.out.println(listJson);
        int i2= listJson.indexOf("\"data\":");
        int i3 = listJson.indexOf("});");
        String json2 = listJson.substring(i2+7,i3);
        System.out.println(json2);
        JSONObject jsonObject2 = JSON.parseObject(json2);
        Integer total2 = jsonObject2.getInteger("total");
        System.out.println(total2);
        JSONArray diffArr2 = jsonObject2.getJSONArray("diff");
        System.out.println(diffArr2.size());
    }
}
