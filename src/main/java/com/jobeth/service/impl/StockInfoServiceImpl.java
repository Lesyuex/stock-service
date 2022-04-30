package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jobeth.common.util.*;
import com.jobeth.po.StockInfo;
import com.jobeth.mapper.StockInfoMapper;
import com.jobeth.service.StockInfoService;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockInfoVo;
import com.jobeth.vo.StockSingleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:53:53
 * Description: -
 */
@Service
@Slf4j
public class StockInfoServiceImpl implements StockInfoService {
    @Autowired
    private StockInfoMapper stockInfoMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     *
     * 根据市场代码 + 股票代码 获取分时数据 (指数 和 股票通用)
     * @param type 根据市场代码
     * @param code 股票代码
     * @return 分时数据
     * @throws Exception Exception
     */
    @Override
    public Map<String, Object> queryMinutes(int type, String code) throws Exception {
        String txMinutes = PropertiesUtils.getByKey("txMinutes");
        String realCode = StockUtils.getRealCodes(type, code);
        String realUrl = String.format("%s%s", txMinutes, realCode);
        String res = RestTemplateUtils.request(realUrl, String.class);
        // 解析数据
        JSONObject jsonObject = JSON.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject stock = data.getJSONObject(realCode);
        String[] newestInfo = stock.getJSONObject("qt").getObject(realCode, String[].class);

        StockDetailVo stockDetailVo = ReflectionUtils.createDataByStrArr(newestInfo, StockDetailVo.class);
        // 分时图数据 [时间、价格、成交量、成交额] => [时间、价格、总成交量、总成交额]
        JSONArray minutesData = stock.getJSONObject("data").getJSONArray("data");
        BigDecimal yestclose = BigDecimal.valueOf(stockDetailVo.getYesterdayPrice());
        BigDecimal bigDecimal100 = new BigDecimal(100);
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
            newMinutesData.add(objects);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("newestInfo", stockDetailVo);
        map.put("newestMinutes", newMinutesData);
        map.put("yestclose", yestclose);
        return map;
    }


    /**
     * 查询指数简单信息（可批量上证指数，深证成指 000001，399001）
     *
     * @param codes codes
     * @return 指数信息
     * @throws Exception Exception
     */
    @Override
    public List<StockSingleVo> getSingle(int type,String codes) throws Exception {
        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtils.getByKey("txBatchSingle");
        String formatCode = StockUtils.getRealCodes(type,codes);
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
    /**
     * 根据传入的股票代码查询股票当前详细信息（可批量603138，000001）
     *
     * @param codes code
     * @return 股票详细信息
     * @throws Exception Exception
     */
    @Override
    public List<StockDetailVo> getDetail(int type,String codes) throws Exception {
        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtils.getByKey("txBatchDetail");
        // 处理股票带码
        String formatCode = StockUtils.getRealCodes(type,codes);
        String url = String.format("%s%s", txBatch, formatCode);
        // 解析数据
        String body = RestTemplateUtils.request(url, String.class);
        String str = body.replaceAll("\\n", "");
        String[] stockDetailArr = str.split(";");
        List<StockDetailVo> stockDetailVoList = new ArrayList<>(stockDetailArr.length);
        for (String stockDetail : stockDetailArr) {
            String[] arr = stockDetail.split("~");
            StockDetailVo stockVo = StockDetailVo.generateStockByStrArr(arr);
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
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        log.info("【获取所有股票数据...】");
        StockInfoMapper mapper = session.getMapper(StockInfoMapper.class);
        List<StockInfo> shList = this.getShStock();
        log.info("【获取上交所股票数据完成】");
        Map<String, List<StockInfo>> szStock = this.getSzStock();
        log.info("【获取深交所股票数据完成】");
        List<StockInfo> szList = szStock.get("dataList");
        List<StockInfo> dataListAB = szStock.get("dataListAB");
        try {
            log.info("【开始更新数据库数据】");
            mapper.deleteAll();
            shList.forEach(mapper::insert);
            szList.forEach(mapper::insert);
            dataListAB.forEach(mapper::updateSzBStock);
            session.commit();
            session.clearCache();
            log.info("【更新数据到数据库完成】");
        } catch (Exception e) {
            log.error("【插入数据库失败...】", e);
            session.rollback();
        } finally {
            session.close();
        }
    }

    /**
     * 查询癌股所有股票
     *
     * @return 癌股所有股票
     */
    @Override
    public List<StockInfoVo> listAll() {
        QueryWrapper<StockInfo> query = new QueryWrapper<StockInfo>();
        query.select("CODE", "SEC_NAME_CN", "LIST_BOARD");
        List<StockInfo> list = this.stockInfoMapper.selectList(query);
        List<StockInfoVo> stockInfoVoList = new ArrayList<>(list.size());
        for (StockInfo stockInfo : list) {
            StockInfoVo stockInfoVo = new StockInfoVo();
            BeanUtils.copyProperties(stockInfo, stockInfoVo);
            stockInfoVoList.add(stockInfoVo);
        }
        return stockInfoVoList;
    }

    /**
     * 获取所有上交所的股票
     *
     * @return List<StockInfo>
     * @throws Exception Exception
     */
    public List<StockInfo> getShStock() throws Exception {
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
        List<StockInfo> list = new ArrayList<>();
        for (Object o : resultArr) {
            JSONObject stock = (JSONObject) o;
            StockInfo stockInfo = stock.toJavaObject(StockInfo.class);
            // 处理b股
            String bStockCode = stockInfo.getBStockCode();
            if ((stockInfo.getSecNameCn().indexOf("Ｂ") > 0 || stockInfo.getSecNameCn().indexOf("B") > 0) && !"-".equals(bStockCode)) {
                stockInfo.setCode(stockInfo.getBStockCode());
                stockInfo.setAbFlag("B");
            } else {
                stockInfo.setCode(stockInfo.getAStockCode());
                stockInfo.setAbFlag("A");
            }
            list.add(stockInfo);
        }
        return list;
    }


    /**
     * 获取深交所所有股票
     *
     * @return 所有股票 + AB股股票
     * @throws Exception Exception
     */
    public Map<String, List<StockInfo>> getSzStock() throws Exception {
        HashMap<String, List<StockInfo>> map = new HashMap<>();
        List<StockInfo> dataList = new ArrayList<>();
        List<StockInfo> dataListAB = new ArrayList<>();
        for (int tabKey = 1; tabKey <= 4; tabKey++) {
            //获取深交所所有股票
            JSONObject currentTabData = this.getDataByTabAndPage(tabKey, 1);
            JSONArray pageData = currentTabData.getJSONArray("data");
            // 先存数据
            this.addData(pageData, dataList);
            JSONObject metaData = (JSONObject) currentTabData.get("metadata");
            int pageCount = metaData.getInteger("pagecount");
            int currentPage = metaData.getInteger("pageno");
            if (pageCount > currentPage) {
                log.info("【深交所Tab{}共有{}页数据，开始获取其他页数据】", tabKey, pageCount);
                for (int pageNo = 2; pageNo <= pageCount; pageNo++) {
                    currentTabData = this.getDataByTabAndPage(tabKey, pageNo);
                    pageData = currentTabData.getJSONArray("data");
                    // 继续存数据
                    if (tabKey == 4) {
                        this.addABData(pageData, dataListAB);
                    } else {
                        this.addData(pageData, dataList);
                    }
                    log.info("深交所Tab{}共{}页，获取第{}页数据完成", tabKey, pageCount, pageNo);
                    Thread.sleep(555);
                }
            }
        }
        map.put("dataList", dataList);
        map.put("dataListAB", dataListAB);
        return map;
    }

    public void addABData(JSONArray data, List<StockInfo> dataList) {
        for (Object o : data) {
            JSONObject stock = (JSONObject) o;
            StockInfo stockInfo = new StockInfo();
            String aStockCode = stock.getString("agdm") == null ? "-" : stock.getString("agdm");
            stockInfo.setAStockCode(aStockCode);
            String bStockCode = stock.getString("bgdm") == null ? "-" : stock.getString("bgdm");
            stockInfo.setBStockCode(bStockCode);
            dataList.add(stockInfo);
        }
    }

    /**
     * 解析当前数据并存放
     *
     * @param data     当前Tab的第N页数据
     * @param dataList 数据容器
     */
    public void addData(JSONArray data, List<StockInfo> dataList) {
        for (Object o : data) {
            /*
             * agdm: "000011"
             * agjc: "<a href='http://www.szse.cn/certificate/individual/index.html?code=000011' target='_blank'><u>深物业A</u></a>"
             * agltgb: "5.26"
             * agssrq: "1992-03-30"
             * agzgb: "5.28"
             * bk: "主板"
             * sshymc: "K 房地产"
             * 或者
             *  bgdm: "200468"
             *  bgjc: "<a href='http://www.szse.cn/certificate/individual/index.html?code=200468' target='_blank'><u>宁通信B</u></a>"
             *  bgltgb: "1.00"
             *  bgssrq: "1997-05-22"
             *  bgzgb: "1.00"
             *  bk: "主板"
             *  sshymc: "C 制造业"
             * */

            JSONObject stock = (JSONObject) o;
            StockInfo stockInfo = new StockInfo();
            String gzj = stock.getString("agjc");
            if (gzj == null) {
                gzj = stock.getString("bgjc");
            }
            int begin = gzj.indexOf("<u>");
            int end = gzj.indexOf("</u>");
            String name = gzj.substring(begin + 3, end);
            stockInfo.setSecNameCn(name);
            stockInfo.setSecNameFull(name);
            stockInfo.setCompanyAbbr(name);
            stockInfo.setCompanyAbbrEn("-");

            String aStockCode = stock.getString("agdm") == null ? "-" : stock.getString("agdm");
            stockInfo.setAStockCode(aStockCode);
            String bStockCode = stock.getString("bgdm") == null ? "-" : stock.getString("bgdm");
            stockInfo.setBStockCode(bStockCode);
            if (!"-".equals(aStockCode)) {
                stockInfo.setCode(stockInfo.getAStockCode());
                stockInfo.setAbFlag("A");
            }
            if (!"-".equals(bStockCode)) {
                stockInfo.setCode(stockInfo.getBStockCode());
                stockInfo.setAbFlag("B");
            }
            String listDate = stock.getString("agssrq");
            if (listDate == null) {
                listDate = stock.getString("bgssrq");
            }
            listDate = listDate.replaceAll("-", "");
            stockInfo.setListDate(listDate);
            stockInfo.setDelistDate("-");
            String listBoard = "主板".equals(stock.getString("bk")) ? "2" : "3";
            stockInfo.setListBoard(listBoard);
            dataList.add(stockInfo);
        }
    }

    /**
     * 根据Tab页和页数获取股票
     *
     * @param tab    第几个tab
     * @param pageNo 当前Tab的第几页
     * @return JSONObject
     * @throws Exception Exception
     */
    public JSONObject getDataByTabAndPage(int tab, int pageNo) throws Exception {
        String url = PropertiesUtils.getByKey("szStockList");
        String tabKey = String.format("%s%s", "tab", tab);
        String page = String.valueOf(pageNo);
        String realUrl = url.replace("tab_key", tabKey).replaceAll("page_no", page);
        String json = HttpClientUtils.getWithHeader(realUrl, new HashMap<>());
        JSONArray tabPageNoData = JSON.parseObject(json, JSONArray.class);
        return tabPageNoData.getJSONObject(tab - 1);
    }

}
