package com.tt.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务
 *
 * @author tt
 * @version 1.0
 * @since 2025/04/03 10:25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockService {

    // 锁的前缀
    @Value("${com.tt.warehouse.distributed-lock-prefix}")
    private String distributedLockPrefix;
    // 锁的过期时间，单位秒
    @Value("${com.tt.warehouse.lock-expire-time}")
    private long lockExpireTime;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 尝试获取分布式锁
     *
     * @param approvalProcessId 审批流程ID
     * @param contractCode      合同编码
     * @param requestId         请求标识
     * @return 成功获取锁返回true，表示锁已被其他请求持有
     */
    public boolean tryLock(String approvalProcessId, String contractCode, String requestId) {
        String lockKey = generateLockKey(approvalProcessId, contractCode);
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, requestId, lockExpireTime, TimeUnit.SECONDS);
        return result == null || !result;
    }

    /**
     * 释放分布式锁
     *
     * @param approvalProcessId 审批流程ID
     * @param contractCode      合同编码
     * @param requestId         请求标识
     */
    public void unlock(String approvalProcessId, String contractCode, String requestId) {
        String lockKey = generateLockKey(approvalProcessId, contractCode);
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
            if (requestId != null && requestId.equals(currentValue)) {
                Boolean deleted = stringRedisTemplate.delete(lockKey);
                if (Boolean.TRUE.equals(deleted)) {
                    log.debug("释放锁成功: {}", lockKey);
                } else {
                    log.warn("释放锁失败，锁可能已过期: {}", lockKey);
                }
            } else {
                log.warn("尝试释放的锁与当前持有者不匹配或锁已过期: 期望={}, 实际={}", requestId, currentValue);
            }
        } catch (Exception e) {
            log.error("释放锁过程发生异常: {}", lockKey, e);
        }
    }

    /**
     * 生成锁的键
     *
     * @param approvalProcessId 审批流程ID
     * @param contractCode      合同编码
     * @return 锁键
     */
    private String generateLockKey(String approvalProcessId, String contractCode) {
        return distributedLockPrefix + approvalProcessId + ":" + contractCode;
    }

    /**
     * 续期分布式锁
     *
     * @param approvalProcessId 审批流程ID
     * @param contractCode      合同编码
     * @param requestId         请求标识
     * @return 是否续期成功
     */
    public boolean renewLock(String approvalProcessId, String contractCode, String requestId) {
        String lockKey = generateLockKey(approvalProcessId, contractCode);
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(lockKey);

            // 只有当前持有者才能续期
            if (requestId != null && requestId.equals(currentValue)) {
                Boolean result = stringRedisTemplate.expire(lockKey, lockExpireTime, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(result)) {
                    log.debug("锁续期成功: {}", lockKey);
                    return true;
                } else {
                    log.warn("锁续期失败，锁可能已过期: {}", lockKey);
                    return false;
                }
            } else {
                log.warn("尝试续期的锁与当前持有者不匹配或锁已过期: 期望={}, 实际={}", requestId, currentValue);
                return false;
            }
        } catch (Exception e) {
            log.error("锁续期过程发生异常: {}", lockKey, e);
            return false;
        }
    }
}
