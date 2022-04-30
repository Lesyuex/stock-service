package com.jobeth.po;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 板块成分股
 * </p>
 *
 * @author jobeth
 * @since 2022-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PlateStockInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 板块编码
     */
    private String plateCode;

    /**
     * 股票简称
     */
    private String stockName;
    /**
     * 股票代码
     */
    private String stockCode;

}
