package com.jobeth.po;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * 板块
 * </p>
 *
 * @author jobeth
 * @since 2022-04-21
 */
@Data
public class PlateInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 代码
     */
    private String code;

    /**
     * 名字
     */
    private String name;

    /**
     * 1:行业
     * 2:概念
     * 3:地域
     */
    private String categoryType;

    /**
     * 分类名字
     */
    private String categoryName;
}
