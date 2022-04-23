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
     * A股-上证 6位纯数字，以600、601、603、605、900开头，查询股票详情时候拼接前缀“sh”
     * 沪市A股 沪市A股的代码是以600、601或603打头
     * 沪市B股 沪市B股的代码是以900打头
     * A股-深证 6位纯数字，以000、002、003、200、300开头，查询股票详情时候拼接前缀“sz”
     * 深市A股 深市A股的代码是以000打头
     * 中小板 中小板的代码是002打头
     * 深圳B股 深圳B股的代码是以200打头
     * 300开头是创业板
     * 400开头的股票是三板市场股票。
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
