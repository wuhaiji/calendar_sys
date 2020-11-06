package com.yuntun.calendar_sys.exception;


import com.yuntun.calendar_sys.model.code.ResultCode;
import lombok.Data;

/**
 * @author whj
 * @since 2020-02-09 17:29
 **/
@Data
public class ServiceException extends RuntimeException {

    /**
     * 错误码
     */
    protected String code;
    /**
     * 错误信息
     */
    protected String msg;

    public ServiceException() {
        super();
    }

    public ServiceException(ResultCode errorInfoInterface) {
        super(errorInfoInterface.getCode());
        this.code = errorInfoInterface.getCode();
        this.msg = errorInfoInterface.getMsg();
    }

    public ServiceException(ResultCode code, Throwable cause) {
        super(code.getCode(), cause);
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public ServiceException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ServiceException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }


    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
