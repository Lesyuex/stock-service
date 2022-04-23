package com.jobeth.vo;

import com.jobeth.annotion.QtIndex;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/23 2:10:10
 * Description: -
 */
@Data
public class StockSingleVo implements Serializable {
    /**
     * 代表交易所，200-美股（us），100-港股（hk），51-深圳（sz），1-上海（sh）
     */
    @QtIndex(value = "0",desc = "代表交易所")
    private String market;
    @QtIndex(value = "1",desc = "股票名字")
    private String name;
    @QtIndex(value = "2",desc = "股票代码")
    private String code;
    @QtIndex(value = "3",desc = "当前价格")
    private String currentPrice;
    @QtIndex(value = "4",desc = "涨跌")
    private String upDownValue;
    @QtIndex(value = "5",desc = "涨跌%")
    private String upDownPercent;
    @QtIndex(value = "6",desc = "成交量（手）")
    private String volume;
    @QtIndex(value = "7",desc = "价格/成交量（手）/成交额 1710.00/27043/4681914206")
    private String clinchInfo;
    @QtIndex(value = "9",desc = "总市值")
    private String marketValue;
}
