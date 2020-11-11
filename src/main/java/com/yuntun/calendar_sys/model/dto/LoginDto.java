package com.yuntun.calendar_sys.model.dto;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/10
 */
@Data
public class LoginDto {
    String username;
    String password;
    String code;
    String publickey;
}
