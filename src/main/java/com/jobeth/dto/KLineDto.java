package com.jobeth.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/1 0:31:31
 * Description: -
 */
@Data
public class KLineDto {
    // 类型 day week month seson year   m1 m5 m15 m30 m60 m120
    private String kname;
    private int ktype;
    private String code;
    private String startDate;
    private String endDate;
    private int stockType;
}
