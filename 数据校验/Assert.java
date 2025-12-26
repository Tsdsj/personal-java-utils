package com.utils.validation;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 断言工具类
 * <p>
 * 用于业务参数校验，校验失败时抛出 IllegalArgumentException
 * 可与 ValidationUtil 配合使用，简化参数校验代码
 * </p>
 *
 * @author tt
 * @since 2025-12-26
 */
public class Assert {

    private Assert() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    // ==================== 基础断言 ====================

    /**
     * 断言表达式为 true，否则抛出异常
     *
     * @param expression 布尔表达式
     * @param message    异常信息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式为 true，否则抛出异常（延迟构建异常信息）
     *
     * @param expression      布尔表达式
     * @param messageSupplier 异常信息提供者
     */
    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 断言表达式为 false，否则抛出异常
     *
     * @param expression 布尔表达式
     * @param message    异常信息
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    // ==================== 对象断言 ====================

    /**
     * 断言对象不为 null，否则抛出异常
     *
     * @param object  待校验对象
     * @param message 异常信息
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言对象不为 null，否则抛出异常（延迟构建异常信息）
     *
     * @param object          待校验对象
     * @param messageSupplier 异常信息提供者
     */
    public static void notNull(Object object, Supplier<String> messageSupplier) {
        if (object == null) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 断言对象为 null，否则抛出异常
     *
     * @param object  待校验对象
     * @param message 异常信息
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    // ==================== 字符串断言 ====================

    /**
     * 断言字符串不为空（非null且非空字符串），否则抛出异常
     *
     * @param str     待校验字符串
     * @param message 异常信息
     */
    public static void notEmpty(String str, String message) {
        if (ValidationUtil.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串不为空白（非null、非空、非纯空白），否则抛出异常
     *
     * @param str     待校验字符串
     * @param message 异常信息
     */
    public static void notBlank(String str, String message) {
        if (ValidationUtil.isBlank(str)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串长度在指定范围内，否则抛出异常
     *
     * @param str       待校验字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @param message   异常信息
     */
    public static void lengthInRange(String str, int minLength, int maxLength, String message) {
        if (!ValidationUtil.isLengthInRange(str, minLength, maxLength)) {
            throw new IllegalArgumentException(message);
        }
    }

    // ==================== 格式断言 ====================

    /**
     * 断言为有效邮箱，否则抛出异常
     *
     * @param email   待校验邮箱
     * @param message 异常信息
     */
    public static void isEmail(String email, String message) {
        if (!ValidationUtil.isEmail(email)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言为有效手机号，否则抛出异常
     *
     * @param mobile  待校验手机号
     * @param message 异常信息
     */
    public static void isMobile(String mobile, String message) {
        if (!ValidationUtil.isMobile(mobile)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言为有效身份证号，否则抛出异常
     *
     * @param idCard  待校验身份证号
     * @param message 异常信息
     */
    public static void isIdCard(String idCard, String message) {
        if (!ValidationUtil.isIdCard(idCard)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言为有效URL，否则抛出异常
     *
     * @param url     待校验URL
     * @param message 异常信息
     */
    public static void isUrl(String url, String message) {
        if (!ValidationUtil.isUrl(url)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言为有效用户名，否则抛出异常
     *
     * @param username 待校验用户名
     * @param message  异常信息
     */
    public static void isUsername(String username, String message) {
        if (!ValidationUtil.isUsername(username)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言为强密码，否则抛出异常
     *
     * @param password 待校验密码
     * @param message  异常信息
     */
    public static void isStrongPassword(String password, String message) {
        if (!ValidationUtil.isStrongPassword(password)) {
            throw new IllegalArgumentException(message);
        }
    }

    // ==================== 数字断言 ====================

    /**
     * 断言数字在指定范围内，否则抛出异常
     *
     * @param num     待校验数字
     * @param min     最小值
     * @param max     最大值
     * @param message 异常信息
     */
    public static void inRange(int num, int min, int max, String message) {
        if (!ValidationUtil.isInRange(num, min, max)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言数字在指定范围内，否则抛出异常
     *
     * @param num     待校验数字
     * @param min     最小值
     * @param max     最大值
     * @param message 异常信息
     */
    public static void inRange(long num, long min, long max, String message) {
        if (!ValidationUtil.isInRange(num, min, max)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言为正数，否则抛出异常
     *
     * @param num     待校验数字
     * @param message 异常信息
     */
    public static void isPositive(Number num, String message) {
        if (!ValidationUtil.isPositive(num)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言为非负数，否则抛出异常
     *
     * @param num     待校验数字
     * @param message 异常信息
     */
    public static void isNonNegative(Number num, String message) {
        if (!ValidationUtil.isNonNegative(num)) {
            throw new IllegalArgumentException(message);
        }
    }

    // ==================== 集合断言 ====================

    /**
     * 断言集合不为空，否则抛出异常
     *
     * @param collection 待校验集合
     * @param message    异常信息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (ValidationUtil.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言Map不为空，否则抛出异常
     *
     * @param map     待校验Map
     * @param message 异常信息
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (ValidationUtil.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言数组不为空，否则抛出异常
     *
     * @param array   待校验数组
     * @param message 异常信息
     */
    public static void notEmpty(Object[] array, String message) {
        if (ValidationUtil.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }

    // ==================== 业务断言 ====================

    /**
     * 断言两个对象相等，否则抛出异常
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message  异常信息
     */
    public static void equals(Object expected, Object actual, String message) {
        if (expected == null) {
            if (actual != null) {
                throw new IllegalArgumentException(message);
            }
        } else if (!expected.equals(actual)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言两个对象不相等，否则抛出异常
     *
     * @param unexpected 不期望的值
     * @param actual     实际值
     * @param message    异常信息
     */
    public static void notEquals(Object unexpected, Object actual, String message) {
        if (unexpected == null) {
            if (actual == null) {
                throw new IllegalArgumentException(message);
            }
        } else if (unexpected.equals(actual)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串匹配指定正则，否则抛出异常
     *
     * @param str     待校验字符串
     * @param regex   正则表达式
     * @param message 异常信息
     */
    public static void matches(String str, String regex, String message) {
        if (!ValidationUtil.matches(str, regex)) {
            throw new IllegalArgumentException(message);
        }
    }

    // ==================== 私有方法 ====================

    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return messageSupplier != null ? messageSupplier.get() : null;
    }
}
