package com.jobeth.common.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.jobeth.dto.StockDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
public class RestTemplateUtils {

    private static final RestTemplate REST_TEMPLATE = SpringContextUtils.getBean(RestTemplate.class);


    public static List<Object> test() throws FileNotFoundException {
        String downUrl = PropertiesUtils.getByKey("downUrl");
        ResponseEntity<byte[]> response = REST_TEMPLATE.exchange(downUrl, HttpMethod.GET, null, byte[].class);
        byte[] file = response.getBody();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file);
        return EasyExcel.read(new BufferedInputStream(byteArrayInputStream)).head(StockDto.class).sheet()
                .doReadSync();

    }
    public static <T> T request(String url, Class<T> clazz) {
        ResponseEntity<T> forEntity = REST_TEMPLATE.getForEntity(url, clazz);
        return forEntity.getBody();
    }


    public static <T> T getWithHeader(String url, Map<String, String> header, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, String> param : header.entrySet()) {
            headers.set(param.getKey(), param.getValue());
        }
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity(null, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        return REST_TEMPLATE.exchange(builder.build().toString(), HttpMethod.GET, request, clazz).getBody();
    }

    public static ResponseEntity<?> exchange(String downUrl, HttpMethod method, Object o, Class<?> aClass) {
        return REST_TEMPLATE.exchange(downUrl, method, null, aClass);
    }
}
