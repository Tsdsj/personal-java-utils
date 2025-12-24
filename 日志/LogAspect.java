package com.tt.common.aspect;

import com.tt.common.annotation.Log;
import com.tt.common.util.IpUtil;
import com.tt.common.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.tt.common.constant.DefaultConstant.DATE_TIME_FORMAT;
import static com.tt.common.constant.DefaultConstant.USER_AGENT;

/**
 * @author tt
 * @date 2024/12/16 10:00
 * 日志切面
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Before("@annotation(g)")
    public void doBefore(JoinPoint joinPoint, Log g) {
        // 获取请求信息
        String ip = IpUtil.getIpAddr(request);
        String userAgent = request.getHeader(USER_AGENT);
        String requestURI = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();
        String description = g.value();

        // 格式化请求时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String formattedRequestTime = requestTime.format(formatter);

        // 记录日志
        LogUtil logUtil = new LogUtil(redisTemplate);
        logUtil.setLogInfo(requestURI, ip, userAgent, formattedRequestTime, description);

    }

    @AfterReturning(pointcut = "@annotation(log)", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Log log, Object result) {
        // 可以在方法执行后记录更多信息或进行其他操作
    }
}
