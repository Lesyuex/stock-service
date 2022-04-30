package com.jobeth.common.excetion;

import com.jobeth.common.enums.ExceptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StockException extends RuntimeException {
    private Integer code;
    private String message;

    public StockException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public StockException(ExceptionEnum exceptionEnum){
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }
}
