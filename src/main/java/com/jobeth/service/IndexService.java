package com.jobeth.service;

import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockSingleVo;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/6 21:24:24
 * Description: -
 */
public interface IndexService {

    /**
     * 查询股票详细信息 可批量
     *
     * @param codes code
     * @return List<StockVo>
     */
    public List<StockDetailVo> getDetail(String codes) throws Exception;

    List<StockSingleVo> getSingle(String codes) throws Exception;

    Map<String, Object> queryMinutes(String code) throws Exception;
}
