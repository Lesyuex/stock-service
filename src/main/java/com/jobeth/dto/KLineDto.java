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
    // 类型
    private int type;
    private String kType;
    private String code;
    private String startDate;
    private String endDate;
}
