package com.jobeth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jobeth.dto.PlateStocksInfoDto;
import com.jobeth.po.PlateInfo;
import com.jobeth.po.PlateStockInfo;
import com.jobeth.mapper.PlateInfoMapper;
import com.jobeth.mapper.PlateStockInfoMapper;
import com.jobeth.service.PlateInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobeth.common.util.HttpClientUtils;
import com.jobeth.common.util.PropertiesUtils;
import com.jobeth.common.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 板块 服务实现类
 * </p>
 *
 * @author jobeth
 * @since 2022-04-21
 */
@Service
@Slf4j
public class PlateInfoServiceImpl extends ServiceImpl<PlateInfoMapper, PlateInfo> implements PlateInfoService {

    /**
     * 更新所有板块信息
     */
    @Override
    public void putPlate() {
        log.info("【开始获取所有板块详细数据...】");
        String eastMoneyBk = PropertiesUtils.getByKey("eastMoneyBk");
        // 所有板块信息
        List<PlateStocksInfoDto> dtos = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String bKurl = eastMoneyBk.replace("bkIndex", String.valueOf(i));
            String response = HttpClientUtils.sendGet(bKurl, null);
            JSONObject resultObj = JSON.parseObject(response);
            // 从返回结果获取板块集合
            JSONObject data = resultObj.getJSONObject("data");
            JSONObject diff = data.getJSONObject("diff");
            for (String key : diff.keySet()) {
                PlateStocksInfoDto dto = new PlateStocksInfoDto();
                JSONObject plate = diff.getJSONObject(key);
                dto.setCode(plate.getString("f12"));
                dto.setName(plate.getString("f14"));
                dto.setCategoryType(String.valueOf(i));
                String name = "其它";
                if (i == 1) {
                    name = "地域板块";
                } else if (i == 2) {
                    name = "概念板块";
                } else {
                    name = "行业板块";
                }
                dto.setCategoryName(name);
                // 获取成分股
                List<PlateStockInfo> bkStockList = getPlateStockList(dto);
                dto.setList(bkStockList);
                dtos.add(dto);
            }
        }
        SqlSession sqlBatchSession = SpringContextUtils.getSqlBatchSession();
        PlateInfoMapper plateInfoMapper = sqlBatchSession.getMapper(PlateInfoMapper.class);
        PlateStockInfoMapper plateStockInfoMapper = sqlBatchSession.getMapper(PlateStockInfoMapper.class);
        try {
            log.info("【获取所有板块详细数据完成，开始插入数据库】");
            // 删除所有板块表信息
            plateInfoMapper.delete(null);
            // 删除成分股表信息
            plateStockInfoMapper.delete(null);
            dtos.forEach(dto -> {
                PlateInfo plateInfo = new PlateInfo();
                BeanUtils.copyProperties(dto, plateInfo);
                plateInfoMapper.insert(plateInfo);
                dto.getList().forEach(plateStockInfoMapper::insert);
            });
            sqlBatchSession.commit();
            sqlBatchSession.clearCache();
            log.info("【更新所有板块详细数据到数据库完成】");
        } catch (Exception e) {
            log.error("【插入数据库失败...】", e);
            sqlBatchSession.rollback();
        } finally {
            sqlBatchSession.close();
        }
    }


    /**
     * 根据板块信息获取成分股
     * @param dto 板块信息
     * @return 成分股
     */
    public List<PlateStockInfo> getPlateStockList(PlateStocksInfoDto dto) {
        String bkStocksStr = PropertiesUtils.getByKey("eastMoneyBkStocks");
        String bkCode = dto.getCode();
        // 发送请求
        String bkStocksUrl = bkStocksStr.replace("bkCode", bkCode);
        String response = HttpClientUtils.sendGet(bkStocksUrl, null);
        // 处理请求结果
        JSONObject bkStocksObj = JSON.parseObject(response);
        JSONObject bkStocksInfo = bkStocksObj.getJSONObject("data");
        // 取到所有成分股
        JSONObject stockList = bkStocksInfo.getJSONObject("diff");
        List<PlateStockInfo> list = new ArrayList<>();
        for (String stockIndex : stockList.keySet()) {
            PlateStockInfo plateStockInfo = new PlateStockInfo();
            JSONObject stock = stockList.getJSONObject(stockIndex);
            plateStockInfo.setPlateCode(bkCode);
            plateStockInfo.setStockCode(stock.getString("f12"));
            plateStockInfo.setStockName(stock.getString("f14"));
            list.add(plateStockInfo);
        }
        return list;
    }

    /**
     * 根据板块类型查询板块信息
     * @param categoryType categoryType
     * @return List
     */
    public List<PlateInfo> listByCategoryType(String categoryType){
        QueryWrapper<PlateInfo> query = new QueryWrapper<>();
        query.select("code","name");
        query.eq("category_type",categoryType);
        return this.baseMapper.selectList(query);
    }
}
