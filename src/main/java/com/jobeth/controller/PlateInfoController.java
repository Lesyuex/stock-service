package com.jobeth.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jobeth.common.util.ResultUtils;
import com.jobeth.po.PlateInfo;
import com.jobeth.service.PlateInfoService;
import com.jobeth.vo.PlateLeadUpDownVo;
import com.jobeth.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 板块 前端控制器
 * </p>
 *
 * @author jobeth
 * @since 2022-04-21
 */
@RestController
@RequestMapping("/plate")
public class PlateInfoController {
    @Autowired
    private PlateInfoService plateInfoService;

    @PutMapping("/update/all")
    public ResultVo<Boolean> putPlate() throws Exception {
        this.plateInfoService.putPlate();
        return ResultUtils.success(true);
    }

    @GetMapping("/list/category/{categoryType}")
    public ResultVo<List<PlateInfo>> listByCategoryType(@PathVariable("categoryType") String categoryType) {
        QueryWrapper<PlateInfo> query = new QueryWrapper<>();
        query.select("code", "name","category_type","category_name");
        query.eq("category_type", categoryType);
        List<PlateInfo> list = this.plateInfoService.list(query);
        return ResultUtils.success(list);
    }

    /**
     *
     * @return
     */
    @GetMapping("/get/leadUpAndDown/{categoryType}")
    public ResultVo<Object> getLeadUpAndDown(@PathVariable("categoryType") int categoryType) throws Exception {
        Map<String, List<PlateLeadUpDownVo>> leadUpAndDown = this.plateInfoService.getLeadUpAndDown(categoryType);
        return ResultUtils.success(leadUpAndDown);
    }
}

