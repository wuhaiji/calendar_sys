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
    DETAIL_ERROR("20501", "查询最新心语详情异常"),
    ADD_ERROR("20502", "添加心语异常"),
    UPDATE_ERROR("20503", "修改心语异常"),
    DELETE_ERROR("20504", "删除心语异常"),
    LIST_ERROR("20505", "分页查询心语列表异常"),
    UPDATE_BATCH_ERROR("20505", "分页查询心语列表异常"),
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
