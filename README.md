# personal-java-utils / selfuse-java-utils

> 个人 Java 工具类与通用组件沉淀：分布式锁、限流、日志切面、分页、数据校验、Spring 上下文、Redis/JWT 工具、常用配置等。  
> Personal Java utilities and reusable components: distributed lock, rate limit, logging aspect, pagination, validation, Spring context, Redis/JWT helpers, and common configs.

---

## ✨ Features | 功能概览

- 🔐 **Distributed Lock** 分布式锁（含 AOP 方式）
- 🚦 **Rate Limit** 限流（含 AOP 方式）
- 🧾 **Logging Aspect** 日志切面（请求/方法维度记录）
- 📄 **Pagination** 分页请求/结果封装 & 工具方法
- ✅ **Validation** 数据校验 & 断言工具
- 🌱 **Spring Utils** Spring 上下文工具（获取 Bean、配置、事件发布）
- ⚙️ **Configs** 常用配置：Jackson / MyBatis-Plus / Redis
- 🧰 **Utils** Redis / JWT / Security / ThreadPool / Throw 等工具类
- 📦 **Result Wrapper** 统一返回对象封装

---

## 📁 Project Structure | 目录结构

- **分布式锁**
  - `DistributedLock.java`
  - `DistributedLockAspect.java`
  - `DistributedLockService.java`
- **分页**
  - `PageRequest.java`
  - `PageResult.java`
  - `PaginationUtil.java`
- **数据校验**
  - `ValidationUtil.java` — 常用格式校验（邮箱/手机号/身份证/银行卡等）
  - `Assert.java` — 断言工具，校验失败抛异常
- **Spring 工具**
  - `SpringContextUtil.java` — Spring 上下文工具（获取 Bean/配置/发布事件）
- **配置类**
  - `JacksonConfig.java`
  - `MybatisPlusConfig.java`
  - `RedisConfig.java`
- **日志**
  - `Log.java`
  - `LogAspect.java`
  - `LogUtil.java`
- **限流**
  - `RateLimit.java`
  - `RateLimitAspect.java`
  - `RateLimiter.java`
- **Redis 操作**
  - `RedisUtil.java`
  - `RedisUtil版本2.java`（建议后续重命名为 `RedisUtilV2.java`）
- **其他工具**
  - `JwtUtil.java`
  - `Result.java`
  - `SecurityUtil.java`
  - `ThreadPoolUtil.java`
  - `ThrowUtil.java`

---
