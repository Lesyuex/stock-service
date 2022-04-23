package com.jobeth.controller;

import com.jobeth.service.IndexService;
import com.jobeth.vo.ResultVo;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockSingleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 17:12:12
 * Description: -指数查询接口
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private IndexService indexService;
    @GetMapping("/query/detail/{codes}")
    public ResultVo<Object> getDetail(@PathVariable("codes") String codes) throws Exception {
        List<StockDetailVo> stockVoList = this.indexService.queryDetail(codes);
        if (stockVoList.size() <= 1) {
            return ResultVo.success(stockVoList.get(0));
        }
        return ResultVo.success(stockVoList);
    }

    @GetMapping("/query/single/{codes}")
    public ResultVo<Object> getSingle(@PathVariable("codes") String codes) throws Exception {
        List<StockSingleVo> stockVoList = this.indexService.querySingle(codes);
        if (stockVoList.size() <= 1) {
            return ResultVo.success(stockVoList.get(0));
        }
        return ResultVo.success(stockVoList);
    }
}
