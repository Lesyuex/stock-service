package com.jobeth.controller;

import com.jobeth.common.util.ResultUtils;
import com.jobeth.service.IndexService;
import com.jobeth.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 指数接口.
 *
 * @author JyrpoKoo
 * @date 2022/5/6 21:23:23
 * Description: -
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private IndexService indexService;

    /**
     * 获取最新分时数据
     *
     * @param code code
     * @return 最新分时
     * @throws Exception Exception
     */
    @GetMapping("/get/minutes/{code}")
    public ResultVo<Map<String, Object>> getMinuites(@PathVariable("code") String code) throws Exception {
        Map<String, Object> stringObjectMap = this.indexService.queryMinutes(code);
        return ResultUtils.success(stringObjectMap);
    }
}
