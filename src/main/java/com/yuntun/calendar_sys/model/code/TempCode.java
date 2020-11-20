package com.yuntun.calendar_sys.model.code;

/**
 * 常用错误信息枚举
 *
 * @author whj
 */
public enum TempCode implements ResultCode {

    TODAY_TEMP_ALREADY_EXISTS("20700", "官方图文已经创建"),
    ADD_TEMP_FAILURE("20701", "创建官方图文失败"),
    UPDATE_TEMP_FAILURE("20702", "修改官方图文失败"),
    DELETE_TEMP_FAILURE("20703", "删除官方图文失败"),
    DETAIL_TEMP_FAILURE("20704", "查询官方图文详情失败"),
    DETAIL_TEMP_ID_DOES_NOT_EXIST("20705", "官方图文不存在"),
    LIST_TEMP_FAILURE("20706", "查询官方图文列表失败"),
    LIST_MONTH_FAILURE("20706", "按月份查询官方图文列表失败"),
    LIST_DAY_FAILURE("20707", "按日期官方图文是否存在失败"),
    ;



    TempCode(String resultCode, String resultMsg) {
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
