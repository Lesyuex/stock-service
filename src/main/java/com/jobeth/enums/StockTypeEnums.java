package com.jobeth.enums;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 13:11:11
 * Description: -
 */
@Getter
public enum StockTypeEnums {
    /**
     * A股-上证 6位纯数字，以6、9开头，查询股票详情时候拼接前缀“sh”
     * A股-深证 6位纯数字，以0、2、3开头，查询股票详情时候拼接前缀“sz”
     * 港股 5位数字，查询股票详情时候拼接“hk”
     */
    CHINA_STOCK_SZ(0,"sz"),
    CHINA_STOCK_SH(1,"sh");
    private final Integer code;
    private final String prefix;

    StockTypeEnums(int code, String prefix) {
        this.code = code;
        this.prefix = prefix;
    }
}
