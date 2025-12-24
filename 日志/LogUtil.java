package com.tt.common.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author tt
 * @date 2025/1/16 10:41
 * 日志工具类
 */
public class LogUtil {

    private static final String LOG_PREFIX = "log:";
    private static final long EXPIRE_TIME_DAYS = 60L;
    private static final TimeUnit EXPIRE_TIME_UNIT = TimeUnit.DAYS;

    private final StringRedisTemplate redisTemplate;

    public LogUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置日志信息
     *
     * @param requestURI 请求URI
     * @param ip 请求IP
     * @param userAgent 用户代理
     * @param formattedRequestTime 格式化后的请求时间
     * @param description 描述信息
     */
    public void setLogInfo(String requestURI, String ip, String userAgent, String formattedRequestTime, String description) {
        // 获取用户信息
        String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .orElse(null);

        // 构建日志数据
        Map<String, String> logData = buildLogData(requestURI, ip, userAgent, formattedRequestTime, description, username);

        // 存储到Redis
        String logKey = createLogKey(requestURI, formattedRequestTime);
        storeInRedis(logKey, logData);
    }

    /**
     * 构建日志数据
     */
    private Map<String, String> buildLogData(String requestURI, String ip, String userAgent,
                                             String formattedRequestTime, String description, String username) {
        Map<String, String> logData = new HashMap<>();
        logData.put("requestURI", requestURI);
        logData.put("ip", ip);
        logData.put("userAgent", userAgent);
        logData.put("requestTime", formattedRequestTime);
        logData.put("description", description);
        Optional.ofNullable(username).ifPresent(name -> logData.put("username", name));

        return logData;
    }

    /**
     * 创建日志键名
     */
    private String createLogKey(String requestURI, String formattedRequestTime) {
        return LOG_PREFIX + requestURI + ":" + formattedRequestTime;
    }

    /**
     * 存储日志信息到Redis
     *
     * @param logKey Redis键
     * @param logData 日志数据
     */
    private void storeInRedis(String logKey, Map<String, String> logData) {
        redisTemplate.opsForHash().putAll(logKey, logData);
        redisTemplate.expire(logKey, EXPIRE_TIME_DAYS, EXPIRE_TIME_UNIT);
    }
}