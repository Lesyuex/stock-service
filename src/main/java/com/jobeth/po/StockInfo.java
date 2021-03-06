package com.jobeth.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 股票信息表
 * </p>
 *
 * @author jobeth
 * @since 2022-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StockInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 股票代码带市场
     */
    @TableId(value = "MARKET_CODE", type = IdType.INPUT)
    private String marketCode;
    /**
     * 股票代码
     */
    @TableField("A_STOCK_CODE")
    private String aStockCode;

    /**
     * B股代码
     */
    @TableField("B_STOCK_CODE")
    private String bStockCode;

    /**
     * 股票简称
     */
    @TableField("SEC_NAME_CN")
    private String secNameCn;

    /**
     * 股票全称
     */
    @TableField("SEC_NAME_FULL")
    private String secNameFull;

    /**
     * 公司名称（英语）
     */
    @TableField("COMPANY_ABBR_EN")
    private String companyAbbrEn;

    /**
     * 公司名称
     */
    @TableField("COMPANY_ABBR")
    private String companyAbbr;

    /**
     * 股票代码
     */
    @TableField("COMPANY_CODE")
    private String companyCode;

    /**
     * 上市时间
     */
    @TableField("LIST_DATE")
    private LocalDate listDate;

    /**
     * 上市板块
     */
    @TableField("LIST_BOARD")
    private String listBoard;

    /**
     * A： A股
     * B：B股
     */
    @TableField("AB_FLAG")
    private String abFlag;

}
