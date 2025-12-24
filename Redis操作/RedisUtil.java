package com.tt.util;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * 封装对 Redis 的增删改查操作
 *
 * @author tt
 * @version 1.0
 * @since 2025/12/24
 */
@Slf4j
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // ================================ 通用操作 ================================ //

    /**
     * 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 设置结果
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                return redisTemplate.expire(key, timeout, unit);
            }
            return false;
        } catch (Exception e) {
            log.error("设置过期时间失败, key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间（秒），-1 表示永不过期，-2 表示键不存在
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 存在返回 true，否则返回 false
     */
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("判断键是否存在失败, key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 删除键
     *
     * @param key 键
     * @return 删除结果
     */
    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除键失败, key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 批量删除键
     *
     * @param keys 键集合
     * @return 删除成功的数量
     */
    public Long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("批量删除键失败, error: {}", e.getMessage());
            return 0L;
        }
    }

    // ================================ String 操作 ================================ //

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     * @return 设置结果
     */
    public Boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置值失败, key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 设置值并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 设置结果
     */
    public Boolean set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            if (timeout > 0) {
                redisTemplate.opsForValue().set(key, value, timeout, unit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置值失败, key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 值递增
     *
     * @param key   键
     * @param delta 增量（大于0）
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 值递减
     *
     * @param key   键
     * @param delta 减量（大于0）
     * @return 递减后的值
     */
    public Long decrement(String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================ Hash 操作 ================================ //

    /**
     * 获取 Hash 中的值
     *
     * @param key     键
     * @param hashKey Hash 键
     * @return Hash 值
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取 Hash 中的所有键值对
     *
     * @param key 键
     * @return Hash 键值对
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 设置 Hash 中的值
     *
     * @param key     键
     * @param hashKey Hash 键
     * @param value   值
     * @return 设置结果
     */
    public Boolean hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            log.error("设置Hash值失败, key: {}, hashKey: {}, error: {}", key, hashKey, e.getMessage());
            return false;
        }
    }

    /**
     * 批量设置 Hash 中的值
     *
     * @param key 键
     * @param map Hash 键值对
     * @return 设置结果
     */
    public Boolean hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("批量设置Hash值失败, key: {}, error: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 删除 Hash 中的值
     *
     * @param key      键
     * @param hashKeys Hash 键
     * @return 删除成功的数量
     */
    public Long hDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断 Hash 中是否存在某个键
     *
     * @param key     键
     * @param hashKey Hash 键
     * @return 存在返回 true，否则返回 false
     */
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    // ================================ List 操作 ================================ //

    /**
     * 获取 List 中指定范围的元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引（-1 表示到最后）
     * @return 元素列表
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取 List 的长度
     *
     * @param key 键
     * @return List 长度
     */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取 List 中指定索引的元素
     *
     * @param key   键
     * @param index 索引
     * @return 元素
     */
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 从左侧向 List 中添加元素
     *
     * @param key   键
     * @param value 值
     * @return List 长度
     */
    public Long lLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从右侧向 List 中添加元素
     *
     * @param key   键
     * @param value 值
     * @return List 长度
     */
    public Long lRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从左侧弹出元素
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object lLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从右侧弹出元素
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    // ================================ Set 操作 ================================

    /**
     * 获取 Set 中的所有元素
     *
     * @param key 键
     * @return 元素集合
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断 Set 中是否存在某个元素
     *
     * @param key   键
     * @param value 值
     * @return 存在返回 true，否则返回 false
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 向 Set 中添加元素
     *
     * @param key    键
     * @param values 值
     * @return 添加成功的数量
     */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取 Set 的大小
     *
     * @param key 键
     * @return Set 大小
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 从 Set 中删除元素
     *
     * @param key    键
     * @param values 值
     * @return 删除成功的数量
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    // ================================ ZSet 操作 ================================ //

    /**
     * 向 ZSet 中添加元素
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 添加结果
     */
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取 ZSet 中指定范围的元素（按分数从小到大排序）
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 元素集合
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取 ZSet 中指定范围的元素（按分数从大到小排序）
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 元素集合
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取 ZSet 的大小
     *
     * @param key 键
     * @return ZSet 大小
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取元素的分数
     *
     * @param key   键
     * @param value 值
     * @return 分数
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 从 ZSet 中删除元素
     *
     * @param key    键
     * @param values 值
     * @return 删除成功的数量
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }
}
