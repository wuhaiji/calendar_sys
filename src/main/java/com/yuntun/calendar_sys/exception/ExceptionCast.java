package com.yuntun.calendar_sys.exception;


import com.yuntun.calendar_sys.model.code.CommonCode;
import com.yuntun.calendar_sys.model.code.ResultCode;
import com.yuntun.calendar_sys.model.response.Result;

/**
 * @author whj
 * @since 2020-09-08
 **/
public class ExceptionCast {
    public static void cast(ResultCode resultCode) {
        throw new ServiceException(resultCode);
    }

    /**
     * 处理feign http请求异常
     *
     * @param result
     */
    public static void feignCast(Result result) {
        //网络异常
        if (result == null) {
            throw new ServiceException(CommonCode.NETWORK_ANOMALY);
        }
        //业务异常
        if (!CommonCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }

    }

}
