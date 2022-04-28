package com.jobeth.vo;

import com.jobeth.annotion.QtIndex;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClinchDetailVo implements Serializable {
    @QtIndex(value = "0",desc = "第几笔交易")
    private Integer clinchNum;
    @QtIndex(value = "1",desc = "交易时间")
    private String time;
    @QtIndex(value = "2",desc = "交易价格")
    private Double price;
    @QtIndex(value = "3",desc = "涨跌")
    private Double diff;
    @QtIndex(value = "4",desc = "成交量")
    private Integer volume;
    @QtIndex(value = "5",desc = "成交额")
    private Double turnover;
    @QtIndex(value = "6",desc = "主动买卖")
    private String mark;
}
