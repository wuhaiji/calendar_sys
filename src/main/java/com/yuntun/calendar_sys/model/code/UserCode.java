package com.yuntun.calendar_sys.model.code;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/5
 */
public enum UserCode implements ResultCode {

    DETAIL_USER_FAILURE("20201", "查询用户详情异常"),
    ADD_USER_FAILURE("20202", "添加用户异常"),
    UPDATE_USER_FAILURE("20203", "修改用户异常"),
    DELETE_USER_FAILURE("20204", "删除用户异常"),
    LIST_USER_FAILURE("20205", "分页查询用户列表异常"),
    WECHAT_USER_LOGIN_ERROR("20206", "登录微信平台异常"),
    LOGIN_FAILED_TIME_OUT("20207", "登录超时"),
    NOT_LOGGED_IN("20208", "未登录"),
    USER_AGENT_EXCEPTION("20209", "浏览器信息异常"),

    ;
    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    UserCode(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }


    @Override
    public String getCode() {
        return this.resultCode;
    }

    @Override
    public String getMsg() {
        return this.resultMsg;
    }
}
