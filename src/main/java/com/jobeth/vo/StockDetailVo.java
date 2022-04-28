package com.jobeth.vo;
import com.jobeth.annotion.QtIndex;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
@Data
public class StockDetailVo implements Serializable {

    @QtIndex(value = "1",desc = "股票名字")
    private String name;
    @QtIndex(value = "2",desc = "股票代码")
    private String code;
    @QtIndex(value = "3",desc = "当前价格")
    private Double currentPrice;
    @QtIndex(value = "4",desc = "昨收")
    private Double yesterdayPrice;
    @QtIndex(value = "5",desc = "今开")
    private Double todayOpenPrice;
    @QtIndex(value = "6",desc = "成交量（手）")
    private Integer volume;
    @QtIndex(value = "7",desc = "外盘")
    private Integer outerDisk;
    @QtIndex(value = "8",desc = "内盘")
    private Integer innerPlate;
    @QtIndex(value = "9",desc = "买一")
    private Double buyFirstPrice;
    @QtIndex(value = "10",desc = "买一量（手）")
    private Integer buyFirstVolume;
    @QtIndex(value = "11",desc = "买二")
    private Double buySecondPrice;
    @QtIndex(value = "12",desc = "买二量（手）")
    private Integer buySecondVolume;
    @QtIndex(value = "13",desc = "买三")
    private Double buyThirdPrice;
    @QtIndex(value = "14",desc = "买三量（手）")
    private Integer buyThirdVolume;
    @QtIndex(value = "15",desc = "买四")
    private Double buyForthPrice;
    @QtIndex(value = "16",desc = "买四量（手）")
    private Integer buyForthVolume;
    @QtIndex(value = "17",desc = "买五")
    private Double buyFifthPrice;
    @QtIndex(value = "18",desc = "买五量（手")
    private Integer buyFifthVolume;
    @QtIndex(value = "19",desc = "卖一")
    private Double sellFirstPrice;
    @QtIndex(value = "20",desc = "卖一量（手）")
    private Integer sellFirstVolume;
    @QtIndex(value = "21",desc = "卖二")
    private Double sellSecondPrice;
    @QtIndex(value = "22",desc = "卖二量（手）")
    private Integer sellSecondVolume;
    @QtIndex(value = "23",desc = "卖三")
    private Double sellThirdPrice;
    @QtIndex(value = "24",desc = "卖三量（手）")
    private Integer sellThirdVolume;
    @QtIndex(value = "25",desc = "卖四")
    private Double sellForthPrice;
    @QtIndex(value = "26",desc = "卖四量（手）")
    private Integer sellForthVolume;
    @QtIndex(value = "27",desc = "卖五")
    private Double sellFifthPrice;
    @QtIndex(value = "28",desc = "卖五量（手）")
    private Integer sellFifthVolume;
    @QtIndex(value = "29",desc = "最近逐笔成交")
    private String recentClinch;
    @QtIndex(value = "30",desc = "时间 2022-04-11 11:11:54")
    private String currentTime;
    @QtIndex(value = "31",desc = "涨跌")
    private Double upDownValue;
    @QtIndex(value = "32",desc = "涨跌%")
    private Double upDownPercent;
    @QtIndex(value = "33",desc = "最高")
    private Double highestPrice;
    @QtIndex(value = "34",desc = "最低")
    private Double lowestPrice;
    @QtIndex(value = "35",desc = "价格/成交量（手）/成交额 1710.00/27043/4681914206")
    private String clinchInfo;
    @QtIndex(value = "36",desc = "成交量（手）")
    private Integer volume2;
    @QtIndex(value = "37",desc = "成交额（万）")
    private Integer turnover;
    @QtIndex(value = "38",desc = "换手率")
    private Double turnoverRate;
    @QtIndex(value = "39",desc = "市盈率")
    private Double peRatio;
    @QtIndex(value = "40",desc = "-")
    private String forty;
    @QtIndex(value = "41",desc = "最高")
    private Double highest;
    @QtIndex(value = "42",desc = "最低")
    private Double lowest;
    @QtIndex(value = "43",desc = "振幅")
    private Double amplitude;
    @QtIndex(value = "44",desc = "流通市值")
    private Double circulatingMarketValue;
    @QtIndex(value = "45",desc = "总市值")
    private Double marketValue;
    @QtIndex(value = "46",desc = "市净率")
    private String pbRatio;
    @QtIndex(value = "47",desc = "涨停价")
    private Double dailyLimitPrice;
    @QtIndex(value = "48",desc = "跌停价")
    private Double limitDownPrice;
    @QtIndex(value = "49",desc = "量比")
    private String volumeRate;
    @QtIndex(value = "51",desc = "均价")
    private Double averagePrice;
    @QtIndex(value = "52",desc = "动态市盈率")
    private Double dyPriceRate;
    @QtIndex(value = "52",desc = "静态市盈率")
    private Double staPriceRate;
    @QtIndex(value = "52",desc = "成交额")
    private Double turnOver;
    public static StockDetailVo generateStockByStrArr(String[] arr) throws IllegalAccessException {
        StockDetailVo shareVo = new StockDetailVo();
        Class<?> aClass = shareVo.getClass();
        Field[] filedArr = aClass.getDeclaredFields();
        for (Field filed : filedArr) {
            QtIndex annotation = filed.getAnnotation(QtIndex.class);
            String value = null;
            if (annotation != null) {
                value = annotation.value();
                int i = Integer.parseInt(value);
                filed.setAccessible(true);
                filed.set(shareVo,arr[i]);
            }
        }
        return shareVo;
    }
}
