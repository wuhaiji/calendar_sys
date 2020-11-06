package com.yuntun.calendar_sys.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * spring环境中获取request和session
 * @author whj
 */

public class SpringRequestUtils {
    public static HttpServletRequest request;

    static {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        request = requestAttributes.getRequest();
    }


    public static HttpSession getSession() {
        return request.getSession();

    }

    public static HttpServletRequest getRequest() {
        return request;
    }

    public static Object getSessionAttribute(String attributeName) {
        return request.getSession().getAttribute(attributeName);
    }

    public static Object getRequestAttribute(String attributeName) {
        return request.getAttribute(attributeName);
    }
}
