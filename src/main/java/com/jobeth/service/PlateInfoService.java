package com.jobeth.service;

import com.jobeth.po.PlateInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jobeth.vo.PlateLeadUpDownVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 板块 服务类
 * </p>
 *
 * @author jobeth
 * @since 2022-04-21
 */
public interface PlateInfoService extends IService<PlateInfo> {
    /**
     * 添加板块
     */
    @Transactional(rollbackFor = Exception.class)
    void putPlate();

   Map<String, List<PlateLeadUpDownVo>> getLeadUpAndDown(int cateType) throws Exception;
}
