# Redis 接入技术方案

## 文档信息

- **版本**：v1.0
- **创建日期**：2026年1月18日
- **适用范围**：Interest Tracker 后端系统
- **技术栈**：Spring Boot 3.0.x + Redis

---

## 目录

1. [背景与需求分析](#1-背景与需求分析)
2. [Redis 使用场景](#2-redis-使用场景)
3. [技术选型](#3-技术选型)
4. [配置方案](#4-配置方案)
5. [代码实现方案](#5-代码实现方案)
6. [缓存策略设计](#6-缓存策略设计)
7. [工具类封装](#7-工具类封装)
8. [最佳实践](#8-最佳实践)
9. [注意事项与故障处理](#9-注意事项与故障处理)
10. [实施计划](#10-实施计划)

---

## 1. 背景与需求分析

### 1.1 为什么需要 Redis

当前系统存在以下性能瓶颈和需求：

1. **数据库查询压力大**
   - Dashboard 统计查询涉及多表聚合，响应时间长
   - 频繁查询用户数据、统计数据
   - 热点数据重复查询

2. **第三方 API 调用限制**
   - TMDB API 有调用频率限制
   - 相同搜索关键词重复调用
   - 需要缓存 API 响应结果

3. **用户体验优化**
   - 统计数据加载慢
   - 列表查询响应时间长
   - 需要提升系统响应速度

4. **系统扩展性**
   - 未来需要分布式锁
   - 需要会话管理（JWT Token 黑名单）
   - 需要消息队列（可选）

### 1.2 Redis 带来的价值

- ✅ **性能提升**：热点数据缓存，减少数据库查询
- ✅ **成本降低**：减少第三方 API 调用次数
- ✅ **用户体验**：响应速度提升 3-10 倍
- ✅ **系统稳定性**：降低数据库压力，提升系统承载能力
- ✅ **功能扩展**：支持分布式锁、会话管理等高级功能

---

## 2. Redis 使用场景

### 2.1 数据缓存场景

#### 2.1.1 统计数据缓存

**场景**：Dashboard 统计数据

**缓存内容**：
- 用户各类数据统计（照片数、影视数、音乐数等）
- 时间范围统计数据（今日、本周、本月、本年）
- 趋势数据（最近 N 天的数据）

**缓存策略**：
- 缓存时间：5-30 分钟（根据数据更新频率）
- 缓存键格式：`dashboard:summary:{userId}:{timeRange}`
- 失效策略：数据更新时主动失效

**示例**：
```
dashboard:summary:1:week
dashboard:summary:1:month
dashboard:trend:1:7days
```

#### 2.1.2 第三方 API 结果缓存

**场景**：TMDB、豆瓣等第三方 API 调用

**缓存内容**：
- 影视搜索结果
- 影视详情信息
- 音乐搜索结果

**缓存策略**：
- 搜索结果：缓存 1 小时
- 详情信息：缓存 24 小时
- 缓存键格式：`api:tmdb:search:{keyword}:{page}`

**示例**：
```
api:tmdb:search:复仇者联盟:1
api:tmdb:detail:movie:550
api:tmdb:detail:tv:1396
```

#### 2.1.3 热点数据缓存

**场景**：频繁查询的数据

**缓存内容**：
- 用户基本信息
- 影视/音乐/书籍详情
- 最近活动列表

**缓存策略**：
- 缓存时间：10-30 分钟
- 缓存键格式：`user:info:{userId}`、`movie:detail:{movieId}`

**示例**：
```
user:info:1
movie:detail:100
photo:recent:1:10
```

### 2.2 会话管理场景

#### 2.2.1 JWT Token 黑名单

**场景**：用户登出后，Token 需要失效

**实现方式**：
- 登出时将 Token 加入黑名单
- 校验 Token 时检查黑名单
- 缓存时间：Token 过期时间

**缓存键格式**：`token:blacklist:{tokenHash}`

### 2.3 分布式锁场景

#### 2.3.1 防止重复操作

**场景**：
- 防止重复创建记录
- 防止并发更新冲突
- 定时任务防重复执行

**实现方式**：使用 Redis SETNX 或 Redisson 分布式锁

**示例**：
```
lock:create:movie:{userId}:{tmdbId}
lock:update:photo:{photoId}
lock:task:dashboard:stats:{userId}
```

### 2.4 计数器场景

#### 2.4.1 统计计数

**场景**：
- 照片查看次数
- 点赞数统计
- 用户活跃度统计

**实现方式**：使用 Redis INCR 命令

**示例**：
```
counter:photo:view:{photoId}
counter:photo:like:{photoId}
counter:user:active:{userId}:{date}
```

### 2.5 限流场景（未来扩展）

#### 2.5.1 API 限流

**场景**：防止 API 被恶意调用

**实现方式**：使用 Redis 滑动窗口或令牌桶算法

---

## 3. 技术选型

### 3.1 Redis 客户端选型

#### 方案对比

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **Spring Data Redis** | 1. Spring 官方支持<br>2. 集成简单<br>3. 支持多种序列化方式<br>4. 支持 Spring Cache | 1. 性能略低于原生客户端<br>2. 功能封装较多 | ⭐⭐⭐⭐⭐ |
| **Lettuce** | 1. 异步非阻塞<br>2. 性能优秀<br>3. 支持集群 | 1. 需要手动配置<br>2. 学习成本较高 | ⭐⭐⭐⭐ |
| **Jedis** | 1. 成熟稳定<br>2. 功能完整 | 1. 同步阻塞<br>2. 连接池管理复杂 | ⭐⭐⭐ |

#### 推荐方案：Spring Data Redis + Lettuce

**理由**：
1. Spring Boot 3.x 默认使用 Lettuce（异步非阻塞）
2. Spring Data Redis 提供统一抽象，易于使用
3. 支持 Spring Cache 注解，开发效率高
4. 性能满足需求

### 3.2 序列化方案选型

#### 方案对比

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **Jackson2JsonRedisSerializer** | 1. 可读性好<br>2. 支持复杂对象<br>3. 跨语言兼容 | 1. 占用空间较大<br>2. 性能略低 | ⭐⭐⭐⭐⭐ |
| **GenericJackson2JsonRedisSerializer** | 1. 自动处理类型信息<br>2. 反序列化安全 | 1. 占用空间更大 | ⭐⭐⭐⭐ |
| **StringRedisSerializer** | 1. 性能最好<br>2. 占用空间小 | 1. 只支持字符串<br>2. 需要手动序列化 | ⭐⭐⭐ |
| **JdkSerializationRedisSerializer** | 1. 支持所有对象 | 1. 不可读<br>2. 性能差<br>3. 不推荐 | ❌ |

#### 推荐方案：Jackson2JsonRedisSerializer

**理由**：
1. 可读性好，便于调试
2. 支持复杂对象序列化
3. 性能满足需求
4. 跨语言兼容（如果未来需要其他语言访问）

### 3.3 依赖版本

```xml
<!-- Spring Boot 3.0.x 已包含 Spring Data Redis -->
<!-- 需要显式添加 Lettuce 连接池依赖 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

---

## 4. 配置方案

### 4.1 Maven 依赖配置

#### 4.1.1 父 pom.xml

在 `backend/pom.xml` 的 `<dependencyManagement>` 中添加：

```xml
<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>${spring-boot.version}</version>
</dependency>

<!-- Lettuce 连接池 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.11.1</version>
</dependency>
```

#### 4.1.2 子模块 pom.xml

在 `backend/interest-tracker-server/pom.xml` 的 `<dependencies>` 中添加：

```xml
<!-- Spring Data Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Lettuce 连接池 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

### 4.2 配置文件

#### 4.2.1 application.yml

在 `backend/interest-tracker-server/src/main/resources/application.yml` 中添加：

```yaml
# Redis 配置
spring:
  data:
    redis:
      # Redis 服务器地址（默认 localhost）
      host: ${REDIS_HOST:localhost}
      # Redis 服务器端口（默认 6379）
      port: ${REDIS_PORT:6379}
      # Redis 数据库索引（默认 0）
      database: ${REDIS_DATABASE:0}
      # Redis 密码（如果有）
      password: ${REDIS_PASSWORD:}
      # 连接超时时间（毫秒）
      timeout: 3000ms
      # Lettuce 连接池配置
      lettuce:
        pool:
          # 连接池最大连接数（使用负值表示没有限制）
          max-active: 8
          # 连接池最大阻塞等待时间（使用负值表示没有限制）
          max-wait: -1ms
          # 连接池中的最大空闲连接
          max-idle: 8
          # 连接池中的最小空闲连接
          min-idle: 0
```

#### 4.2.2 application-dev.yml（开发环境）

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      password: 
```

#### 4.2.3 application-prod.yml（生产环境，可选）

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT:6379}
      database: ${REDIS_DATABASE:0}
      password: ${REDIS_PASSWORD}
      timeout: 5000ms
      lettuce:
        pool:
          max-active: 16
          max-wait: 3000ms
          max-idle: 8
          min-idle: 2
```

### 4.3 Redis 配置类

创建 `backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/redis/config/RedisConfig.java`：

```java
package com.interest.tracker.framework.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 *
 * @author interest-tracker
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate
     *
     * @param connectionFactory Redis 连接工厂
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 Jackson2JsonRedisSerializer 来序列化和反序列化 redis 的 value 值
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        serializer.setObjectMapper(mapper);

        // 使用 StringRedisSerializer 来序列化和反序列化 redis 的 key 值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
```

---

## 5. 代码实现方案

### 5.1 Redis 工具类封装

创建 `backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/redis/core/RedisKeyDefine.java`：

```java
package com.interest.tracker.framework.redis.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.Duration;

/**
 * Redis Key 定义类
 * 统一管理所有 Redis Key，避免 Key 冲突
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public class RedisKeyDefine {

    /**
     * Key 模板
     */
    private final String keyTemplate;
    /**
     * Value 类型
     */
    private final Class<?> valueType;
    /**
     * 超时时间
     */
    private final Duration timeout;

    // ==================== Dashboard 相关 ====================

    /**
     * Dashboard 统计概览
     * Key: dashboard:summary:{userId}:{timeRange}
     * Timeout: 5 分钟
     */
    public static final RedisKeyDefine DASHBOARD_SUMMARY = new RedisKeyDefine(
            "dashboard:summary:%s:%s",
            Object.class,
            Duration.ofMinutes(5)
    );

    /**
     * Dashboard 趋势数据
     * Key: dashboard:trend:{userId}:{days}
     * Timeout: 10 分钟
     */
    public static final RedisKeyDefine DASHBOARD_TREND = new RedisKeyDefine(
            "dashboard:trend:%s:%s",
            Object.class,
            Duration.ofMinutes(10)
    );

    /**
     * Dashboard 最近活动
     * Key: dashboard:recent:{userId}:{limit}
     * Timeout: 5 分钟
     */
    public static final RedisKeyDefine DASHBOARD_RECENT = new RedisKeyDefine(
            "dashboard:recent:%s:%s",
            Object.class,
            Duration.ofMinutes(5)
    );

    // ==================== 第三方 API 缓存 ====================

    /**
     * TMDB 搜索结果
     * Key: api:tmdb:search:{keyword}:{page}
     * Timeout: 1 小时
     */
    public static final RedisKeyDefine TMDB_SEARCH = new RedisKeyDefine(
            "api:tmdb:search:%s:%s",
            Object.class,
            Duration.ofHours(1)
    );

    /**
     * TMDB 电影详情
     * Key: api:tmdb:movie:{tmdbId}
     * Timeout: 24 小时
     */
    public static final RedisKeyDefine TMDB_MOVIE_DETAIL = new RedisKeyDefine(
            "api:tmdb:movie:%s",
            Object.class,
            Duration.ofHours(24)
    );

    /**
     * TMDB 电视剧详情
     * Key: api:tmdb:tv:{tmdbId}
     * Timeout: 24 小时
     */
    public static final RedisKeyDefine TMDB_TV_DETAIL = new RedisKeyDefine(
            "api:tmdb:tv:%s",
            Object.class,
            Duration.ofHours(24)
    );

    // ==================== 用户相关 ====================

    /**
     * 用户信息
     * Key: user:info:{userId}
     * Timeout: 30 分钟
     */
    public static final RedisKeyDefine USER_INFO = new RedisKeyDefine(
            "user:info:%s",
            Object.class,
            Duration.ofMinutes(30)
    );

    // ==================== Token 黑名单 ====================

    /**
     * Token 黑名单
     * Key: token:blacklist:{tokenHash}
     * Timeout: 根据 Token 过期时间动态设置
     */
    public static final RedisKeyDefine TOKEN_BLACKLIST = new RedisKeyDefine(
            "token:blacklist:%s",
            String.class,
            null // 动态设置
    );

    // ==================== 分布式锁 ====================

    /**
     * 创建影视记录锁
     * Key: lock:create:movie:{userId}:{tmdbId}
     * Timeout: 10 秒
     */
    public static final RedisKeyDefine LOCK_CREATE_MOVIE = new RedisKeyDefine(
            "lock:create:movie:%s:%s",
            String.class,
            Duration.ofSeconds(10)
    );

    /**
     * 格式化 Key
     *
     * @param args 参数
     * @return 格式化后的 Key
     */
    public String formatKey(Object... args) {
        return String.format(keyTemplate, args);
    }
}
```

创建 `backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/redis/core/RedisUtils.java`：

```java
package com.interest.tracker.framework.redis.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * 提供常用的 Redis 操作方法
 *
 * @author interest-tracker
 */
@Component
@Slf4j
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     *
     * @param key   Key
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存（带过期时间）
     *
     * @param key     Key
     * @param value   值
     * @param timeout 过期时间
     */
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 设置缓存（带过期时间，单位：秒）
     *
     * @param key     Key
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     *
     * @param key Key
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取缓存（指定类型）
     *
     * @param key  Key
     * @param type 类型
     * @param <T>  泛型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Cannot cast " + value.getClass() + " to " + type);
    }

    /**
     * 删除缓存
     *
     * @param key Key
     * @return 是否删除成功
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 判断 Key 是否存在
     *
     * @param key Key
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key     Key
     * @param timeout 过期时间
     * @return 是否设置成功
     */
    public Boolean expire(String key, Duration timeout) {
        return redisTemplate.expire(key, timeout);
    }

    /**
     * 获取过期时间（秒）
     *
     * @param key Key
     * @return 过期时间（秒），-1 表示永不过期，-2 表示 Key 不存在
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 递增
     *
     * @param key Key
     * @return 递增后的值
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 递增（指定步长）
     *
     * @param key   Key
     * @param delta 步长
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key Key
     * @return 递减后的值
     */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 递减（指定步长）
     *
     * @param key   Key
     * @param delta 步长
     * @return 递减后的值
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 使用 RedisKeyDefine 设置缓存
     *
     * @param keyDefine Key 定义
     * @param args      Key 参数
     * @param value     值
     */
    public void set(RedisKeyDefine keyDefine, Object value, Object... args) {
        String key = keyDefine.formatKey(args);
        if (keyDefine.getTimeout() != null) {
            set(key, value, keyDefine.getTimeout());
        } else {
            set(key, value);
        }
    }

    /**
     * 使用 RedisKeyDefine 获取缓存
     *
     * @param keyDefine Key 定义
     * @param args      Key 参数
     * @param <T>       类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(RedisKeyDefine keyDefine, Object... args) {
        String key = keyDefine.formatKey(args);
        Object value = get(key);
        if (value == null) {
            return null;
        }
        Class<?> valueType = keyDefine.getValueType();
        if (valueType.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Cannot cast " + value.getClass() + " to " + valueType);
    }

    /**
     * 使用 RedisKeyDefine 删除缓存
     *
     * @param keyDefine Key 定义
     * @param args      Key 参数
     * @return 是否删除成功
     */
    public Boolean delete(RedisKeyDefine keyDefine, Object... args) {
        String key = keyDefine.formatKey(args);
        return delete(key);
    }
}
```

### 5.2 分布式锁工具类

创建 `backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/redis/core/RedisLockUtils.java`：

```java
package com.interest.tracker.framework.redis.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

/**
 * Redis 分布式锁工具类
 * 使用 SET NX EX 实现分布式锁
 *
 * @author interest-tracker
 */
@Component
@Slf4j
public class RedisLockUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) " +
            "else return 0 end";

    /**
     * 尝试获取锁
     *
     * @param key     锁的 Key
     * @param timeout 锁的超时时间
     * @return 锁的值（用于释放锁），如果获取失败返回 null
     */
    public String tryLock(String key, Duration timeout) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                lockKey, 
                lockValue, 
                timeout
        );
        
        if (Boolean.TRUE.equals(success)) {
            return lockValue;
        }
        return null;
    }

    /**
     * 释放锁
     *
     * @param key       锁的 Key
     * @param lockValue 锁的值
     * @return 是否释放成功
     */
    public boolean unlock(String key, String lockValue) {
        String lockKey = LOCK_PREFIX + key;
        
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(UNLOCK_SCRIPT);
        script.setResultType(Long.class);
        
        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(lockKey),
                lockValue
        );
        
        return result != null && result > 0;
    }

    /**
     * 使用 RedisKeyDefine 尝试获取锁
     *
     * @param keyDefine Key 定义
     * @param args      Key 参数
     * @return 锁的值，如果获取失败返回 null
     */
    public String tryLock(RedisKeyDefine keyDefine, Object... args) {
        String key = keyDefine.formatKey(args);
        Duration timeout = keyDefine.getTimeout() != null 
                ? keyDefine.getTimeout() 
                : Duration.ofSeconds(10);
        return tryLock(key, timeout);
    }
}
```

### 5.3 缓存服务示例

#### 5.3.1 Dashboard 服务集成 Redis

在 `DashboardServiceImpl` 中添加缓存：

```java
@Resource
private RedisUtils redisUtils;

@Override
public DashboardSummaryRespVO getSummary(DashboardSummaryReqVO reqVO) {
    Long userId = UserContext.getUserId();
    if (userId == null) {
        throw exception(UNAUTHORIZED);
    }

    // 构建缓存 Key
    String timeRange = reqVO != null && reqVO.getTimeRange() != null 
            ? reqVO.getTimeRange() 
            : "week";
    String cacheKey = RedisKeyDefine.DASHBOARD_SUMMARY.formatKey(userId, timeRange);

    // 尝试从缓存获取
    DashboardSummaryRespVO cached = redisUtils.get(
            RedisKeyDefine.DASHBOARD_SUMMARY,
            userId,
            timeRange
    );
    if (cached != null) {
        log.debug("从缓存获取 Dashboard 统计: userId={}, timeRange={}", userId, timeRange);
        return cached;
    }

    // 缓存未命中，查询数据库
    DashboardSummaryRespVO respVO = new DashboardSummaryRespVO();
    // ... 原有查询逻辑 ...

    // 写入缓存
    redisUtils.set(RedisKeyDefine.DASHBOARD_SUMMARY, respVO, userId, timeRange);
    log.debug("写入 Dashboard 统计缓存: userId={}, timeRange={}", userId, timeRange);

    return respVO;
}
```

#### 5.3.2 TMDB API 缓存集成

在 `MovieDataService` 中添加缓存：

```java
@Resource
private RedisUtils redisUtils;

public TMDBSearchResponse search(String keyword, Integer page) {
    // 构建缓存 Key
    String cacheKey = RedisKeyDefine.TMDB_SEARCH.formatKey(keyword, page);
    
    // 尝试从缓存获取
    TMDBSearchResponse cached = redisUtils.get(
            RedisKeyDefine.TMDB_SEARCH,
            keyword,
            page
    );
    if (cached != null) {
        log.debug("从缓存获取 TMDB 搜索结果: keyword={}, page={}", keyword, page);
        return cached;
    }

    // 缓存未命中，调用 API
    List<MovieDataProvider> providers = getProviders();
    // ... 原有调用逻辑 ...
    TMDBSearchResponse response = provider.search(keyword, page);

    // 写入缓存
    redisUtils.set(RedisKeyDefine.TMDB_SEARCH, response, keyword, page);
    log.debug("写入 TMDB 搜索结果缓存: keyword={}, page={}", keyword, page);

    return response;
}
```

#### 5.3.3 分布式锁示例

防止重复创建记录：

```java
@Resource
private RedisLockUtils redisLockUtils;

@Transactional(rollbackFor = Exception.class)
public MovieCreateRespVO createMovie(MovieCreateReqVO reqVO) {
    Long userId = UserContext.getUserId();
    Long tmdbId = reqVO.getTmdbId();
    
    // 获取分布式锁
    String lockValue = redisLockUtils.tryLock(
            RedisKeyDefine.LOCK_CREATE_MOVIE,
            userId,
            tmdbId
    );
    
    if (lockValue == null) {
        throw exception(MOVIE_CREATE_IN_PROGRESS);
    }
    
    try {
        // 检查是否已存在
        MovieDO existMovie = movieMapper.selectByTmdbId(tmdbId);
        if (existMovie != null) {
            throw exception(MOVIE_ALREADY_EXISTS);
        }
        
        // 创建记录
        // ... 创建逻辑 ...
        
    } finally {
        // 释放锁
        redisLockUtils.unlock(
                RedisKeyDefine.LOCK_CREATE_MOVIE.formatKey(userId, tmdbId),
                lockValue
        );
    }
}
```

---

## 6. 缓存策略设计

### 6.1 缓存更新策略

#### 6.1.1 Cache-Aside（旁路缓存）

**策略**：应用程序负责维护缓存

**流程**：
1. 读取：先查缓存，缓存未命中则查数据库，然后写入缓存
2. 更新：先更新数据库，再删除缓存

**适用场景**：统计数据、热点数据

#### 6.1.2 Write-Through（写透）

**策略**：同时更新缓存和数据库

**流程**：
1. 更新：先更新缓存，再更新数据库

**适用场景**：用户信息、配置信息

#### 6.1.3 Write-Behind（写回）

**策略**：先更新缓存，异步更新数据库

**适用场景**：本项目暂不使用

### 6.2 缓存失效策略

#### 6.2.1 主动失效

**场景**：数据更新时主动删除缓存

**示例**：
```java
public void updateMovieRecord(MovieRecordUpdateReqVO reqVO) {
    // 更新数据库
    movieRecordMapper.updateById(recordDO);
    
    // 删除相关缓存
    Long userId = UserContext.getUserId();
    redisUtils.delete(RedisKeyDefine.DASHBOARD_SUMMARY, userId, "week");
    redisUtils.delete(RedisKeyDefine.DASHBOARD_SUMMARY, userId, "month");
    // ... 删除其他相关缓存
}
```

#### 6.2.2 定时失效

**场景**：设置合理的过期时间，让缓存自动失效

**示例**：
- 统计数据：5-30 分钟
- API 结果：1-24 小时
- 用户信息：30 分钟

### 6.3 缓存预热

**场景**：系统启动时预加载热点数据

**实现**：创建 `CacheWarmupService`，在应用启动后执行

```java
@Component
@Slf4j
public class CacheWarmupService implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private DashboardService dashboardService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("开始缓存预热...");
        // 预热热点数据
        // ...
        log.info("缓存预热完成");
    }
}
```

---

## 7. 工具类封装

### 7.1 缓存注解支持（可选）

如果需要使用 Spring Cache 注解，可以添加配置：

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
```

使用示例：

```java
@Cacheable(value = "dashboard", key = "#userId + ':' + #timeRange")
public DashboardSummaryRespVO getSummary(Long userId, String timeRange) {
    // ...
}

@CacheEvict(value = "dashboard", key = "#userId + ':*'")
public void updateRecord(Long userId) {
    // ...
}
```

---

## 8. 最佳实践

### 8.1 Key 命名规范

1. **使用冒号分隔**：`module:type:id`
2. **使用小写字母**：避免大小写混淆
3. **避免特殊字符**：只使用字母、数字、冒号、下划线
4. **统一管理**：使用 `RedisKeyDefine` 统一管理

**示例**：
```
✅ dashboard:summary:1:week
✅ api:tmdb:search:复仇者联盟:1
❌ Dashboard:Summary:1:Week
❌ dashboard_summary_1_week
```

### 8.2 缓存时间设置

1. **统计数据**：5-30 分钟（根据更新频率）
2. **第三方 API**：1-24 小时（根据数据变化频率）
3. **用户信息**：30 分钟
4. **热点数据**：10-30 分钟

### 8.3 缓存穿透防护

**问题**：查询不存在的数据，导致大量请求打到数据库

**解决方案**：
1. 缓存空值（设置较短的过期时间）
2. 使用布隆过滤器（Bloom Filter）

**示例**：
```java
public MovieRespVO getMovie(Long id) {
    // 先查缓存
    MovieRespVO cached = redisUtils.get(RedisKeyDefine.MOVIE_DETAIL, id);
    if (cached != null) {
        // 如果是空值标记，直接返回 null
        if (cached.getId() == null) {
            return null;
        }
        return cached;
    }
    
    // 查数据库
    MovieDO movieDO = movieMapper.selectById(id);
    if (movieDO == null) {
        // 缓存空值，防止缓存穿透
        MovieRespVO empty = new MovieRespVO();
        redisUtils.set(RedisKeyDefine.MOVIE_DETAIL, empty, Duration.ofMinutes(5), id);
        return null;
    }
    
    // 缓存正常值
    MovieRespVO respVO = BeanUtils.toBean(movieDO, MovieRespVO.class);
    redisUtils.set(RedisKeyDefine.MOVIE_DETAIL, respVO, id);
    return respVO;
}
```

### 8.4 缓存雪崩防护

**问题**：大量缓存同时失效，导致请求全部打到数据库

**解决方案**：
1. 设置随机的过期时间
2. 使用多级缓存
3. 限流降级

**示例**：
```java
// 设置随机过期时间（5-10 分钟）
Duration timeout = Duration.ofMinutes(5 + new Random().nextInt(5));
redisUtils.set(key, value, timeout);
```

### 8.5 缓存击穿防护

**问题**：热点 Key 失效，大量请求同时查询数据库

**解决方案**：
1. 使用分布式锁
2. 设置热点数据永不过期，异步更新

**示例**：
```java
public DashboardSummaryRespVO getSummary(Long userId, String timeRange) {
    // 先查缓存
    DashboardSummaryRespVO cached = redisUtils.get(...);
    if (cached != null) {
        return cached;
    }
    
    // 获取分布式锁
    String lockValue = redisLockUtils.tryLock("lock:dashboard:" + userId, Duration.ofSeconds(5));
    if (lockValue == null) {
        // 获取锁失败，等待后重试
        Thread.sleep(100);
        return getSummary(userId, timeRange);
    }
    
    try {
        // 双重检查
        cached = redisUtils.get(...);
        if (cached != null) {
            return cached;
        }
        
        // 查询数据库
        DashboardSummaryRespVO result = queryFromDatabase(...);
        
        // 写入缓存
        redisUtils.set(..., result, ...);
        
        return result;
    } finally {
        redisLockUtils.unlock("lock:dashboard:" + userId, lockValue);
    }
}
```

---

## 9. 注意事项与故障处理

### 9.1 注意事项

1. **序列化问题**
   - 确保缓存的对象实现了 `Serializable`（如果使用 JDK 序列化）
   - 使用 JSON 序列化时，注意处理循环引用

2. **内存管理**
   - 设置合理的过期时间，避免内存溢出
   - 监控 Redis 内存使用情况
   - 设置最大内存限制和淘汰策略

3. **数据一致性**
   - 更新数据时，记得删除相关缓存
   - 考虑使用事务或消息队列保证一致性

4. **性能优化**
   - 避免大 Key（单个 Key 的值不要超过 1MB）
   - 避免热点 Key（使用分片或本地缓存）
   - 批量操作使用 Pipeline

### 9.2 故障处理

#### 9.2.1 Redis 连接失败

**处理方案**：
1. 配置连接超时和重试
2. 使用降级策略：Redis 不可用时直接查数据库
3. 监控 Redis 健康状态

**代码示例**：
```java
public DashboardSummaryRespVO getSummary(DashboardSummaryReqVO reqVO) {
    try {
        // 尝试从缓存获取
        DashboardSummaryRespVO cached = redisUtils.get(...);
        if (cached != null) {
            return cached;
        }
    } catch (Exception e) {
        log.warn("Redis 缓存获取失败，降级到数据库查询", e);
        // 降级：直接查数据库
    }
    
    // 查询数据库
    return queryFromDatabase(...);
}
```

#### 9.2.2 缓存数据不一致

**处理方案**：
1. 更新数据时主动删除缓存
2. 使用版本号或时间戳判断数据是否过期
3. 定期刷新缓存

### 9.3 监控指标

建议监控以下指标：

1. **Redis 连接数**：监控连接池使用情况
2. **缓存命中率**：`命中次数 / 总请求次数`
3. **缓存响应时间**：Redis 操作耗时
4. **内存使用率**：Redis 内存使用情况
5. **Key 数量**：监控 Key 数量增长

---

## 10. 实施计划

### 10.1 第一阶段：基础接入（1-2 天）

**任务清单**：
- [ ] 添加 Maven 依赖
- [ ] 配置 Redis 连接
- [ ] 创建 RedisConfig 配置类
- [ ] 创建 RedisUtils 工具类
- [ ] 创建 RedisKeyDefine 常量类
- [ ] 测试 Redis 连接

### 10.2 第二阶段：核心功能缓存（2-3 天）

**任务清单**：
- [ ] Dashboard 统计数据缓存
- [ ] TMDB API 结果缓存
- [ ] 用户信息缓存
- [ ] 热点数据缓存
- [ ] 缓存失效逻辑实现

### 10.3 第三阶段：高级功能（1-2 天）

**任务清单**：
- [ ] 分布式锁实现
- [ ] Token 黑名单功能
- [ ] 缓存穿透/雪崩/击穿防护
- [ ] 缓存预热功能

### 10.4 第四阶段：优化与测试（1-2 天）

**任务清单**：
- [ ] 性能测试
- [ ] 缓存命中率监控
- [ ] 故障降级测试
- [ ] 文档完善

---

## 11. 总结

### 11.1 技术方案总结

本方案采用 **Spring Data Redis + Lettuce** 作为 Redis 客户端，使用 **Jackson2JsonRedisSerializer** 进行序列化，提供了完整的工具类封装和最佳实践。

### 11.2 预期效果

- ✅ **性能提升**：热点数据查询速度提升 3-10 倍
- ✅ **成本降低**：减少第三方 API 调用 50% 以上
- ✅ **用户体验**：页面加载速度明显提升
- ✅ **系统稳定性**：降低数据库压力，提升系统承载能力

### 11.3 后续扩展

- Redis 集群支持
- Redis Sentinel 高可用
- 缓存预热策略优化
- 缓存监控告警

---

## 附录

### A. Redis 命令参考

常用 Redis 命令：

```bash
# 连接 Redis
redis-cli -h localhost -p 6379

# 查看所有 Key
KEYS *

# 查看 Key 类型
TYPE key

# 查看 Key 过期时间
TTL key

# 删除 Key
DEL key

# 查看内存使用
INFO memory

# 监控命令执行
MONITOR
```

### B. 配置文件模板

完整的 `application.yml` Redis 配置：

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      database: ${REDIS_DATABASE:0}
      password: ${REDIS_PASSWORD:}
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0
```

---

**文档版本**：v1.0  
**最后更新**：2024年

