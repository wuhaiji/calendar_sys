package com.yuntun.calendar_sys.model.code;

/**
 * 常用错误信息枚举
 *
 * @author whj
 */
public enum CommonCode implements ResultCode {

    SUCCESS("10000", "成功!"),
    METHOD_NOT_SUPPORT("10001", "请求方法不支持!"),
    NETWORK_ANOMALY("10004", "网络异常!"),
    LOGOUT_FAILED("10005", "登出失败"),
    PARAMETER_ABNORMAL("10006", "参数异常"),
    FREQUENT_OPERATION("10007", "操作频繁,请稍后再试"),
    SERVER_ERROR("99999", "抱歉，服务器异常，请稍后重试！");


    CommonCode(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;


    @Override
    public String getCode() {
        return this.resultCode;
    }

    @Override
    public String getMsg() {
        return this.resultMsg;
    }
}
