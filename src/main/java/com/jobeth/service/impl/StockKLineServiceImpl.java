package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.common.util.*;
import com.jobeth.dto.KLineDto;
import com.jobeth.mapper.StockDayInfoMapper;
import com.jobeth.po.StockDayInfo;
import com.jobeth.mapper.StockInfoMapper;
import com.jobeth.service.StockKLineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobeth.vo.StockKLineVo;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private StockInfoMapper stockInfoMapper;

    /**
     * 获取 k线图
     *
     * @param dto dto
     * @return List<StockDayInfo>
     */
    @Override
    public List<StockKLineVo> queryK(KLineDto dto) {
        String kLine = PropertiesUtils.getByKey("kLine");
        String realCodes = StockUtils.getRealCodes(dto.getType(), dto.getCode());
        String url = kLine
                .replace("typePlace", dto.getKType())
                .replace("codePlace", realCodes)
                .replace("startDatePlace", dto.getStartDate())
                .replace("endDatePlace", dto.getEndDate());
        String res = RestTemplateUtils.request(url, String.class);
        int begin = res.indexOf("qfq=");
        String json = res.substring(begin+4);
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONObject(realCodes).getJSONArray("qfqday");
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
            stockKLineVo.setVolume(arr.getInteger(8));
            list.add(stockKLineVo);
        });
        return list;
    }

}
