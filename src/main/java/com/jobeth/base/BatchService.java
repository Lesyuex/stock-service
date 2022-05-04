package com.jobeth.base;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/2 0:17:17
 * Description: -
 */
public interface BatchService<M> {
    void batchInsert(List<M> dataList);
}
