package com.jobeth.common.util;
import com.jobeth.common.enums.ResultEnum;
import com.jobeth.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/1 1:04:04
 * Description: -
 */
@Slf4j
public class ResultUtils {
    private ResultUtils() {
    }

    /**
     * 根据枚举类型输出结果
     *
     * @param response   response
     * @param resultEnum resultEnum
     */
    public static void writeJson(ServletResponse response, ResultEnum resultEnum) {
        ResultVo<Object> result = buildResult(resultEnum);
        writeJson(response, result);
    }

    /**
     * 响应Json数据
     *
     * @param response response
     * @param object   object
     */
    public static void writeJson(ServletResponse response, Object object) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(JacksonUtil.objectToJson(object) + "");
            log.info("【 ResponseJson - {} 】", JacksonUtil.objectToJson(object));
        } catch (Exception e) {
            log.error("【 ResponseJson - 发生错误- {} 】", e.getMessage());
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 操作正常结果
     *
     * @return 操作正常结果
     */
    public static <T> ResultVo<T> success() {
        return buildResult(ResultEnum.SUCCESS);
    }
    public static <T> ResultVo<T> success(T data) {
        return buildResult(ResultEnum.SUCCESS,data);
    }
    /**
     * 根据枚举类型输出结果
     *
     * @return 结果
     */
    public static <T> ResultVo<T> buildResult(ResultEnum resultEnum, T data) {
        ResultVo<T> resultVo = new ResultVo<>();
        resultVo.setCode(resultEnum.getCode());
        resultVo.setMessage(resultEnum.getMessage());
        resultVo.setData(data);
        return resultVo;
    }



    /**
     * 根据枚举类型输出结果
     *
     * @return 结果
     */
    public static <T> ResultVo<T> buildResult(ResultEnum resultEnum) {
        return buildResult(resultEnum, null);
    }

    /**
     * 操作失败结果
     *
     * @return 操作失败结果
     */
    public static <T> ResultVo<T> fail() {
        return fail(ResultEnum.FAIL);
    }

    /**
     * 操作失败结果
     *
     * @return 操作失败结果
     */
    public static <T> ResultVo<T> fail(ResultEnum resultEnum) {
        return buildResult(resultEnum);
    }


}
