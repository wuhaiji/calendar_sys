package com.yuntun.calendar_sys.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.code.CommonCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 发送http请求工具
 * @author whj
 */
public class RestUtils {
    private static final Logger log = LoggerFactory.getLogger(RestUtils.class);
    private static RestTemplate restTemplate = SpringUtils.getBean("restTemplate");
    // private static ThreadPoolExecutor executor = SpringUtils.getBean("threadPoolExecutor");


    // /**
    //  * 异步发送放在requestBody里面的formData格式参数的post请求
    //  *
    //  * @param map 请求参数
    //  * @param url 请求地址
    //  * @return 包装responseBody的string内容的future对象
    //  */
    // public static Future<String> asyncPostForBody(Map<String, Object> map, String url) {
    //     return executor.submit(() -> postForBody(map, url));
    // }
    //
    // /**
    //  * 异步发送放在requestBody里面的formData格式参数的post请求
    //  *
    //  * @param map 请求参数
    //  * @param url 请求地址
    //  * @return 包装responseBody的JSONObject的future对象
    //  */
    // public static Future<String> asyncPostForJson(Map<String, Object> map, String url) {
    //     return executor.submit(() -> postForBody(map, url));
    // }

    /**
     * 发送放在requestBody里面的formData格式参数的post请求，返回responseBody的string内容
     *
     * @param map
     * @param url
     * @return
     */
    public static String postForBody(Map<String, Object> map, String url) {
        LinkedMultiValueMap<String, Object> valueMap = MapToMutiValueMap(map);
        log.info("发送post请求url:{}", url);
        log.info("发送post请求参数为:{}", JSON.toJSONString(map));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(valueMap, headers);
        String objectStr;
        try {
            objectStr = restTemplate.postForObject(url, entity, String.class);
        } catch (RestClientException e) {
            log.error("网络异常" + e);
            objectStr = null;
        }
        errorHandle(objectStr);
        return objectStr;
    }

    /**
     * 发送放在requestBody里面的formData格式参数的post请求，返回responseBody的阿里巴巴 JSONObject对象
     *
     * @param headers
     * @param map
     * @param url
     * @return
     */
    public static JSONObject postForJson(HttpHeaders headers, Map<String, Object> map, String url) {
        LinkedMultiValueMap<String, Object> valueMap = MapToMutiValueMap(map);
        log.info("发送post请求url:{}", url);
        log.info("发送post请求参数为:{}", JSON.toJSONString(map));
        log.info("发送post请求头参数为:{}", JSON.toJSONString(headers));
        //请求头
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(valueMap, headers);
        String objectStr;
        try {
            objectStr = restTemplate.postForObject(url, entity, String.class);
        } catch (RestClientException e) {
            log.error("网络异常" + e);
            objectStr = null;
        }
        errorHandle(objectStr);
        return JSONObject.parseObject(objectStr);
    }

    /**
     * 发送放在requestBody里面的formData格式参数的post请求，返回responseBody的阿里巴巴 JSONObject对象
     *
     * @param map
     * @param url
     * @return
     */
    public static <T> T postForJson(Map<String, Object> map, String url, Class<T> c) {
        LinkedMultiValueMap<String, Object> valueMap = MapToMutiValueMap(map);
        log.info("发送post请求url:{}", url);
        log.info("发送post请求参数为:{}", JSON.toJSONString(map));
        //请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(valueMap, headers);
        String objectStr;
        try {
            objectStr = restTemplate.postForObject(url, entity, String.class);
        } catch (RestClientException e) {
            log.error("网络异常" + e);
            objectStr = null;
        }
        errorHandle(objectStr);
        return JSONObject.parseObject(objectStr,c);
    }
    /**
     * 发送放在requestBody里面的formData格式参数的post请求，返回responseBody的阿里巴巴 JSONObject对象
     *
     * @param map
     * @param url
     * @return
     */
    public static JSONObject postForJson(Map<String, Object> map, String url) {
        LinkedMultiValueMap<String, Object> valueMap = MapToMutiValueMap(map);
        log.info("发送post请求url:{}", url);
        log.info("发送post请求参数为:{}", JSON.toJSONString(map));
        //请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(valueMap, headers);
        String objectStr;
        try {
            objectStr = restTemplate.postForObject(url, entity, String.class);
        } catch (RestClientException e) {
            log.error("网络异常" + e);
            objectStr = null;
        }
        errorHandle(objectStr);
        return JSONObject.parseObject(objectStr);
    }

    /**
     * 发送url格式参数的get请求，返回responseBody的string内容
     *
     * @param map
     * @param url
     * @return
     */
    public static String getForBody(Map<String, Object> map, String url) {
        String responseStr;
        String UrlWithParams = setParamsInUrl(map, url);
        log.info("发送get请求url:{}", UrlWithParams);
        try {
            responseStr = restTemplate.getForObject(UrlWithParams, String.class, map);
        } catch (RestClientException e) {
            log.error("网络异常：" + e);
            responseStr = null;
        }
        errorHandle(responseStr);
        return responseStr;
    }


    /**
     * 发送url格式参数的get请求，返回responseBody的string内容
     *
     * @param map
     * @param url
     * @return
     */
    public static JSONObject getForJson(Map<String, Object> map, String url) {
        String responseStr;
        String UrlWithParams = setParamsInUrl(map, url);
        log.info("发送get请求url:{}", UrlWithParams);
        try {
            responseStr = restTemplate.getForObject(UrlWithParams, String.class, map);
        } catch (RestClientException e) {
            log.error("网络异常：" + e);
            responseStr = null;
        }
        errorHandle(responseStr);
        return JSONObject.parseObject(responseStr);
    }

    /**
     * 将map中的值放入一个新的MutiValueMap
     *
     * @param map
     * @return
     */
    private static LinkedMultiValueMap<String, Object> MapToMutiValueMap(Map<String, Object> map) {
        LinkedMultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            valueMap.add(entry.getKey(), entry.getValue());
        }
        return valueMap;
    }

    /**
     * url设置参数
     *
     * @param map
     * @param url
     * @return
     */
    private static String setParamsInUrl(Map<String, Object> map, String url) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        if (map.size() > 0) {
            urlBuilder.append("?");
            for (String key : map.keySet()) {
                urlBuilder
                        .append(key)
                        .append("=")
                        .append(map.get(key))
                        .append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        return urlBuilder.toString();
    }

    private static void errorHandle(String objectStr) {
        if (objectStr == null || objectStr.equals("")) {
            throw new ServiceException(CommonCode.NETWORK_ANOMALY);
        }
    }
}
