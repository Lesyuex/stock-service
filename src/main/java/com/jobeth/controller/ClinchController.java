package com.jobeth.controller;

import com.jobeth.service.ClinchService;
import com.jobeth.vo.ClinchDetailVo;
import com.jobeth.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/{market}")
public class ClinchController {
    @Autowired
    private ClinchService clinchService;

    @GetMapping("/query/clinch/{code}/{size}")
    public ResultVo<Object> getClinch(@PathVariable("market") String market, @PathVariable("code") String code, @PathVariable(value = "size") int size) throws Exception {
        List<ClinchDetailVo> clinchDetailVos = this.clinchService.queryMingxi(market, code, size);
        return ResultVo.success(clinchDetailVos);
    }
}
