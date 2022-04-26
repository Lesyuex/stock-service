package com.jobeth.service;

import com.jobeth.vo.FundingVo;

import java.util.*;

public interface FundingService {
    public Map<String, List<FundingVo>> getMinutes() throws Exception;
}
