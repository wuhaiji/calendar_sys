package com.yuntun.calendar_sys.model.code;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/9
 */

public enum HeartWordsCode implements ResultCode {
    DETAIL_SYSUSER_ERROR("20101", "查询心语详情异常"),
    ADD_HEART_WORDS_ERROR("20102", "添加心语异常"),
    UPDATE_SYSUSER_ERROR("20103", "修改心语异常"),
    DELETE_SYSUSER_ERROR("20104", "删除心语异常"),
    LIST_SYSUSER_ERROR("20105", "分页查询心语列表异常"),
    ;
    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    HeartWordsCode(String resultCode, String resultMsg) {
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
