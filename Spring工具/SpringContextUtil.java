package com.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Spring 上下文工具类
 * <p>
 * 提供静态方法获取 Spring 容器中的 Bean、Environment、发布事件等功能
 * 适用于非 Spring 管理的类中获取 Bean 的场景
 * </p>
 *
 * <p>使用方式：确保此类被 Spring 扫描到（@Component 或 XML 配置）</p>
 *
 * @author tt
 * @since 2025-12-26
 */
@Component
public class SpringContextUtil implements ApplicationContextAware, BeanFactoryPostProcessor {

    private static ApplicationContext applicationContext;
    private static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringContextUtil.beanFactory = beanFactory;
    }

    // ==================== 获取上下文 ====================

    /**
     * 获取 ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * 获取 BeanFactory
     *
     * @return ListableBeanFactory
     */
    public static ListableBeanFactory getBeanFactory() {
        return beanFactory != null ? beanFactory : applicationContext;
    }

    // ==================== 获取 Bean ====================

    /**
     * 通过名称获取 Bean
     *
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        assertContextInjected();
        return getBeanFactory().getBean(name);
    }

    /**
     * 通过类型获取 Bean
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型泛型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        assertContextInjected();
        return getBeanFactory().getBean(clazz);
    }

    /**
     * 通过名称和类型获取 Bean
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @param <T>   Bean 类型泛型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        assertContextInjected();
        return getBeanFactory().getBean(name, clazz);
    }

    /**
     * 通过名称和构造参数获取 Bean
     *
     * @param name Bean 名称
     * @param args 构造参数
     * @return Bean 实例
     */
    public static Object getBean(String name, Object... args) {
        assertContextInjected();
        return getBeanFactory().getBean(name, args);
    }

    /**
     * 获取指定类型的所有 Bean
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型泛型
     * @return Bean 名称与实例的 Map
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        assertContextInjected();
        return getBeanFactory().getBeansOfType(clazz);
    }

    /**
     * 获取带有指定注解的所有 Bean
     *
     * @param annotationType 注解类型
     * @return Bean 名称与实例的 Map
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        assertContextInjected();
        return getBeanFactory().getBeansWithAnnotation(annotationType);
    }

    /**
     * 获取 Bean 上的指定注解
     *
     * @param beanName       Bean 名称
     * @param annotationType 注解类型
     * @param <A>            注解类型泛型
     * @return 注解实例，不存在则返回 null
     */
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        assertContextInjected();
        return getBeanFactory().findAnnotationOnBean(beanName, annotationType);
    }

    // ==================== Bean 判断 ====================

    /**
     * 判断是否包含指定名称的 Bean
     *
     * @param name Bean 名称
     * @return true-包含，false-不包含
     */
    public static boolean containsBean(String name) {
        assertContextInjected();
        return getBeanFactory().containsBean(name);
    }

    /**
     * 判断 Bean 是否为单例
     *
     * @param name Bean 名称
     * @return true-单例，false-非单例
     */
    public static boolean isSingleton(String name) {
        assertContextInjected();
        return getBeanFactory().isSingleton(name);
    }

    /**
     * 判断 Bean 是否为原型
     *
     * @param name Bean 名称
     * @return true-原型，false-非原型
     */
    public static boolean isPrototype(String name) {
        assertContextInjected();
        return getBeanFactory().isPrototype(name);
    }

    /**
     * 获取 Bean 的类型
     *
     * @param name Bean 名称
     * @return Bean 的 Class 类型
     */
    public static Class<?> getType(String name) {
        assertContextInjected();
        return getBeanFactory().getType(name);
    }

    /**
     * 获取 Bean 的所有别名
     *
     * @param name Bean 名称
     * @return 别名数组
     */
    public static String[] getAliases(String name) {
        assertContextInjected();
        return getBeanFactory().getAliases(name);
    }

    // ==================== 环境与配置 ====================

    /**
     * 获取 Environment
     *
     * @return Environment
     */
    public static Environment getEnvironment() {
        assertContextInjected();
        return applicationContext.getEnvironment();
    }

    /**
     * 获取配置属性值
     *
     * @param key 配置键
     * @return 配置值
     */
    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    /**
     * 获取配置属性值，带默认值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getProperty(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * 获取配置属性值并转换为指定类型
     *
     * @param key        配置键
     * @param targetType 目标类型
     * @param <T>        目标类型泛型
     * @return 配置值
     */
    public static <T> T getProperty(String key, Class<T> targetType) {
        return getEnvironment().getProperty(key, targetType);
    }

    /**
     * 获取配置属性值并转换为指定类型，带默认值
     *
     * @param key          配置键
     * @param targetType   目标类型
     * @param defaultValue 默认值
     * @param <T>          目标类型泛型
     * @return 配置值
     */
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return getEnvironment().getProperty(key, targetType, defaultValue);
    }

    /**
     * 获取必须的配置属性值
     *
     * @param key 配置键
     * @return 配置值
     * @throws IllegalStateException 如果配置不存在
     */
    public static String getRequiredProperty(String key) {
        return getEnvironment().getRequiredProperty(key);
    }

    /**
     * 获取当前激活的 Profile
     *
     * @return 激活的 Profile 数组
     */
    public static String[] getActiveProfiles() {
        return getEnvironment().getActiveProfiles();
    }

    /**
     * 判断指定 Profile 是否激活
     *
     * @param profile Profile 名称
     * @return true-激活，false-未激活
     */
    public static boolean isProfileActive(String profile) {
        String[] activeProfiles = getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            if (activeProfile.equals(profile)) {
                return true;
            }
        }
        return false;
    }

    // ==================== 事件发布 ====================

    /**
     * 发布应用事件
     *
     * @param event 事件对象
     */
    public static void publishEvent(ApplicationEvent event) {
        assertContextInjected();
        applicationContext.publishEvent(event);
    }

    /**
     * 发布应用事件（支持任意对象作为事件）
     *
     * @param event 事件对象
     */
    public static void publishEvent(Object event) {
        assertContextInjected();
        applicationContext.publishEvent(event);
    }

    // ==================== 其他 ====================

    /**
     * 获取应用名称
     *
     * @return 应用名称
     */
    public static String getApplicationName() {
        return getProperty("spring.application.name", "");
    }

    /**
     * 获取应用启动时间
     *
     * @return 启动时间戳（毫秒）
     */
    public static long getStartupDate() {
        assertContextInjected();
        return applicationContext.getStartupDate();
    }

    /**
     * 判断是否为开发环境
     *
     * @return true-开发环境，false-非开发环境
     */
    public static boolean isDev() {
        return isProfileActive("dev") || isProfileActive("development");
    }

    /**
     * 判断是否为测试环境
     *
     * @return true-测试环境，false-非测试环境
     */
    public static boolean isTest() {
        return isProfileActive("test") || isProfileActive("testing");
    }

    /**
     * 判断是否为生产环境
     *
     * @return true-生产环境，false-非生产环境
     */
    public static boolean isProd() {
        return isProfileActive("prod") || isProfileActive("production");
    }

    // ==================== 私有方法 ====================

    /**
     * 断言 ApplicationContext 已注入
     */
    private static void assertContextInjected() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                    "ApplicationContext 未注入，请确保 SpringContextUtil 已被 Spring 容器管理"
            );
        }
    }
}
