package com.jobeth.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/5/15 0:28:28
 * Description: -
 */
@Data
public class FivedayVo {
    private LocalDate date;
    private BigDecimal yesclose;
    private List<MinutesVo> minutesVoList;
}
