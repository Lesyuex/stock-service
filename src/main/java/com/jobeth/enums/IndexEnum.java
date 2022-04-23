package com.jobeth.enums;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 17:32:32
 * Description: -
 */
@Getter
public enum IndexEnum {
    /**
     * 0开头的是上交所 定义的指数
     * 3开头的是深交所 定义的指数
     */
    CHINA_INDEX_SZ("sz"),
    CHINA_INDEX_SH("sh");
    private final String prefix;

    IndexEnum( String prefix) {
        this.prefix = prefix;
    }
}
