package com.yuntun.calendar_sys.model.code;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/5
 */
public enum SysUserCode implements ResultCode {

    DETAIL_SYSUSER_FAILURE("20101", "查询用户详情异常"),
    ADD_SYSUSER_FAILURE("20102", "添加用户异常"),
    UPDATE_SYSUSER_FAILURE("20103", "修改用户异常"),
    DELETE_SYSUSER_FAILURE("20104", "删除用户异常"),
    LIST_SYSUSER_FAILURE("20105", "分页查询用户列表异常"),
    LOGIN_CAPTCHA_ERROR("20106", "图形验证码不正确,请重新获取"),
    LOGIN_FAILED_PUBLICKEY_INCORRECT("20107", "加密密钥不正确"),
    LOGIN_FAILED_USERNAME_INCORRECT("20108", "账号不存在"),
    LOGIN_FAILED_PASSWORD_INCORRECT("20109", "密码不正确"),
    LOGIN_EXCEPTION("20110", "验证账户时异常"),
    NOT_LOGGED_IN("20111", "未登录"),
    LOGIN_FAILED_TIME_OUT("20112", "登录超时,请重新登录"),
    LOGIN_FAILED_ILLEGAL_TOKEN("20113", "登录异常,非法token"),
    USER_AGENT_EXCEPTION("20114", "登录异常,浏览器信息错误"),
    LOGIN_FAILED_TOKEN_IS_EMPTY("20115", "登录信息异常,token为空"),
    INSUFFICIENT_PERMISSIONS("20116", "权限不足"),
    USERNAME_ALREADY_EXISTS("20117", "用户名已存在"),
    PHONE_NUMBER_ALREADY_EXISTS("20118", "手机号已存在"),
            ;
    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    SysUserCode(String resultCode, String resultMsg) {
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
