package com.tt.common.annotation;

import java.lang.annotation.*;

/**
 * @author tt
 * @date 2024/12/16 09:58
 * 系统日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 日志描述信息
     */
    String value() default "";

}
