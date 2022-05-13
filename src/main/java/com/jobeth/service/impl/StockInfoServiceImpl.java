package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobeth.base.BatchService;
import com.jobeth.common.enums.ResultEnum;
import com.jobeth.common.excetion.StockException;
import com.jobeth.common.util.*;
import com.jobeth.mapper.StockInfoMapper;
import com.jobeth.model.StockInfoModel;
import com.jobeth.po.StockInfo;
import com.jobeth.service.StockInfoService;
import com.jobeth.vo.ClinchDetailVo;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockInfoVo;
import com.jobeth.vo.StockSingleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:53:53
 * Description: -
 */
@Service
@Slf4j
public class StockInfoServiceImpl extends ServiceImpl<StockInfoMapper, StockInfo> implements StockInfoService {
    private  List<StockInfoVo> stockInfoVoList = null;

    public static void main(String[] args) {
        double i = 809349 ;
        double d =8093491177.481;
        BigDecimal bigDecimal = new BigDecimal(d);
        BigDecimal bigDecimal1 = new BigDecimal(i);
        System.out.println(d/i);
        double average = bigDecimal.divide(bigDecimal1, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        System.out.println(average);

    }

    /**
     * 股票代码 获取分时数据 (指数 和 股票通用)
     *
     * @param code 股票代码
     * @return 分时数据
     * @throws Exception Exception
     */
    @Override
    public Map<String, Object> queryMinutes(String code) throws Exception {
        String txMinutes = PropertiesUtils.getByKey("txMinutes");
        String realUrl = String.format("%s%s", txMinutes, code);
        String res = RestTemplateUtils.request(realUrl, String.class);
        // 解析数据
        JSONObject jsonObject = JSON.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject stock = data.getJSONObject(code);
        String[] newestInfo = stock.getJSONObject("qt").getObject(code, String[].class);

        StockDetailVo stockDetailVo = ReflectionUtils.createDataByStrArr(newestInfo, StockDetailVo.class);
        // 分时图数据 [时间、价格、成交量、成交额] => [时间、价格、总成交量、总成交额]
        JSONArray minutesData = stock.getJSONObject("data").getJSONArray("data");
        BigDecimal yestclose = BigDecimal.valueOf(stockDetailVo.getYesterdayPrice());
        BigDecimal bigDecimal100 = new BigDecimal(100);
        double y1MaxValue = 0;
        double y1MinValue = 0;
        double absMaxPercent = 0;
        List<Object[]> newMinutesData = new ArrayList<>(minutesData.size());
        for (int i = 0; i < minutesData.size(); i++) {
            Object[] objects = new Object[7];
            String json = "[" + minutesData.getString(i).replaceAll(" ", ",") + "]";
            JSONArray o = JSON.parseObject(json, JSONArray.class);
            // 格式化date 0930 => 09:30
            String dateStr = o.getString(0);
            if (dateStr.length() == 3) {
                dateStr = "0" + dateStr;
            }
            StringBuilder stringBuilder = new StringBuilder(dateStr);
            stringBuilder.insert(2, ":");
            objects[0] = stringBuilder.toString();
            BigDecimal minutesPrice = o.getBigDecimal(1);
            objects[1] = minutesPrice;
            // 计算均价 （成交额除以成交量）//累计成交量//累计成交额
            BigDecimal volume = o.getBigDecimal(2);
            BigDecimal clinch = o.getBigDecimal(3);
            objects[2] = volume;
            objects[3] = clinch;
            int minutesVolume = volume.intValue();
            // 9.31 -> 14.57
            if (1 <= i && i <= 238) {
                String preStr = "[" + minutesData.getString(i - 1).replaceAll(" ", ",") + "]";
                JSONArray pre = JSON.parseObject(preStr, JSONArray.class);
                minutesVolume = minutesVolume - pre.getInteger(2);
            } else if (i == 239 || i == 240) {
                minutesVolume = 0;
            } else if (i == 241) {
                String preStr = "[" + minutesData.getString(238).replaceAll(" ", ",") + "]";
                JSONArray pre = JSON.parseObject(preStr, JSONArray.class);
                minutesVolume = minutesVolume - pre.getInteger(2);
            }
            objects[4] = minutesVolume;
            //均价
            double average = clinch.divide(volume, MathContext.DECIMAL128).divide(bigDecimal100, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            objects[5] = average;
            //最新涨跌幅
            BigDecimal diffPrice = minutesPrice.subtract(yestclose);
            double v = diffPrice.divide(yestclose, MathContext.DECIMAL128).multiply(bigDecimal100).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            objects[6] = v;
            double abs = Math.abs(v);
            if (abs>absMaxPercent)
            {
                absMaxPercent = abs;
            }
            newMinutesData.add(objects);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("newestInfo", stockDetailVo);
        map.put("newestMinutes", newMinutesData);
        map.put("yestclose", yestclose);
        map.put("y2MaxValue",absMaxPercent);
        BigDecimal down = new BigDecimal(100d - absMaxPercent);
        BigDecimal up = new BigDecimal(100d + absMaxPercent);
        y1MaxValue = yestclose.multiply(up).divide(bigDecimal100, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        y1MinValue = yestclose.multiply(down).divide(bigDecimal100, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        map.put("y1MaxValue",y1MaxValue);
        map.put("y1MinValue",y1MinValue);
        return map;
    }


    /**
     * 查询癌股所有股票
     *
     * @return 癌股所有股票
     */
    @Override
    public List<StockInfoVo> listAll() {
        if (this.stockInfoVoList != null) {
            return this.stockInfoVoList;
        }
        QueryWrapper<StockInfo> query = new QueryWrapper<>();
        query.select("MARKET_CODE", "SEC_NAME_CN");
        List<StockInfo> list = this.baseMapper.selectList(query);
        this.stockInfoVoList = new ArrayList<>(list.size());
        for (StockInfo stockInfo : list) {
            StockInfoVo stockInfoVo = new StockInfoVo();
            BeanUtils.copyProperties(stockInfo, stockInfoVo);
            stockInfoVoList.add(stockInfoVo);
        }
        return stockInfoVoList;
    }

    /**
     * 查询指数简单信息（可批量上证指数，深证成指 000001，399001）
     *
     * @param codes codes
     * @return 指数信息
     * @throws Exception Exception
     */
    @Override
    public List<StockSingleVo> getSingle(String codes) throws Exception {
        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtils.getByKey("txBatchSingle");
        String[] codeArr = codes.split(",");
        StringBuilder builder = new StringBuilder();
        for (String code : codeArr) {
            builder.append("s_");
            builder.append(code);
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

    /**
     * 根据传入的股票代码查询股票当前详细信息（可批量603138，000001）
     *
     * @param codes code
     * @return 股票详细信息
     * @throws Exception Exception
     */
    @Override
    public List<StockDetailVo> getDetail(String codes) throws Exception {
        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtils.getByKey("txBatchDetail");
        String url = String.format("%s%s", txBatch, codes);
        // 解析数据
        String body = RestTemplateUtils.request(url, String.class);
        String str = body.replaceAll("\\n", "");
        String[] stockDetailArr = str.split(";");
        List<StockDetailVo> stockDetailVoList = new ArrayList<>(stockDetailArr.length);
        for (String stockDetail : stockDetailArr) {
            String[] arr = stockDetail.split("~");
            StockDetailVo stockVo = ReflectionUtils.createDataByStrArr(arr, StockDetailVo.class);
            stockDetailVoList.add(stockVo);
        }
        return stockDetailVoList;
    }


    /**
     * 更新所有股票
     *
     * @throws Exception Exception
     */
    @Override
    public void putStock() throws Exception {
        SqlSession session = SpringContextUtils.getSqlBatchSession();
        log.info("【获取所有股票数据】");
        StockInfoMapper mapper = session.getMapper(StockInfoMapper.class);
        try {
            // 先删除所有数据
            mapper.delete(null);
            this.getShStock(mapper);
            log.info("【获取上交所股票数据完成】");
            this.getSzStock(mapper);
            log.info("【获取深交所股票数据完成】");
            session.commit();
            session.clearCache();
            log.info("【更新数据到数据库完成】");
        } catch (Exception e) {
            log.error("【更新数据到数据库失败】", e);
            session.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    /**
     * 获取所有上交所的股票
     *
     * @param mapper mapper
     * @throws Exception Exception
     */
    public void getShStock(StockInfoMapper mapper) throws Exception {
        String url = PropertiesUtils.getByKey("shStockList");
        HashMap<String, String> headerMap = new HashMap<>(1);
        // 接口需要Referer请求头信息
        headerMap.put("Referer", "http://www.sse.com.cn/");
        String json = HttpClientUtils.getWithHeader(url, headerMap);
        // 解析数据
        int i = json.indexOf("{");
        int i1 = json.lastIndexOf("}");
        String substring = json.substring(i, i1 + 1);
        JSONObject jsonObject = JSONObject.parseObject(substring);
        JSONArray resultArr = jsonObject.getJSONArray("result");
        for (Object o : resultArr) {
            JSONObject stock = (JSONObject) o;
            // 可以将a_stock_code 的字段类型转为aStockCode 对应的 JavaObject
            StockInfo stockInfo = stock.toJavaObject(StockInfo.class);
            // 处理b股
            String bStockCode = stockInfo.getBStockCode();
            if (!"-".equals(bStockCode) && (stockInfo.getSecNameCn().indexOf("Ｂ") > 0 || stockInfo.getSecNameCn().indexOf("B") > 0)) {
                stockInfo.setMarketCode("sh" + stockInfo.getBStockCode());
                stockInfo.setAbFlag("B");
            } else {
                stockInfo.setMarketCode("sh" + stockInfo.getAStockCode());
                stockInfo.setAbFlag("A");
            }
            mapper.insert(stockInfo);
        }
    }

    public void getSzStock(StockInfoMapper mapper) {
        String downUrl = PropertiesUtils.getByKey("downUrl");
        String url = "";
        for (int i = 1; i <= 2; i++) {
            // tab1 为A股主板和创业板
            // tab2 无
            // tab3 b股
            // tab4 a+b股信息
            String tab = "tab" + i;
            url = downUrl.replace("{tabPlace}", tab);
            // 下载每个Tab页的文件
            ResponseEntity<?> exchange = RestTemplateUtils.exchange(url, HttpMethod.GET, null, byte[].class);
            byte[] file = (byte[]) exchange.getBody();
            if (file == null) {
                throw new StockException(ResultEnum.SERVER_NO_THIS_SOURCE);
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file);
            //一边读一边插入
            ExcelUtils.readBatchInsert(byteArrayInputStream, StockInfoModel.class, new BatchService<StockInfoModel>() {
                @Override
                public void batchInsert(List<StockInfoModel> modelList) {
                    insertByTab(modelList, tab, mapper);
                }
            });
        }
    }

    private void insertByTab(List<StockInfoModel> modelList, String tab, StockInfoMapper mapper) {
        modelList.forEach(model -> {
            StockInfo stockInfo = new StockInfo();
            BeanUtils.copyProperties(model, stockInfo);
            // 1:主板 2：科创板 3：创业板
            if ("主板".equals(model.getBoardName())) {
                stockInfo.setListBoard("1");
            } else {
                stockInfo.setListBoard("3");
            }
            boolean isAstock = "tab1".equals(tab);
            stockInfo.setAbFlag(isAstock ? "A" : "B");
            stockInfo.setAStockCode(model.getAstockCode());
            stockInfo.setBStockCode(model.getBstockCode());
            String code = isAstock ? model.getAstockCode() : model.getBstockCode();
            stockInfo.setMarketCode("sz" + code);
            stockInfo.setCompanyCode(code);
            stockInfo.setSecNameCn(isAstock ? model.getAstockName() : model.getBstockName());
            stockInfo.setListDate(isAstock ? LocalDate.parse(model.getAlistDate()) : LocalDate.parse(model.getBlistDate()));
            mapper.insert(stockInfo);
        });
    }
}
