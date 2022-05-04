package com.jobeth.mapper;

import com.jobeth.po.StockInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;

/**
 * <p>
 * 股票信息表 Mapper 接口
 * </p>
 *
 * @author jobeth
 * @since 2022-04-18
 */
public interface StockInfoMapper extends BaseMapper<StockInfo> {
    void updateSzBStock(StockInfo stockInfo);
}
