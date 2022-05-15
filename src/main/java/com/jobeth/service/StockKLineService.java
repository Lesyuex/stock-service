package com.jobeth.service;

import com.jobeth.dto.KLineDto;
import com.jobeth.po.StockDayInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jobeth.vo.StockKLineVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 日k数据 服务类
 * </p>
 *
 * @author jobeth
 * @since 2022-04-30
 */
public interface StockKLineService extends IService<StockDayInfo> {
    List<StockKLineVo> queryK(KLineDto dto);
    List<StockKLineVo> getMinuK(KLineDto dto);
    Map<String, Object> getFiveday(String code) throws Exception;
}
