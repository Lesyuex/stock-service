package com.jobeth.controller;

import com.jobeth.common.util.ResultUtils;
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
@RequestMapping("/clinch")
public class ClinchController {
    @Autowired
    private ClinchService clinchService;

    /**
     * 获取成交明细
     *
     * @param code code
     * @param size size
     * @return ResultVo
     * @throws Exception
     */
    @GetMapping("/get/{code}/{size}")
    public ResultVo<Object> getClinch( @PathVariable("code") String code, @PathVariable(value = "size") int size) throws Exception {
        List<ClinchDetailVo> clinchDetailVos = this.clinchService.queryMingxi( code, size);
        return ResultUtils.success(clinchDetailVos);
    }
}
