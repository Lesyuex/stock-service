package com.jobeth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jobeth.enums.RequestUrlEnums;
import com.jobeth.service.StockInfoService;
import com.jobeth.util.HttpClientUtil;
import com.jobeth.util.StringUtil;
import com.jobeth.vo.ResultVo;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -股票查询接口
 */
@RestController
@RequestMapping("/{market}")
public class StockInfoController {
    @Autowired
    private StockInfoService stockInfoService;

    @GetMapping("/query/detail/{codes}")
    public ResultVo<Object> getDetail(@PathVariable("market") String market, @PathVariable("codes") String codes) throws Exception {
        List<StockDetailVo> stockDetailVoList = this.stockInfoService.query(codes);
        if (stockDetailVoList.size() <= 1) {
            return ResultVo.success(stockDetailVoList.get(0));
        }
        return ResultVo.success(stockDetailVoList);
    }

    @PutMapping("/update/all")
    public ResultVo<Boolean> putStock() throws Exception {
        this.stockInfoService.putStock();
        return ResultVo.success(true);
    }

    @GetMapping("/list/all")
    public ResultVo<List<StockInfoVo>> listAll() {
        List<StockInfoVo> list = this.stockInfoService.listAll();
        return ResultVo.success(list);
    }

    @GetMapping("/quey/minutes/{code}")
    public ResultVo<Map<String, Object>> getMinuites(@PathVariable("market") String market, @PathVariable("code") String code) throws Exception {
        Map<String, Object> stringObjectMap = this.stockInfoService.queryMinutes(market, code);
        return ResultVo.success(stringObjectMap);
    }
}
