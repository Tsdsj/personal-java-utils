package com.tt.common.aspect;

import com.tt.common.annotation.DistributedLock;
import com.tt.common.enumeration.ResultCode;
import com.tt.common.result.Result;
import com.tt.common.util.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面
 *
 * @author tt
 * @version 1.0
 * @since 2025/04/03 10:27
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final DistributedLockService lockService;
    // Spring 表达式解析
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 环绕通知，处理分布式锁逻辑
     *
     * @param point           连接点
     * @param distributedLock 分布式锁注解
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint point, DistributedLock distributedLock) throws Throwable {
        // 获取方法信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String methodPath = method.getDeclaringClass().getName() + "." + method.getName();

        log.info("执行锁方法: {}, 锁表达式: {}, 过期时间: {}秒, 自动续期: {}, 续期间隔: {}秒, 操作超时: {}分钟",
                methodPath,
                Arrays.toString(distributedLock.keys()),
                distributedLock.expireTime(),
                distributedLock.autoRenew(),
                distributedLock.renewInterval(),
                distributedLock.timeout());

        // 获取方法参数
        Object[] args = point.getArgs();
        String[] parameterNames = signature.getParameterNames();

        // 创建 Spring 表达式上下文
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        // 解析锁的键
        String[] keyValues = new String[distributedLock.keys().length];
        for (int i = 0; i < distributedLock.keys().length; i++) {
            Object value = parser.parseExpression(distributedLock.keys()[i]).getValue(context);
            if (value == null) {
                log.error("锁键表达式计算结果为空: {} 在方法 {}", distributedLock.keys()[i], methodPath);
                return Result.error(ResultCode.ERROR.getCode(), "锁键不能为空");
            }
            keyValues[i] = String.valueOf(value);
            if (keyValues[i].trim().isEmpty()) {
                log.error("锁键值为空字符串: {} 在方法 {}", distributedLock.keys()[i], methodPath);
                return Result.error(ResultCode.ERROR.getCode(), "锁键不能为空");
            }
        }

        // 生成或解析请求 ID
        String requestId;
        if (distributedLock.requestId().isEmpty()) {
            requestId = UUID.randomUUID().toString();
        } else {
            Object requestIdValue = parser.parseExpression(distributedLock.requestId()).getValue(context);
            if (requestIdValue == null) {
                log.error("请求ID表达式计算结果为空: {} 在方法 {}", distributedLock.requestId(), methodPath);
                requestId = UUID.randomUUID().toString(); // 如果自定义ID为空，使用UUID作为后备方案
            } else {
                requestId = String.valueOf(requestIdValue);
            }
        }
        long lockStartTime = System.currentTimeMillis();

        // 尝试获取锁
        if (lockService.tryLock(keyValues[0], keyValues.length > 1 ? keyValues[1] : "", requestId)) {
            log.warn("重复提交请求: 方法={}, keys={}", methodPath, String.join(", ", keyValues));
            return Result.error(ResultCode.REQUEST_TOO_FREQUENTLY.getCode(), "请求正在处理中，请勿重复提交");
        }

        log.info("获取锁成功, 方法={}, keys={}, 请求ID={}", methodPath, String.join(", ", keyValues), requestId);

        // 如果需要自动续期，创建守护线程进行锁续期
        ScheduledExecutorService scheduler = null;
        ScheduledFuture<?> renewTask = null;

        try {
            if (distributedLock.autoRenew()) {
                scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setDaemon(true);
                    t.setName("lock-renewal-" + requestId);
                    return t;
                });

                final String key1 = keyValues[0];
                final String key2 = keyValues.length > 1 ? keyValues[1] : "";

                renewTask = scheduler.scheduleAtFixedRate(() -> {
                    try {
                        if (lockService.renewLock(key1, key2, requestId)) {
                            log.info("续期锁成功, 方法={}, keys={}, 请求ID={}", methodPath, String.join(", ", keyValues), requestId);
                        } else {
                            log.warn("续期锁失败, 可能已丢失锁控制权: 方法={}, keys={}, 请求ID={}",
                                    methodPath, String.join(", ", keyValues), requestId);
                        }
                    } catch (Exception e) {
                        log.error("锁续期发生异常, 方法={}, keys={}, 请求ID={}",
                                methodPath, String.join(", ", keyValues), requestId, e);
                    }
                }, distributedLock.renewInterval(), distributedLock.renewInterval(), TimeUnit.SECONDS);
            }

            // 执行目标方法
            Object result = point.proceed();

            // 方法执行完成后释放锁
            double lockDurationSeconds = (System.currentTimeMillis() - lockStartTime) / 1000.0;
            try {
                lockService.unlock(keyValues[0], keyValues.length > 1 ? keyValues[1] : "", requestId);
                log.info("方法执行完成，释放锁: 方法={}, keys={}, 请求ID={}, 持有锁时间={}秒",
                        methodPath, String.join(", ", keyValues), requestId,
                        String.format("%.2f", lockDurationSeconds));
            } catch (Exception e) {
                log.error("释放锁失败: 方法={}, keys={}, 请求ID={}",
                        methodPath, String.join(", ", keyValues), requestId, e);
            }
            return result;

        } catch (Throwable e) {
            // 执行过程中出现异常，释放锁
            double lockDurationSeconds = (System.currentTimeMillis() - lockStartTime) / 1000.0;
            try {
                lockService.unlock(keyValues[0], keyValues.length > 1 ? keyValues[1] : "", requestId);
                log.info("方法执行异常，释放锁: 方法={}, keys={}, 请求ID={}, 异常={}, 持有锁时间={}秒",
                        methodPath, String.join(", ", keyValues), requestId, e.getMessage(),
                        String.format("%.2f", lockDurationSeconds));
            } catch (Exception ex) {
                log.error("释放锁失败: 方法={}, keys={}, 请求ID={}",
                        methodPath, String.join(", ", keyValues), requestId, ex);
            }
            throw e;
        } finally {
            // 清理资源
            if (renewTask != null) {
                renewTask.cancel(false);
            }
            if (scheduler != null) {
                scheduler.shutdown();
            }
        }
    }
}