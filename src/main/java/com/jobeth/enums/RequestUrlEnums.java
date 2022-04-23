package com.jobeth.enums;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 15:45:45
 * Description: -
 */
@Getter
public enum RequestUrlEnums {
    // 腾讯批量查询详细
    TENCENT_BATCH_QUERY_DETAIL_URL("https://web.sqt.gtimg.cn/utf8/q=","https://web.sqt.gtimg.cn/utf8/q=sh603138,sz000001,");
    private final String url;
    private final String desc;

    RequestUrlEnums(String url, String desc) {
        this.url = url;
        this.desc = desc;
    }
}
