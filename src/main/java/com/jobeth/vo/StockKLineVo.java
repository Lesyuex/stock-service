package com.jobeth.vo;

import com.jobeth.annotion.QtIndex;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/1 2:33:33
 * Description: -
 */
@Data
public class StockKLineVo {

    /**
     * 日期
     */
    @QtIndex("0")
    private LocalDate date;

    /**
     * 开盘价
     */
    @QtIndex("1")
    private BigDecimal open;

    /**
     * 收盘价
     */
    @QtIndex("2")
    private BigDecimal close;

    /**
     * 最高价
     */
    @QtIndex("3")
    private BigDecimal highest;

    /**
     * 最低价
     */
    @QtIndex("4")
    private BigDecimal lowest;

    /**
     * 成交额
     */
    @QtIndex("5")
    private BigDecimal turnover;

    /**
     * 换手率
     */
    @QtIndex("7")
    private BigDecimal turnoverRate;

    /**
     * 成交量
     */
    @QtIndex("8")
    private Double volume;
}
