package com.tt.common.aspect;

import com.tt.common.annotation.RateLimit;
import com.tt.common.constant.MessageConstant;
import com.tt.common.context.BaseContext;
import com.tt.common.enumeration.ResultCode;
import com.tt.common.result.Result;
import com.tt.common.util.RateLimiter;
import com.tt.common.util.RedisClient;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author tt
 * @date 2024/12/5 10:08
 * 限流切面
 * 需要配合 @Operation 注解使用以获取方法名
 */
@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private RedisClient redisClient;

    /*
     * 切入点
     * 限流处理
     */
    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = generateKey(joinPoint);
        Collection<? extends GrantedAuthority> authorities = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if ("ROLE_admin".equals(authority.getAuthority())) {
                    return joinPoint.proceed();
                }
            }
        }

        // 如果是管理员，直接放行,不限流

        if (!rateLimiter.isAllowed(key, rateLimit.limit(), rateLimit.timeWindowInSeconds())) {
            // 获取剩余时间
            long waitTime = getWaitTime(key);
            // 获取方法名
            String methodName = getSignatureValue(joinPoint);
            return Result.error(ResultCode.REQUEST_TOO_FREQUENTLY.getCode(),
                    "接口：" + methodName + "，" + MessageConstant.REQUEST_TOO_FREQUENTLY + "，请于" + waitTime + "秒后重试");
        }
        return joinPoint.proceed();
    }

    /*
     * 生成限流器的 key
     */
    private String generateKey(ProceedingJoinPoint joinPoint) {
        // 获取方法名
        return joinPoint.getSignature().toShortString().replaceAll("\\(.*\\)", "");
    }

    /*
     * 获取剩余时间
     */
    private long getWaitTime(String key) {
        String userKey = BaseContext.getCurrentId() + ":" + key;
        Long expire = redisClient.getExpire(userKey);
        if (expire == null || expire == -1) {
            return 0;
        }
        return expire;
    }

    /*
     * 通过反射获取方法名
     */
    private String getSignatureValue(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Operation operation = method.getAnnotation(Operation.class);
        return operation.summary();
    }
}
