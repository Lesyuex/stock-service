package com.jobeth.vo;

import com.jobeth.annotion.QtIndex;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MinutesVo {
    @QtIndex("0")
    private String time;
    @QtIndex("1")
    public BigDecimal price;
    @QtIndex("2")
    public BigDecimal volume;
    @QtIndex("3")
    public BigDecimal clinch;
    public BigDecimal minuVolume;
    public BigDecimal averagePrice;
    private BigDecimal changeValue;
    private BigDecimal percent;
}
