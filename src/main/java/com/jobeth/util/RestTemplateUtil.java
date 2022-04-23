package com.jobeth.util;

import com.jobeth.enums.RequestUrlEnums;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
public class RestTemplateUtil {

    private static final RestTemplate REST_TEMPLATE = SpringContextUtil.getBean(RestTemplate.class);

    public static <T> T request(String url,Class<T> clazz){
        ResponseEntity<T> forEntity = REST_TEMPLATE.getForEntity(url, clazz);
        return (T)forEntity.getBody();
    }

    public static <T> T getWithHeader(String url,Map<String,String> header,Class<T> clazz){
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, String> param : header.entrySet()) {
            headers.set(param.getKey(), param.getValue());
        }
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity(null, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        return REST_TEMPLATE.exchange(builder.build().toString(), HttpMethod.GET, request, clazz).getBody();
    }
}
