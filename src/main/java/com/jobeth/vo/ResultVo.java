package com.jobeth.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jobeth.common.enums.ResultEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "返回数据Model")
public class ResultVo<T> {

    /**
     * 状态码
     */
    @ApiModelProperty(value = "接口状态码", name = "code", required = true)
    private Integer code;
    /**
     * 信息
     */
    @ApiModelProperty(value = "接口信息提示", name = "message", required = true)
    private String message;
    @ApiModelProperty(value = "数据", name = "data")
    private T data;

    public ResultVo() {
        this.code = 200;
        this.message = "操作成功！";
    }

    public ResultVo(T data) {
        this.code = 200;
        this.message = "操作成功！";
        this.data = data;
    }

    public ResultVo(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public ResultVo(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultVo(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    public ResultVo(ResultEnum resultEnum, T data) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.data = data;
    }
}