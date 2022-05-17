package com.jobeth.vo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/19 16:36:36
 * Description: -
 */
@Data
public class StockInfoVo {
    private static final long serialVersionUID = 1L;

    /**
     * 股票代码
     */
    private String marketCode;
    /**
     * 股票简称
     */
    private String secNameCn;

    /**
     * 类型 1 股票 2 指数 3 板块 4 基金
     */
    private int type;
}
