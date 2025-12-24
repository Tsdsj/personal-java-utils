package top.tt.common.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author TJ Yuan
 * @date 2024/9/25 下午2:32
 * Redis 客户端
 */
@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // ============================== String操作 ==============================

    /**
     * 保存数据
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 保存数据-过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间
     * @param unit  时间单位
     */
    public void setWithTime(String key, String value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, time, unit);
    }

    /**
     * 保存数据-过期时间（分钟）
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间,单位是 分钟
     */
    public void setWithTime(String key, String value, Long time) {
        stringRedisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
    }

    /**
     * 批量设置键值对
     *
     * @param keyValueMap 键值对Map
     */
    public void multiSet(Map<String, String> keyValueMap) {
        stringRedisTemplate.opsForValue().multiSet(keyValueMap);
    }

    /**
     * 当key不存在时设置值
     *
     * @param key   键
     * @param value 值
     * @return 是否设置成功
     */
    public Boolean setIfAbsent(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 当key不存在时设置值，同时设置过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间
     * @param unit  时间单位
     * @return 是否设置成功
     */
    public Boolean setIfAbsent(String key, String value, Long time, TimeUnit unit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, time, unit);
    }

    /**
     * 通过键获取对应的值
     *
     * @param key 键
     * @return 值
     */
    public String getValueByKey(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 通过键获取对应的值
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 批量获取值
     *
     * @param keys 键集合
     * @return 值列表
     */
    public List<String> multiGet(Collection<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 自增操作
     *
     * @param key 键
     * @return 自增后的值
     */
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 增加指定的值
     *
     * @param key   键
     * @param delta 增加的值
     * @return 增加后的值
     */
    public Long increment(String key, Long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减操作
     *
     * @param key 键
     * @return 自减后的值
     */
    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    /**
     * 减少指定的值
     *
     * @param key   键
     * @param delta 减少的值
     * @return 减少后的值
     */
    public Long decrement(String key, Long delta) {
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    // ============================== Hash操作 ==============================

    /**
     * 设置Hash的值
     *
     * @param key     键
     * @param hashKey hash键
     * @param value   值
     */
    public void hashSet(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 批量设置Hash的值
     *
     * @param key 键
     * @param map Hash键值对
     */
    public void hashMultiSet(String key, Map<String, String> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取Hash的值
     *
     * @param key     键
     * @param hashKey hash键
     * @return 值
     */
    public Object hashGet(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取Hash中所有的值
     *
     * @param key 键
     * @return 所有值的Map
     */
    public Map<Object, Object> hashGetAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除Hash中的值
     *
     * @param key      键
     * @param hashKeys hash键集合
     * @return 删除的数量
     */
    public Long hashDelete(String key, Object... hashKeys) {
        return stringRedisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断Hash中是否存在指定的键
     *
     * @param key     键
     * @param hashKey hash键
     * @return 是否存在
     */
    public Boolean hashHasKey(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * Hash中的键值自增
     *
     * @param key     键
     * @param hashKey hash键
     * @param delta   增加的值
     * @return 增加后的值
     */
    public Long hashIncrement(String key, String hashKey, Long delta) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    // ============================== List操作 ==============================

    /**
     * 从列表左边添加元素
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public Long listLeftPush(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从列表左边批量添加元素
     *
     * @param key    键
     * @param values 值集合
     * @return 列表长度
     */
    public Long listLeftPushAll(String key, Collection<String> values) {
        return stringRedisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 从列表右边添加元素
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public Long listRightPush(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从列表右边批量添加元素
     *
     * @param key    键
     * @param values 值集合
     * @return 列表长度
     */
    public Long listRightPushAll(String key, Collection<String> values) {
        return stringRedisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 从列表左边弹出元素
     *
     * @param key 键
     * @return 元素值
     */
    public String listLeftPop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从列表右边弹出元素
     *
     * @param key 键
     * @return 元素值
     */
    public String listRightPop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取列表长度
     *
     * @param key 键
     * @return 列表长度
     */
    public Long listSize(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   键
     * @param start 起始索引
     * @param end   结束索引
     * @return 元素列表
     */
    public List<String> listRange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    // ============================== Set操作 ==============================

    /**
     * 添加元素到集合
     *
     * @param key    键
     * @param values 值
     * @return 添加的数量
     */
    public Long setAdd(String key, String... values) {
        return stringRedisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取集合中的所有元素
     *
     * @param key 键
     * @return 元素集合
     */
    public Set<String> setMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 判断集合中是否存在元素
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    public Boolean setIsMember(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取集合的大小
     *
     * @param key 键
     * @return 集合大小
     */
    public Long setSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }

    /**
     * 从集合中移除元素
     *
     * @param key    键
     * @param values 值集合
     * @return 移除的数量
     */
    public Long setRemove(String key, Object... values) {
        return stringRedisTemplate.opsForSet().remove(key, values);
    }

    // ============================== ZSet操作 ==============================

    /**
     * 添加元素到有序集合
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public Boolean zSetAdd(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取有序集合中指定范围的元素
     *
     * @param key   键
     * @param start 起始索引
     * @param end   结束索引
     * @return 元素集合
     */
    public Set<String> zSetRange(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取有序集合中指定分数范围的元素
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素集合
     */
    public Set<String> zSetRangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取元素在有序集合中的排名
     *
     * @param key   键
     * @param value 值
     * @return 排名，从0开始
     */
    public Long zSetRank(String key, String value) {
        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取有序集合的大小
     *
     * @param key 键
     * @return 集合大小
     */
    public Long zSetSize(String key) {
        return stringRedisTemplate.opsForZSet().size(key);
    }

    /**
     * 从有序集合中移除元素
     *
     * @param key    键
     * @param values 值集合
     * @return 移除的数量
     */
    public Long zSetRemove(String key, Object... values) {
        return stringRedisTemplate.opsForZSet().remove(key, values);
    }

    // ============================== 通用操作 ==============================

    /**
     * 通过键模糊查询
     *
     * @param pattern 模糊查询的键
     * @return 键集合
     */
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public Boolean checkExists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 设置key的过期时间
     *
     * @param key  键
     * @param time 过期时间
     * @param unit 时间单位
     * @return 是否成功
     */
    public Boolean expire(String key, Long time, TimeUnit unit) {
        return stringRedisTemplate.expire(key, time, unit);
    }

    /**
     * 延长key的过期时间（毫秒）
     *
     * @param key  键
     * @param time 过期时间,毫秒
     * @return 是否成功
     */
    public Boolean expire(String key, Long time) {
        return stringRedisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }

    /**
     * 删除这个key
     *
     * @param key 键
     * @return 是否成功
     */
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除key
     *
     * @param keys 键集合
     * @return 删除的数量
     */
    public Long deleteMulti(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 获取剩余过期时间
     *
     * @param key  键
     * @param unit 时间单位
     * @return 剩余时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        return stringRedisTemplate.getExpire(key, unit);
    }

    /**
     * 获取剩余过期时间（秒）
     *
     * @param key 键
     * @return 剩余时间，秒
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
