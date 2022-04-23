package com.jobeth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jobeth.entity.StockInfo;
import com.jobeth.mapper.StockInfoMapper;
import com.jobeth.service.CountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/23 19:43:43
 * Description: -
 */
@Service
public class CountServiceImpl implements CountService {

    @Autowired
    private StockInfoMapper stockInfoMapper;
    /**
     * 市场总览
     * @return Map
     */
    @Override
    public Map<String, Integer> countUpAndDown() {
        // 先查找所有股票代码
        QueryWrapper<StockInfo> query = new QueryWrapper<>();
        query.select("");
        /*stockInfoMapper.selectList();*/
        return null;
    }
}
