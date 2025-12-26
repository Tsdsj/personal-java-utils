package com.utils.validation;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 数据校验工具类
 * <p>
 * 提供常用的数据格式校验方法，包括：
 * - 字符串校验（空、长度、格式）
 * - 常用格式校验（邮箱、手机号、身份证、URL等）
 * - 数字校验（范围、正负）
 * - 集合校验
 * </p>
 *
 * @author tt
 * @since 2025-12-26
 */
public class ValidationUtil {

    private ValidationUtil() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    // ==================== 正则表达式常量 ====================

    /**
     * 邮箱正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * 中国大陆手机号正则（11位，1开头）
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$"
    );

    /**
     * 固定电话正则（区号-号码，区号3-4位，号码7-8位）
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^0\\d{2,3}-?\\d{7,8}$"
    );

    /**
     * 18位身份证号正则
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$"
    );

    /**
     * URL正则
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$"
    );

    /**
     * 中文字符正则
     */
    private static final Pattern CHINESE_PATTERN = Pattern.compile(
            "^[\\u4e00-\\u9fa5]+$"
    );

    /**
     * 邮政编码正则（6位数字）
     */
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile(
            "^\\d{6}$"
    );

    /**
     * IPv4地址正则
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"
    );

    /**
     * 纯数字正则
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
            "^\\d+$"
    );

    /**
     * 字母数字下划线正则
     */
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]+$"
    );

    /**
     * 用户名正则（字母开头，允许字母数字下划线，4-16位）
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z][a-zA-Z0-9_]{3,15}$"
    );

    /**
     * 强密码正则（至少8位，包含大小写字母和数字）
     */
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$"
    );

    /**
     * 银行卡号正则（16-19位数字）
     */
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile(
            "^\\d{16,19}$"
    );

    /**
     * 车牌号正则（普通车牌 + 新能源车牌）
     */
    private static final Pattern LICENSE_PLATE_PATTERN = Pattern.compile(
            "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$"
    );

    // ==================== 字符串校验 ====================

    /**
     * 判断字符串是否为空（null 或 空字符串）
     *
     * @param str 待校验字符串
     * @return true-为空，false-不为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 待校验字符串
     * @return true-不为空，false-为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白（null、空字符串、纯空白字符）
     *
     * @param str 待校验字符串
     * @return true-为空白，false-不为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空白
     *
     * @param str 待校验字符串
     * @return true-不为空白，false-为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断多个字符串是否都不为空
     *
     * @param strs 待校验字符串数组
     * @return true-都不为空，false-存在空值
     */
    public static boolean isAllNotEmpty(String... strs) {
        if (strs == null || strs.length == 0) {
            return false;
        }
        for (String str : strs) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断多个字符串是否存在空值
     *
     * @param strs 待校验字符串数组
     * @return true-存在空值，false-都不为空
     */
    public static boolean hasEmpty(String... strs) {
        return !isAllNotEmpty(strs);
    }

    /**
     * 校验字符串长度是否在指定范围内
     *
     * @param str       待校验字符串
     * @param minLength 最小长度（包含）
     * @param maxLength 最大长度（包含）
     * @return true-在范围内，false-不在范围内
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength <= 0;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 校验字符串长度是否等于指定值
     *
     * @param str    待校验字符串
     * @param length 期望长度
     * @return true-长度相等，false-长度不等
     */
    public static boolean isLength(String str, int length) {
        return str != null && str.length() == length;
    }

    // ==================== 格式校验 ====================

    /**
     * 校验是否为有效的邮箱地址
     *
     * @param email 待校验邮箱
     * @return true-有效，false-无效
     */
    public static boolean isEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 校验是否为有效的手机号（中国大陆11位）
     *
     * @param mobile 待校验手机号
     * @return true-有效，false-无效
     */
    public static boolean isMobile(String mobile) {
        return isNotEmpty(mobile) && MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * 校验是否为有效的固定电话
     *
     * @param phone 待校验电话号码
     * @return true-有效，false-无效
     */
    public static boolean isPhone(String phone) {
        return isNotEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 校验是否为有效的18位身份证号
     *
     * @param idCard 待校验身份证号
     * @return true-有效，false-无效
     */
    public static boolean isIdCard(String idCard) {
        if (isEmpty(idCard) || !ID_CARD_PATTERN.matcher(idCard).matches()) {
            return false;
        }
        // 校验校验码
        return validateIdCardChecksum(idCard);
    }

    /**
     * 校验身份证校验码
     */
    private static boolean validateIdCardChecksum(String idCard) {
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * weights[i];
        }
        
        char expectedCheck = checkCodes[sum % 11];
        char actualCheck = Character.toUpperCase(idCard.charAt(17));
        
        return expectedCheck == actualCheck;
    }

    /**
     * 校验是否为有效的URL
     *
     * @param url 待校验URL
     * @return true-有效，false-无效
     */
    public static boolean isUrl(String url) {
        return isNotEmpty(url) && URL_PATTERN.matcher(url).matches();
    }

    /**
     * 校验是否为纯中文
     *
     * @param str 待校验字符串
     * @return true-纯中文，false-非纯中文
     */
    public static boolean isChinese(String str) {
        return isNotEmpty(str) && CHINESE_PATTERN.matcher(str).matches();
    }

    /**
     * 校验是否包含中文
     *
     * @param str 待校验字符串
     * @return true-包含中文，false-不包含中文
     */
    public static boolean containsChinese(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (c >= '\u4e00' && c <= '\u9fa5') {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验是否为有效的邮政编码（6位数字）
     *
     * @param postalCode 待校验邮政编码
     * @return true-有效，false-无效
     */
    public static boolean isPostalCode(String postalCode) {
        return isNotEmpty(postalCode) && POSTAL_CODE_PATTERN.matcher(postalCode).matches();
    }

    /**
     * 校验是否为有效的IPv4地址
     *
     * @param ip 待校验IP地址
     * @return true-有效，false-无效
     */
    public static boolean isIPv4(String ip) {
        return isNotEmpty(ip) && IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 校验是否为纯数字
     *
     * @param str 待校验字符串
     * @return true-纯数字，false-非纯数字
     */
    public static boolean isNumber(String str) {
        return isNotEmpty(str) && NUMBER_PATTERN.matcher(str).matches();
    }

    /**
     * 校验是否为字母数字下划线组合
     *
     * @param str 待校验字符串
     * @return true-符合，false-不符合
     */
    public static boolean isAlphanumeric(String str) {
        return isNotEmpty(str) && ALPHANUMERIC_PATTERN.matcher(str).matches();
    }

    /**
     * 校验是否为有效的用户名（字母开头，字母数字下划线，4-16位）
     *
     * @param username 待校验用户名
     * @return true-有效，false-无效
     */
    public static boolean isUsername(String username) {
        return isNotEmpty(username) && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 校验是否为强密码（至少8位，包含大小写字母和数字）
     *
     * @param password 待校验密码
     * @return true-强密码，false-弱密码
     */
    public static boolean isStrongPassword(String password) {
        return isNotEmpty(password) && STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 校验是否为有效的银行卡号（16-19位数字，Luhn算法校验）
     *
     * @param bankCard 待校验银行卡号
     * @return true-有效，false-无效
     */
    public static boolean isBankCard(String bankCard) {
        if (isEmpty(bankCard) || !BANK_CARD_PATTERN.matcher(bankCard).matches()) {
            return false;
        }
        // Luhn算法校验
        return validateLuhn(bankCard);
    }

    /**
     * Luhn算法校验（银行卡校验）
     */
    private static boolean validateLuhn(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    /**
     * 校验是否为有效的车牌号
     *
     * @param licensePlate 待校验车牌号
     * @return true-有效，false-无效
     */
    public static boolean isLicensePlate(String licensePlate) {
        return isNotEmpty(licensePlate) && LICENSE_PLATE_PATTERN.matcher(licensePlate).matches();
    }

    // ==================== 数字校验 ====================

    /**
     * 校验数字是否在指定范围内
     *
     * @param num 待校验数字
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return true-在范围内，false-不在范围内
     */
    public static boolean isInRange(int num, int min, int max) {
        return num >= min && num <= max;
    }

    /**
     * 校验数字是否在指定范围内
     *
     * @param num 待校验数字
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return true-在范围内，false-不在范围内
     */
    public static boolean isInRange(long num, long min, long max) {
        return num >= min && num <= max;
    }

    /**
     * 校验是否为正数
     *
     * @param num 待校验数字
     * @return true-正数，false-非正数
     */
    public static boolean isPositive(Number num) {
        return num != null && num.doubleValue() > 0;
    }

    /**
     * 校验是否为非负数
     *
     * @param num 待校验数字
     * @return true-非负数，false-负数
     */
    public static boolean isNonNegative(Number num) {
        return num != null && num.doubleValue() >= 0;
    }

    /**
     * 校验是否为负数
     *
     * @param num 待校验数字
     * @return true-负数，false-非负数
     */
    public static boolean isNegative(Number num) {
        return num != null && num.doubleValue() < 0;
    }

    // ==================== 集合校验 ====================

    /**
     * 判断集合是否为空
     *
     * @param collection 待校验集合
     * @return true-为空，false-不为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 待校验集合
     * @return true-不为空，false-为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断Map是否为空
     *
     * @param map 待校验Map
     * @return true-为空，false-不为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断Map是否不为空
     *
     * @param map 待校验Map
     * @return true-不为空，false-为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断数组是否为空
     *
     * @param array 待校验数组
     * @return true-为空，false-不为空
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否不为空
     *
     * @param array 待校验数组
     * @return true-不为空，false-为空
     */
    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    // ==================== 对象校验 ====================

    /**
     * 判断对象是否为null
     *
     * @param obj 待校验对象
     * @return true-为null，false-不为null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断对象是否不为null
     *
     * @param obj 待校验对象
     * @return true-不为null，false-为null
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 判断所有对象是否都不为null
     *
     * @param objects 待校验对象数组
     * @return true-都不为null，false-存在null
     */
    public static boolean isAllNotNull(Object... objects) {
        if (objects == null || objects.length == 0) {
            return false;
        }
        for (Object obj : objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    // ==================== 自定义正则校验 ====================

    /**
     * 使用自定义正则表达式校验
     *
     * @param str   待校验字符串
     * @param regex 正则表达式
     * @return true-匹配，false-不匹配
     */
    public static boolean matches(String str, String regex) {
        if (isEmpty(str) || isEmpty(regex)) {
            return false;
        }
        return Pattern.matches(regex, str);
    }

    /**
     * 使用预编译的Pattern校验
     *
     * @param str     待校验字符串
     * @param pattern 预编译的Pattern
     * @return true-匹配，false-不匹配
     */
    public static boolean matches(String str, Pattern pattern) {
        if (isEmpty(str) || pattern == null) {
            return false;
        }
        return pattern.matcher(str).matches();
    }
}
