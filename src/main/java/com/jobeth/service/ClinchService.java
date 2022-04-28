package com.jobeth.service;

import com.jobeth.vo.ClinchDetailVo;

import java.util.List;

public interface ClinchService {
    List<ClinchDetailVo> queryMingxi(String market, String code, int size) throws Exception;
}
