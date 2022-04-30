package com.jobeth.dto;

import com.jobeth.po.PlateStockInfo;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/21 20:22:22
 * Description: -
 */
@Data
public class PlateStocksInfoDto {
    /**
     * 代码
     */
    private String code;

    /**
     * 名字
     */
    private String name;

    /**
     * 1:行业
     * 2:概念
     * 3:地域
     */
    private String categoryType;

    /**
     * 分类名字
     */
    private String categoryName;
    private List<PlateStockInfo> list;
}
