package com.jobeth.service.impl;

import com.jobeth.service.IndexService;
import com.jobeth.common.util.PropertiesUtils;
import com.jobeth.common.util.ReflectionUtils;
import com.jobeth.common.util.RestTemplateUtils;
import com.jobeth.common.util.StringUtils;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockSingleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 17:13:13
 * Description: -
 */
@Service
@Slf4j
public class IndexServiceImpl implements IndexService {

    /**
     * 查询指数详细信息（可批量上证指数，深证成指 000001，399001）
     *
     * @param codes codes
     * @return 指数信息
     * @throws Exception Exception
     */
    @Override
    public List<StockDetailVo> queryDetail(String codes) throws Exception {
        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtils.getByKey("txBatchDetail");
        String formatCode = StringUtils.formatIndexCode(codes);
        String url = String.format("%s%s", txBatch, formatCode);
        String body = RestTemplateUtils.request(url, String.class);
        String str = body.replaceAll("\\n", "");
        String[] stockDetailArr = str.split(";");
        List<StockDetailVo> stockVoList = new ArrayList<>(stockDetailArr.length);
        for (String stockDetail : stockDetailArr) {
            String[] arr = stockDetail.split("~");
            StockDetailVo stockDetailVo = ReflectionUtils.createDataByStrArr(arr, StockDetailVo.class);
            stockVoList.add(stockDetailVo);
        }
        return stockVoList;
    }

    /**
     * 查询指数简单信息（可批量上证指数，深证成指 000001，399001）
     *
     * @param codes codes
     * @return 指数信息
     * @throws Exception Exception
     */
    @Override
    public List<StockSingleVo> querySingle(String codes) throws Exception {
        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtils.getByKey("txBatchSingle");
        String formatCode = StringUtils.formatIndexCode(codes);
        String[] codeArr = formatCode.split(",");
        StringBuilder builder = new StringBuilder();
        for (String s : codeArr) {
            builder.append("s_");
            builder.append(s);
            builder.append(",");
        }
        String substring = builder.substring(0, builder.length() - 1);
        String url = String.format("%s%s", txBatch, substring);
        String body = RestTemplateUtils.request(url, String.class);
        String str = body.replaceAll("\\n", "");
        String[] stockSingleStrArr = str.split(";");
        List<StockSingleVo> stockVoList = new ArrayList<>(stockSingleStrArr.length);
        for (String stockStr : stockSingleStrArr) {
            int begin = stockStr.indexOf("=\"") + 2;
            int end = stockStr.lastIndexOf("\"");
            String useStr = stockStr.substring(begin, end);
            String[] arr = useStr.split("~");
            StockSingleVo stockSingleVo = ReflectionUtils.createDataByStrArr(arr, StockSingleVo.class);
            stockVoList.add(stockSingleVo);
        }
        return stockVoList;
    }
}
