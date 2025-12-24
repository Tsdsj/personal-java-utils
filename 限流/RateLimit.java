package com.tt.common.annotation;

import java.lang.annotation.*;

/**
 * @author tt
 * @date 2024/12/5 10:08
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    int limit();

    int timeWindowInSeconds();

}