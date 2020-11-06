package com.yuntun.calendar_sys.model.code;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/5
 */
public enum RoleCode implements ResultCode {

    DETAIL_ROLE_FAILURE("20000", "查询角色详情异常"),
    ADD_ROLE_FAILURE("20000", "删除角色异常"),
    UPDATE_ROLE_FAILURE("20000", "修改角色异常"),
    DELETE_ROLE_FAILURE("20000", "删除角色异常"),
    LIST_ROLE_FAILURE("20000", "分页查询角色列表异常"),

    ;
    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    RoleCode(String resultCode, String resultMsg) {
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
