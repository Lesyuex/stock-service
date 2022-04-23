package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jobeth.entity.StockInfo;
import com.jobeth.enums.RequestUrlEnums;
import com.jobeth.mapper.StockInfoMapper;
import com.jobeth.service.StockInfoService;
import com.jobeth.util.HttpClientUtil;
import com.jobeth.util.PropertiesUtil;
import com.jobeth.util.RestTemplateUtil;
import com.jobeth.util.StringUtil;
import com.jobeth.vo.StockDetailVo;
import com.jobeth.vo.StockInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
     * 根据传入的股票代码查询股票当前详细信息（可批量603138，000001）
     * @param codes code
     * @return 股票详细信息
     * @throws Exception Exception
     */
    @Override
    public List<StockDetailVo> query(String codes) throws Exception {
        // 腾讯批量查询地址（详细信息）
        String txBatch = PropertiesUtil.getByKey("txBatchDetail");
        // 处理股票带码
        String formatCode = StringUtil.formatStockCode(codes);
        String url = String.format("%s%s",txBatch,formatCode);
        // 解析数据
        String body = RestTemplateUtil.request(url, String.class);
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
     * @return 癌股所有股票
     */
    @Override
    public List<StockInfoVo> listAll() {
        QueryWrapper<StockInfo> query = new QueryWrapper<StockInfo>();
        query.select("A_STOCK_CODE","B_STOCK_CODE","SEC_NAME_CN","LIST_BOARD");
        List<StockInfo> list = this.stockInfoMapper.selectList(query);
        List<StockInfoVo> stockInfoVoList = new ArrayList<>(list.size());
        for (StockInfo stockInfo : list) {
            StockInfoVo stockInfoVo = new StockInfoVo();
            BeanUtils.copyProperties(stockInfo,stockInfoVo);
            stockInfoVoList.add(stockInfoVo);
        }
        return stockInfoVoList;
    }

    /**
     * 获取所有上交所的股票
     * @return List<StockInfo>
     * @throws Exception Exception
     */
    public List<StockInfo> getShStock() throws Exception {
        String url = PropertiesUtil.getByKey("shStockList");
        HashMap<String, String> headerMap = new HashMap<>(1);
        // 接口需要Referer请求头信息
        headerMap.put("Referer", "http://www.sse.com.cn/");
        String json = HttpClientUtil.getWithHeader(url, headerMap);
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
            list.add(stockInfo);
        }
        return list;
    }


    /**
     * 获取深交所所有股票
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
                    Thread.sleep(333);
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
     * @param data 当前Tab的第N页数据
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
     * @param tab 第几个tab
     * @param pageNo 当前Tab的第几页
     * @return JSONObject
     * @throws Exception Exception
     */
    public JSONObject getDataByTabAndPage(int tab, int pageNo) throws Exception {
        String url = PropertiesUtil.getByKey("szStockList");
        String tabKey = String.format("%s%s","tab",tab);
        String page = String.valueOf(pageNo);
        String realUrl = url.replace("tab_key",tabKey).replaceAll("page_no",page);
        String json = HttpClientUtil.getWithHeader(realUrl, new HashMap<>());
        JSONArray tabPageNoData = JSON.parseObject(json, JSONArray.class);
        return tabPageNoData.getJSONObject(tab - 1);
    }
    
}
