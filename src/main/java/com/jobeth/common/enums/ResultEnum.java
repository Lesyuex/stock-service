package com.jobeth.common.enums;
import lombok.Getter;
/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/1 1:00:00
 * Description: -
 */

@Getter
public enum ResultEnum {
    /**
     * 正常
     */
    SUCCESS(200, "操作成功！"),
    /**
     * 操作失败
     */
    FAIL(400, "操作失败！"),
    UNKNOW_MARKET(300, "未知市场"),
    ERROR_STOCK_CODE(301,"错误股票代码"),
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器发生错误..."),
    /**
     * 请求参数错误
     */
    REQUEST_PARAMETER_ERROR(501, "请求参数错误！"),
    /**
     * 未登录
     */
    USER_NOT_LOGIN(502, "未登录"),
    /**
     * Token过期或者无效
     */
    USER_TOKEN_INVALID(504, "用户登录无效或已过期！"),
    /**
     * 登录失败
     */
    USER_LOGIN_FAIL(505, "请检查用户名或用户密码！"),
    /**
     * 没有权限
     */
    USER_ACCESS_DENIED(507, "权限不足！"),

    /**
     * 系统无此资源
     */
    SERVER_NO_THIS_SOURCE(508, "访问资源不存在"),
    /**
     * 访问地址有误
     */
    CHECK_REQUEST_URL(997, "不存在的访问地址！"),
    /**
     * 访问方式有误
     */
    CHECK_REQUEST_METHOD(998, "不支持的访问方式！"),
    /**
     * 未知
     */
    UNKNOWN(999, "未知...");


    private final Integer code;
    private final String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}