package com.jobeth.vo;

import lombok.Data;
/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
@Data
public class ResultVo<T> {
    private String msg;
    private Integer code;
    private T data;
    public static <T>  ResultVo<T> success(T data){
        ResultVo<T> resultVo = new ResultVo<>();
        resultVo.setCode(200);
        resultVo.setMsg("成功");
        resultVo.setData(data);
        return  resultVo;
    }
}
