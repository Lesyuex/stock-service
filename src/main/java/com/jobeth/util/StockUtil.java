package com.jobeth.util;

import com.jobeth.enums.ExceptionEnum;
import com.jobeth.excetion.StockException;
import lombok.Getter;

public class StockUtil {
    private final static String CN_STOCK_SH = "sh";
    private final static String CN_STOCK_SZ = "sz";
    private final static String HK_STOCK = "hk";

    @Getter
    private enum MarketEnum {
        //中国股票
        CN_STOCK("cn-stock"),
        //中国指数
        CN_INDEX("cn-index");
        private final String name;

        MarketEnum(String name) {
            this.name = name;
        }
    }

    /**
     * 根据market获取真实代码
     * 例如(maket:cn-stock,codes:000001,603138) => 最后生成 sz000001,sh603138
     * 例如(maket:cn-index,codes:000001,399006) => 最后生成 sh000001,sz399006
     *
     * @param market MarketEnum
     * @param codes  codes
     * @return realCodes
     */
    public static String getRealCodes(String market, String codes) {
        if (MarketEnum.CN_STOCK.getName().equals(market)) {
            return getCnStockCodes(codes, 0);
        } else if (MarketEnum.CN_INDEX.getName().equals(market)) {
            return getCnStockCodes(codes, 1);
        } else {
            throw new StockException(ExceptionEnum.UNKNOW_MARKET);
        }
    }

    /**
     * 将 000001 => (平安银行)sz000001 或者 (上证指数)sh000001
     *
     * @param codes 股票代码或者指数代码
     * @param type  生成代码的类型 0：股票，1：指数
     * @return 带前缀的代码
     */
    private static String getCnStockCodes(String codes, int type) {
        StringBuilder prefixCodes = new StringBuilder();
        String[] codeArr = codes.split(",");
        for (String code : codeArr) {
            String prefix = type == 0 ? getCnStockPrefix(code) : getCnIndexPrefix(code);
            prefixCodes.append(prefix);
            prefixCodes.append(code);
            prefixCodes.append(",");
        }
        int index = prefixCodes.lastIndexOf(",");
        return prefixCodes.deleteCharAt(index).toString();
    }

    /**
     * 获取中国股票前缀
     *
     * @param code 000001
     * @return sh/sz
     */
    private static String getCnStockPrefix(String code) {
        if ((!code.startsWith("6") &&
                !code.startsWith("9") &&
                !code.startsWith("0") &&
                !code.startsWith("2") &&
                !code.startsWith("3")) &&
                code.length() != 6) {
            throw new StockException(ExceptionEnum.ERROR_STOCK_CODE);
        }
        if (code.startsWith("6") || code.startsWith("9")) return CN_STOCK_SH;
        return CN_STOCK_SZ;
    }

    /**
     * 获取中国指数前缀
     *
     * @param code 000001
     * @return sh/sz
     */
    private static String getCnIndexPrefix(String code) {
        if ((!code.startsWith("0") && !code.startsWith("3")) || code.length() != 6) {
            throw new StockException(ExceptionEnum.ERROR_STOCK_CODE);
        }
        if (code.startsWith("0")) return CN_STOCK_SH;
        return CN_STOCK_SZ;
    }
}
