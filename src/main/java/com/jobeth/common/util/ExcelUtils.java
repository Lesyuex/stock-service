package com.jobeth.common.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.jobeth.base.BatchService;
import com.jobeth.common.listen.ExcelAnalysisiListen;
import com.jobeth.common.listen.ExcelBatchListen;


import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/1 21:07:07
 * Description: -
 */
public class ExcelUtils {

    /**
     * clazz 传入空则会返回一个List JsonObject 对象
     *
     * @param inputStream inputStream
     * @param clazz       clazz
     * @return List<M>
     */
    public static <M> List<M> getDataList(InputStream inputStream, Class<M> clazz) {
        ExcelAnalysisiListen<M> listen = new ExcelAnalysisiListen<>();
        ExcelReader reader = EasyExcel.read(inputStream, clazz, listen).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        reader.read(readSheet);
        // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
        reader.finish();
        return listen.getDataList();
    }

    /**
     * clazz 传入空则会返回一个List JsonObject 对象
     *
     * @param inputStream inputStream
     * @param clazz       clazz
     * @return List<M>
     */
    public static <M> void readBatchInsert(InputStream inputStream, Class<M> clazz, BatchService<M> batchService) {
        ExcelBatchListen<M> listen = new ExcelBatchListen<>(batchService);
        ExcelReader reader = EasyExcel.read(inputStream, clazz, listen).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        reader.read(readSheet);
        // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
        reader.finish();
    }
}
