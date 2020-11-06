package com.yuntun.calendar_sys.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  request工具
 * </p>
 *
 * @author whj
 * @since 2020/9/8
 */
public class RequestUtils {
    /**
     * 获取请求头参数map
     *
     * @param req request对象
     * @return 请求头Map
     */
    public static Map<String, String> getHeaderMap(HttpServletRequest req) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            String val = req.getHeader(name);
            headerMap.put(name, val);
        }
        return headerMap;
    }

    /**
     * 获取指定的请求头参数
     *
     * @param req        request对象
     * @param headerName 请求头名称
     * @return 请求头值
     */
    public static String getHeader(HttpServletRequest req, String headerName) {
        Map<String, String> headerMap = getHeaderMap(req);
        return headerMap.get(headerName);
    }
}
