package com.jobeth.service;

import com.jobeth.po.PlateInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

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
    public void putPlate();
}
