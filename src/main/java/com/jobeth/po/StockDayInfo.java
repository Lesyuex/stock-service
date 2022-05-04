package com.jobeth.po;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 日k数据
 * </p>
 *
 * @author jobeth
 * @since 2022-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StockDayInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 股票代码
     */
    private String code;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 开盘价
     */
    private BigDecimal open;

    /**
     * 收盘价
     */
    private BigDecimal close;

    /**
     * 最高价
     */
    private BigDecimal highest;

    /**
     * 最低价
     */
    private BigDecimal lowest;

    /**
     * 成交量
     */
    private Integer volume;

    /**
     * 成交额
     */
    private Long turnover;

    /**
     * 换手率
     */
    private BigDecimal turnoverRate;


}
