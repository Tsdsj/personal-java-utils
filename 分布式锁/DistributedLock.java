package com.tt.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解，用于在方法级别实现分布式锁功能
 *
 * <p>使用场景：
 * <ul>
 *   <li>并发控制：防止相同资源被多个请求同时修改</li>
 *   <li>幂等控制：避免相同操作被重复执行</li>
 *   <li>分布式系统中的资源互斥访问</li>
 * </ul>
 *
 * <p>使用示例：
 * <p>1. 基本使用 - 使用方法参数作为锁的key：
 * <pre>
 * {@code
 * @DistributedLock(keys = {"#orderId"})
 * public Result<OrderResult> processOrder(String orderId) {
 *     // 处理订单的业务逻辑
 *     return Result.success(orderService.process(orderId));
 * }
 * }
 * </pre>
 *
 * <p>2. 多个参数组合作为锁的key：
 * <pre>
 * {@code
 * @DistributedLock(keys = {"#userId", "#productId"})
 * public Result<Boolean> purchaseProduct(String userId, String productId) {
 *     // 用户购买产品逻辑
 *     return Result.success(productService.purchase(userId, productId));
 * }
 * }
 * </pre>
 *
 * <p>3. 使用复杂对象的属性作为锁的key：
 * <pre>
 * {@code
 * @DistributedLock(keys = {"#orderRequest.orderId", "#orderRequest.userId"})
 * public Result<OrderResult> createOrder(OrderRequest orderRequest) {
 *     // 创建订单逻辑
 *     return Result.success(orderService.create(orderRequest));
 * }
 * }
 * </pre>
 *
 * <p>4. 自定义锁过期时间和续期设置：
 * <pre>
 * {@code
 * @DistributedLock(
 *     keys = {"#paymentId"},
 *     expireTime = 60,
 *     timeUnit = TimeUnit.SECONDS,
 *     autoRenew = true,
 *     renewInterval = 20,
 *     timeout = 5
 * )
 * public Result<PaymentResult> processPayment(String paymentId) {
 *     // 支付处理逻辑
 *     return Result.success(paymentService.process(paymentId));
 * }
 * }
 * </pre>
 *
 * <p>5. 自定义请求ID：
 * <pre>
 * {@code
 * @DistributedLock(
 *     keys = {"#request.resourceId"},
 *     requestId = "#request.requestId"
 * )
 * public Result<ProcessResult> processWithCustomRequestId(ProcessRequest request) {
 *     // 使用请求中的ID作为锁的请求标识
 *     return Result.success(processService.execute(request));
 * }
 * }
 * </pre>
 *
 * <p>6. 在Controller中使用：
 * <pre>
 * {@code
 * @PostMapping("/orders")
 * @DistributedLock(keys = {"#orderDTO.orderId"})
 * public Result<OrderVO> createOrder(@RequestBody OrderDTO orderDTO) {
 *     OrderVO order = orderService.createOrder(orderDTO);
 *     return Result.success(order);
 * }
 * }
 * </pre>
 *
 * @author tt
 * @version 1.0
 * @since 2025/04/03 10:26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 锁的资源表达式，支持Spring EL表达式
     */
    String[] keys();

    /**
     * 锁的请求ID表达式，可以用 Spring EL 表达式，默认使用 UUID
     */
    String requestId() default "";

    /**
     * 锁的过期时间,单位秒
     */
    long expireTime() default 30;

    /**
     * 锁过期时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否自动续期
     */
    boolean autoRenew() default true;

    /**
     * 续期间隔（秒）
     */
    long renewInterval() default 10;

    /**
     * 操作超时时间（分钟）
     */
    long timeout() default 10;

}