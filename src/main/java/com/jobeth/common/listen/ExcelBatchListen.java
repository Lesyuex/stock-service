package com.jobeth.common.listen;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.jobeth.base.BatchService;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 传入一个service做批量操作.
 *
 * @author JyrpoKoo
 * @date 2022/5/2 0:16:16
 * Description: -
 */
@Slf4j
public class ExcelBatchListen<M> extends AnalysisEventListener<M> {
    private final static int BATCH_COUNT = 3000;
    private final List<M> modelList = new ArrayList<>();
    private BatchService<M> batchService;
    private ExcelBatchListen(){}
    public ExcelBatchListen(BatchService<M> batchService){
        this.batchService = batchService;
    }
    @Override
    public void invoke(M e, AnalysisContext analysisContext) {
        log.debug("解析到一条数据：{}",e);
        // 解析到一条数据 ExcelModal数据
        modelList.add(e);
        // 达到BATCH_COUNT，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (modelList.size() >= BATCH_COUNT) {
            batchService.batchInsert(modelList);
            //清空
            this.modelList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //这里也要保存数据，确保最后的数据也存储
        batchService.batchInsert(modelList);
        log.debug("所有数据解析完成");
    }
}