package com.jobeth.common.listen;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通解析.
 *
 * @author JyrpoKoo
 * @date 2022/5/1 19:04:04
 * Description: -
 */
@Slf4j
@Getter
public class ExcelAnalysisiListen<M> extends AnalysisEventListener<M> {
    private final List<M> dataList = new ArrayList<>();

    @Override
    public void invoke(M e, AnalysisContext analysisContext) {
        log.debug("解析到一条数据：{}",e);
        // 解析到一条数据 ExcelModal数据
        dataList.add(e);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.debug("所有数据解析完成");
    }
}
