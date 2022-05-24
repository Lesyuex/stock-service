package com.jobeth.vo;

import com.jobeth.annotion.EastFiledName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlateLeadUpDownVo {
    /**
     * 1:行业
     * 2:概念
     * 3:地域
     */
    @EastFiledName(value="f1")
    private String categoryType;

    /**
     * 代码
     */
    @EastFiledName(value="f12")
    private String code;

    /**
     * 名字
     */
    @EastFiledName(value="f14")
    private String name;

    /**
     * 分类名字
     */
    private String categoryName;
    /**当前价 涨跌幅 涨跌值 总市值*/
    @EastFiledName(value="f2")
    private BigDecimal price;
    @EastFiledName(value="f3")
    private BigDecimal percent;
    @EastFiledName(value="f4")
    private BigDecimal changeValue;
    @EastFiledName(value="f20")
    private BigDecimal marketValue;
    /**领涨*/
    @EastFiledName(value="f128")
    private String leadUpName;
    @EastFiledName(value="f140")
    private String leadUpCode;
    @EastFiledName(value="f136")
    private BigDecimal leadUpPercent;

    /**领跌*/
    @EastFiledName(value="f207")
    private String leadDownName;
    @EastFiledName(value="f208")
    private String leadDownCode;
    @EastFiledName(value="f222")
    private BigDecimal leadDownPercent;
}
