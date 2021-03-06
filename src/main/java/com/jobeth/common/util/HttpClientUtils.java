package com.jobeth.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
public class HttpClientUtils {

    public static void main(String[] args) throws Exception {
        String url = PropertiesUtils.getByKey("shStockList");
        HashMap<String, String> headerMap = new HashMap<>(1);
        // 接口需要Referer请求头信息
        headerMap.put("Referer", "http://www.sse.com.cn/");
        String json = HttpClientUtils.getWithHeader(url, headerMap);
        // 解析数据
        int i = json.indexOf("{");
        int i1 = json.lastIndexOf("}");
        String substring = json.substring(i, i1 + 1);
        JSONObject jsonObject = JSONObject.parseObject(substring);
        JacksonUtil.toPrettyFormat(jsonObject);
        JSONArray resultArr = jsonObject.getJSONArray("result");
        System.out.println(1);
    }
    /**
     * 调用POST请求
     *
     * @param url         url
     * @param requestBody requestBody
     * @return String
     */
    public static String sendPost(String url, String requestBody) {
        // 创建post请求
        HttpPost post = new HttpPost(url);
        // 设置头
        post.setHeader("Content-Type", "application/json");
        // 设置请求设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(180000)
                .setConnectionRequestTimeout(180000)
                .setSocketTimeout(180000)
                .setRedirectsEnabled(true)
                .build();
        post.setConfig(requestConfig);
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider((CredentialsProvider) basicCredentialsProvider)
                .build();
        // 设置发送体
        post.setEntity((HttpEntity) new StringEntity(requestBody, ContentType.create("application/json", "utf-8")));
        try {
            CloseableHttpResponse response = httpClient.execute(post);
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                String str = EntityUtils.toString(response.getEntity());
                return str;
            }
            // 请求结果
            String result = EntityUtils.toString(response.getEntity());
            return "调用失败:" + result;
        } catch (Exception e) {
            e.printStackTrace();
            return "调用异常";
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 调用Get请求
     *
     * @param url   url
     * @param param param
     * @return String
     */
    public static String sendGet(String url, Map<String, String> param) {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 返回结果
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            //创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();
            // 创建http get请求
            HttpGet httpGet = new HttpGet(uri);
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否返回200
            if (response.getStatusLine().getStatusCode() == 200) {

                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            System.out.println("系统错误:" + e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                System.out.println("系统错误:" + e);
            }

        }
        return resultString;
    }


    public static String getWithHeader(String url, Map<String, String> param) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        URIBuilder builder = new URIBuilder(url);

        URI uri = builder.build();
        HttpGet httpGet = new HttpGet(uri);
        if (param != null) {
            for (String key : param.keySet()) {
                httpGet.addHeader(key, param.get(key));
            }
        }
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return EntityUtils.toString(response.getEntity());
    }
}