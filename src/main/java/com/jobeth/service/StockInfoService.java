package com.jobeth.service;

import com.jobeth.vo.ClinchDetailVo;
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
public interface StockInfoService {
    /**
     * 查询股票详细信息 可批量
     *
     * @param codes code
     * @return List<StockVo>
     */
    public List<StockDetailVo> getDetail(int type,String codes) throws Exception;

    List<StockSingleVo> getSingle(int type,String codes) throws Exception;

    /**
     * 更新上证所的股票到数据库
     */
    public void putStock() throws Exception;

    /**
     * 查询A股所有股票
     *
     * @return List<StockInfo>
     */
    List<StockInfoVo> listAll();

    Map<String, Object> queryMinutes(int type, String code) throws Exception;


}
