package com.yuntun.calendar_sys.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/17
 */
@ConfigurationProperties(prefix = "admin")
@Component
@Data
public class AdminProperties {
    String token;
}
