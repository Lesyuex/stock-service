package com.jobeth.vo;

import com.jobeth.annotion.QtIndex;
import lombok.Data;

@Data
public class FundingVo {
    @QtIndex(value = "0",desc = "时间")
    private String time;
    @QtIndex(value = "1",desc = "沪股通净买额")
    private Double shDiff;
    @QtIndex(value = "2",desc = "沪股通买入")
    private Double shBuy;
    @QtIndex(value = "3",desc = "深股通净买额")
    private Double szDiff;
    @QtIndex(value = "4",desc = "沪股通卖出")
    private Double shSell;
    @QtIndex(value = "5",desc = "北向资金净买入额")
    private Double foudingDiff;

    @QtIndex(value = "6",desc = "沪股通买入")
    private Double szBuy;
    @QtIndex(value = "7",desc = "沪股通卖出")
    private Double szSell;

    @QtIndex(value = "8",desc = "北向资金买入")
    private Double fundingBuy;
    @QtIndex(value = "9",desc = "北向资金买入")
    private Double fundingSell;
}
