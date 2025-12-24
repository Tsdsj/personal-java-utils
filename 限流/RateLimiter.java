package com.tt.common.util;

import com.tt.common.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author tt
 * @date 2024/12/5 10:05
 * 限流器-基于 Redis lua脚本 实现
 * 在有需要限流的方法上加上自定义注解 @RateLimit 即可
 * Example:  @RateLimit(limit = number, timeWindowInSeconds = number)
 * limit表示请求次数的最大值，timeWindowInSeconds表示时间区间的时长，单位为秒，传入的参数都为整数
 */
@Slf4j
@Component
public class RateLimiter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 限流lua脚本
     * 获取传入的键
     * local key = KEYS[1]
     * 获取传入的限制次数
     * local limit = tonumber(ARGV[1])
     * 获取当前的请求次数，如果不存在则默认为0
     * local current = tonumber(redis.call('get', key) or '0')
     * 判断当前请求次数是否超过限制次数
     * if current + 1 > limit then
     * 如果超过限制次数，则返回0表示限流
     * return 0
     * else
     * 如果未超过限制次数，则将请求次数加1
     * redis.call('INCRBY', key, 1)
     * 设置键的过期时间
     * redis.call('expire', key, ARGV[2])
     * 返回1表示请求被允许
     * return 1
     * end
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1] " +
                    "local limit = tonumber(ARGV[1]) " +
                    "local current = tonumber(redis.call('get', key) or '0') " +
                    "if current + 1 > limit then " +
                    "  return 0 " +
                    "else " +
                    "  redis.call('INCRBY', key, 1) " +
                    "  redis.call('expire', key, ARGV[2]) " +
                    "  return 1 " +
                    "end";

    /**
     * 根据指定的键、限制次数、指定的时间区间来检查请求是否被允许。
     *
     * @param key                 用于标识请求的唯一键
     * @param limit               在时间区间内允许的最大请求数
     * @param timeWindowInSeconds 应用限制的时间区间（以秒为单位）
     * @return 如果请求次数超过限制，则进行限流，否则返回true
     */
    public boolean isAllowed(String key, int limit, int timeWindowInSeconds) {
        String userKey = BaseContext.getCurrentId() + ":" + key;
        try {
            Boolean result = stringRedisTemplate.execute(
                    redisScript,
                    Collections.singletonList(userKey),
                    String.valueOf(limit),
                    String.valueOf(timeWindowInSeconds)
            );
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("限流操作失败", e);
            return false;
        }
    }

    private final DefaultRedisScript<Boolean> redisScript;

    public RateLimiter() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Boolean.class);
    }
}
