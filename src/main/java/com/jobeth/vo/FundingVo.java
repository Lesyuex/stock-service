package com.jobeth.vo;

import com.jobeth.annotion.QtIndex;
import lombok.Data;

@Data
public class FundingVo {
    @QtIndex(value = "0",desc = "时间")
    private String time;
    @QtIndex(value = "1",desc = "沪股通净买额")
    private String shDiff;
    @QtIndex(value = "2",desc = "沪股通买入")
    private String shBuy;
    @QtIndex(value = "3",desc = "深股通净买额")
    private String szDiff;
    @QtIndex(value = "4",desc = "沪股通卖出")
    private String shSell;
    @QtIndex(value = "5",desc = "北向资金净买入额")
    private String foudingDiff;

    @QtIndex(value = "6",desc = "沪股通买入")
    private String szBuy;
    @QtIndex(value = "7",desc = "沪股通卖出")
    private String szSell;

    @QtIndex(value = "8",desc = "北向资金买入")
    private String fundingBuy;
    @QtIndex(value = "9",desc = "北向资金买入")
    private String fundingSell;
}
