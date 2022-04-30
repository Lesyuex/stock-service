package com.jobeth.controller;

import com.jobeth.common.util.ResultUtils;
import com.jobeth.service.FundingService;
import com.jobeth.vo.FundingVo;
import com.jobeth.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/funding")
public class FundingCotroller {
    @Autowired
    private FundingService fundingService;

    @GetMapping("/get/minutes/detail")
    public ResultVo<Map<String, List<FundingVo>>> getMinutes() throws Exception {
        Map<String, List<FundingVo>> minutes = this.fundingService.getMinutes();
        return ResultUtils.success(minutes);
    }
}
