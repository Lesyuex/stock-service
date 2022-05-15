package com.jobeth.controller;


import com.jobeth.common.enums.ResultEnum;
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
import java.util.Map;

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

    /**
     * 日k 周k 月k 季k 年k
     * 1分钟k 5分钟k 15分钟k 30分钟k 60分钟k 120分钟k
     * @param kLineDto kLineDto
     * @return ResultVo
     */
    @PostMapping("/get")
    public ResultVo<List<StockKLineVo>> query(@RequestBody KLineDto kLineDto){
        if (kLineDto.getKtype() == 0){
            List<StockKLineVo> list = this.stockKLineService.queryK(kLineDto);
            return ResultUtils.success(list);
        }else if (kLineDto.getKtype() ==1){
            List<StockKLineVo> minuK = this.stockKLineService.getMinuK(kLineDto);
            return ResultUtils.success(minuK);
        }
        return ResultUtils.fail(ResultEnum.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/get/fiveday/line/{code}")
    public ResultVo<Object> fiveday(@PathVariable("code") String code) throws Exception {
        Map<String, Object> fiveday = this.stockKLineService.getFiveday(code);
        return ResultUtils.success(fiveday);
    }
}


