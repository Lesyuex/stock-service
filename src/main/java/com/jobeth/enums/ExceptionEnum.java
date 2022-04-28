package com.jobeth.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {

    UNKNOW_MARKET(300, "未知市场"),
    ERROR_STOCK_CODE(301,"错误股票代码");
    private final Integer code;
    private final String message;

    ExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
