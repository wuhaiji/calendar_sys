package com.yuntun.calendar_sys.model.code;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/9
 */
public enum PermissionCode implements ResultCode {
    LIST_PERMISSION_BY_USERID_ERROR("20401", "查询用户权限异常"),
    ;
    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    PermissionCode(String resultCode, String resultMsg) {
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
