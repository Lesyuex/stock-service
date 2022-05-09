package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.common.util.*;
import com.jobeth.dto.KLineDto;
import com.jobeth.mapper.StockDayInfoMapper;
import com.jobeth.po.StockDayInfo;
import com.jobeth.service.StockKLineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobeth.vo.StockKLineVo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 日k数据 服务实现类
 * </p>
 *
 * @author jobeth
 * @since 2022-04-30
 */
@Service
public class StockKLineServiceImpl extends ServiceImpl<StockDayInfoMapper, StockDayInfo> implements StockKLineService {

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
                .replace("typePlace", dto.getType())
                .replace("codePlace", dto.getCode())
                .replace("startDatePlace", dto.getStartDate())
                .replace("endDatePlace", dto.getEndDate());
        String res = RestTemplateUtils.request(url, String.class);


        JSONObject jsonObject = JSON.parseObject(res);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject(dto.getCode()).getJSONArray(String.format("%s%s","qfq",dto.getType()));
        if (jsonArray == null){
            jsonArray = jsonObject.getJSONObject("data").getJSONObject(dto.getCode()).getJSONArray(dto.getType());
        }
        List<StockKLineVo> list = new ArrayList<>(jsonArray.size());
        jsonArray.forEach(o->{
            JSONArray arr = (JSONArray) o;
            StockKLineVo stockKLineVo = new StockKLineVo();
            stockKLineVo.setDate(arr.getObject(0, LocalDate.class));
            stockKLineVo.setOpen(arr.getBigDecimal(1));
            stockKLineVo.setClose(arr.getBigDecimal(2));
            stockKLineVo.setHighest(arr.getBigDecimal(3));
            stockKLineVo.setLowest(arr.getBigDecimal(4));
            stockKLineVo.setTurnover(arr.getBigDecimal(5));
            stockKLineVo.setTurnoverRate(arr.getBigDecimal(7));
            stockKLineVo.setVolume(arr.getDouble(8));
            list.add(stockKLineVo);
        });
        return list;
    }

}
