package com.jobeth.service;

import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockSingleVo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 17:13:13
 * Description: -
 */
public interface IndexService {
    List<StockDetailVo> queryDetail(String codes) throws Exception;
    List<StockSingleVo> querySingle(String codes) throws Exception;

}
