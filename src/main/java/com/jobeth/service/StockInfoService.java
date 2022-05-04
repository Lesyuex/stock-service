package com.jobeth.service;

import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockInfoVo;
import com.jobeth.vo.StockSingleVo;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:53:53
 * Description: -
 */
public interface StockInfoService{
    /**
     * 查询股票详细信息 可批量
     *
     * @param codes code
     * @return List<StockVo>
     */
    public List<StockDetailVo> getDetail(String codes) throws Exception;

    List<StockSingleVo> getSingle(String codes) throws Exception;

    /**
     * 更新上证所的股票到数据库
     */
    void putStock() throws Exception;

    /**
     * 查询A股所有股票
     *
     * @return List<StockInfo>
     */
    List<StockInfoVo> listAll();

    Map<String, Object> queryMinutes(String code) throws Exception;
}
