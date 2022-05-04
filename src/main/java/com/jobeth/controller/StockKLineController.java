package com.jobeth.controller;


import com.jobeth.common.util.RestTemplateUtils;
import com.jobeth.common.util.ResultUtils;
import com.jobeth.dto.KLineDto;
import com.jobeth.service.StockInfoService;
import com.jobeth.service.StockKLineService;
import com.jobeth.vo.ResultVo;
import com.jobeth.vo.StockKLineVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * <p>
 * 日k数据 前端控制器
 * </p>
 *
 * @author jobeth
 * @since 2022-04-30
 */
@RestController
@RequestMapping("/k")
public class StockKLineController {
    @Autowired
    private StockKLineService stockKLineService;
    @Autowired
    private StockInfoService stockInfoService;

    @PostMapping("/query")
    public ResultVo<List<StockKLineVo>> query(@RequestBody KLineDto kLineDto) throws FileNotFoundException {
        List<StockKLineVo> lsit = this.stockKLineService.queryK(kLineDto);
        return ResultUtils.success(lsit);
    }
}

