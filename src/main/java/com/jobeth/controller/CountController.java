package com.jobeth.controller;

import com.jobeth.service.impl.CountServiceImpl;
import com.jobeth.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/23 19:42:42
 * Description: -
 */
@RestController
@RequestMapping("/count")
public class CountController {
    @Autowired
    public CountServiceImpl countService;
    @GetMapping("/get/detail")
    public ResultVo<Map<String,Integer>> count() throws Exception {
        Map<String, Integer> stringIntegerMap = this.countService.countUpAndDown();
        return ResultVo.success(stringIntegerMap);
    }

}
