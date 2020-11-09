package com.yuntun.calendar_sys.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author whj
 * @description 拦截后preHandle方法实现的操作（打印入口日志）
 * @date 2020/06/24
 */

@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb
                .append(" 请求IP:").append(getIpAddr(request))
                .append("; 请求URI:").append(request.getRequestURI())
                .append("; 方法类型：").append(request.getMethod())
        ;

        boolean hasParam = false;
        Enumeration<String> paramNames = request.getParameterNames();
        sb.append("; 请求参数:{");
        while (paramNames.hasMoreElements()) {
            hasParam = true;
            String paramName = (String) paramNames.nextElement();
            String value = request.getParameter(paramName);
            sb.append("\"").append(paramName).append("\"")
                    .append(":").append("\"").append(value).append("\"").append(",");

        }
        //删除最后一位，
        sb.deleteCharAt(sb.length() - 1);
        if (!hasParam) {
            sb.append("无");
        }else{
            sb.append("}");
        }
        sb.append("; 开始调用。。。");
        logger.info(sb.toString());
        request.setAttribute("reqTime", System.currentTimeMillis());
        return true;
    }
    public String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                InetAddress inet = null;

                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException var5) {
                    var5.printStackTrace();
                }

                assert inet != null;
                ipAddress = inet.getHostAddress();
            }
        }

        if (ipAddress != null && ipAddress.length() > 15 && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {

        StringBuffer sb = new StringBuffer("调用完成:");
        sb.append(request.getServletPath());
        sb.append("; 方法类型:").append(request.getMethod());
        long respTime = System.currentTimeMillis();
        long reqTime = (Long) request.getAttribute("reqTime");
        if (reqTime != 0) {
            reqTime = Long.parseLong(request.getAttribute("reqTime").toString());
        }
        sb.append("; 调用时间:" + (respTime - reqTime) + "ms");
        logger.info(sb.toString());
    }

}
