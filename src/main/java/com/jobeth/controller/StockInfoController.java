package com.jobeth.controller;

import com.jobeth.common.util.ResultUtils;
import com.jobeth.service.StockInfoService;
import com.jobeth.vo.ResultVo;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockInfoVo;
import com.jobeth.vo.StockSingleVo;
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
@RequestMapping("/stock")
public class StockInfoController {
    @Autowired
    private StockInfoService stockInfoService;


    /**
     * 获取A股所有股票更新到数据库
     *
     * @return 状态
     * @throws Exception Exception
     */
    @PutMapping("/update/all")
    public ResultVo<Boolean> putStock() throws Exception {
        this.stockInfoService.putStock();
        return ResultUtils.success(true);
    }

    /**
     * 查询所有股票
     *
     * @return 所有股票
     */
    @GetMapping("/list/all")
    public ResultVo<List<StockInfoVo>> listAll() {
        List<StockInfoVo> list = this.stockInfoService.listAll();
        return ResultUtils.success(list);
    }

    /**
     * 获取最新分时数据
     *
     * @param type type 1 股票 2 指数 3 基金
     * @param code code 股票代码 带前缀
     * @return 最新分时
     * @throws Exception Exception
     */
    @GetMapping("/get/minutes/{type}/{code}")
    public ResultVo<Map<String, Object>> getMinuites(@PathVariable("type") int type, @PathVariable("code") String code) throws Exception {
        Map<String, Object> stringObjectMap = this.stockInfoService.queryMinutes(type,code);
        return ResultUtils.success(stringObjectMap);
    }

    /**
     * 获取最新简单信息
     *
     * @param codes codes
     * @return 最新详细
     * @throws Exception Exception
     */
    @GetMapping("/get/single/{codes}")
    public ResultVo<Object> getSingle(@PathVariable("codes") String codes) throws Exception {
        List<StockSingleVo> stockVoList = this.stockInfoService.getSingle(codes);
        if (stockVoList.size() == 1) {
            return ResultUtils.success(stockVoList.get(0));
        }
        return ResultUtils.success(stockVoList);
    }

    /**
     * 获取最新详细
     *
     * @param codes codes
     * @return 最新详细
     * @throws Exception Exception
     */
    @GetMapping("/get/detail/{codes}")
    public ResultVo<Object> getDetail(@PathVariable("codes") String codes) throws Exception {
        List<StockDetailVo> stockDetailVoList = this.stockInfoService.getDetail(codes);
        if (stockDetailVoList.size() == 1) {
            return ResultUtils.success(stockDetailVoList.get(0));
        }
        return ResultUtils.success(stockDetailVoList);
    }
}
