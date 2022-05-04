package com.jobeth.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/1 4:24:24
 * Description: -
 */
@Data
public class StockDto {

    @ExcelProperty("板块")
    private String boardName;

    /**
     * 股票全称
     */
    @ExcelProperty("公司全称")
    private String secNameFull;

    /**
     * 股票全称（英语）
     */
    @ExcelProperty("英文名称")
    private String companyAbbrEn;
    /**
     * 公司地址
     */
    @ExcelProperty("注册地址")
    private String companyAbbr;

    /**
     * 公司网址
     */
    @ExcelProperty("公司网址")
    private String companyNet;

    /**
     * A股代码
     */
    @ExcelProperty("A股代码")
    private String astockCode;
    /**
     * A股简称
     */
    @ExcelProperty("A股简称")
    private String astockName;

    /**
     * A股上市日期
     */
    @ExcelProperty("A股上市日期")
    private String alistDate;

    /**
     * B股代码
     */
    @ExcelProperty("B股代码")
    private String bstockCode;

    /**
     * B股简称
     */
    @ExcelProperty("B股简称")
    private String bstockName;

    /**
     * B股上市日期
     */
    @ExcelProperty("B股上市日期")
    private String blistDate;

}
