# RabbitMQ 接入技术方案

## 文档信息

- **版本**：v1.0
- **创建日期**：2026年1月18日
- **适用范围**：Interest Tracker 后端系统
- **技术栈**：Spring Boot 3.0.x + RabbitMQ

---

## 目录

1. [背景与需求分析](#1-背景与需求分析)
2. [RabbitMQ 使用场景](#2-rabbitmq-使用场景)
3. [技术选型](#3-技术选型)
4. [配置方案](#4-配置方案)
5. [代码实现方案](#5-代码实现方案)
6. [消息队列设计](#6-消息队列设计)
7. [最佳实践](#7-最佳实践)
8. [注意事项与故障处理](#8-注意事项与故障处理)
9. [实施计划](#9-实施计划)

---

## 1. 背景与需求分析

### 1.1 为什么需要 RabbitMQ

当前系统存在以下问题和需求：

1. **同步处理耗时操作**
   - 照片上传后需要生成缩略图、提取 EXIF 数据
   - 批量上传时用户等待时间过长
   - 影响用户体验

2. **第三方 API 调用阻塞**
   - TMDB API 调用耗时较长
   - 用户需要等待 API 响应
   - 网络不稳定时影响用户体验

3. **AI 功能异步处理需求**
   - AI 标签生成耗时较长（几秒到几十秒）
   - AI 推荐计算需要分析大量数据
   - 不应该阻塞用户操作

4. **统计数据计算**
   - Dashboard 统计数据计算复杂
   - 可以异步预计算，提升响应速度

5. **系统解耦和扩展**
   - 需要解耦业务逻辑
   - 支持未来微服务架构
   - 支持消息通知、事件驱动

### 1.2 RabbitMQ 带来的价值

- ✅ **提升用户体验**：耗时操作异步处理，快速响应用户请求
- ✅ **系统解耦**：业务模块之间通过消息通信，降低耦合度
- ✅ **提高性能**：异步处理，提升系统吞吐量
- ✅ **增强可靠性**：消息持久化，保证消息不丢失
- ✅ **支持扩展**：支持水平扩展，提升系统承载能力
- ✅ **削峰填谷**：处理突发流量，保护系统稳定性

---

## 2. RabbitMQ 使用场景

### 2.1 异步任务处理

#### 2.1.1 照片处理任务

**场景**：照片上传后的处理

**任务内容**：
- 生成缩略图
- 提取 EXIF 数据（拍摄时间、设备信息、GPS 等）
- 图片压缩优化
- AI 内容识别（可选）

**消息队列**：`photo.process`

**消息格式**：
```json
{
  "photoId": 123,
  "userId": 1,
  "fileUrl": "https://cos.example.com/photos/xxx.jpg",
  "fileKey": "photos/2024/01/xxx.jpg",
  "originalFilename": "IMG_001.jpg",
  "mimeType": "image/jpeg"
}
```

**处理流程**：
```
用户上传照片
    ↓
保存照片记录（快速返回）
    ↓
发送消息到 photo.process 队列
    ↓
异步处理：
  - 生成缩略图
  - 提取 EXIF
  - 更新照片记录
```

#### 2.1.2 批量照片处理

**场景**：批量上传照片

**消息队列**：`photo.batch.process`

**消息格式**：
```json
{
  "batchId": "batch-20240101-001",
  "userId": 1,
  "photoIds": [123, 124, 125],
  "totalCount": 3
}
```

**处理流程**：
- 将批量任务拆分为单个任务
- 每个照片单独处理
- 处理完成后更新批量任务状态

#### 2.1.3 AI 标签生成任务

**场景**：为影视、音乐、照片生成 AI 标签

**消息队列**：`ai.tag.generate`

**消息格式**：
```json
{
  "taskId": "tag-20240101-001",
  "type": "movie",  // movie/music/photo/book
  "entityId": 123,
  "entityData": {
    "title": "复仇者联盟",
    "description": "...",
    "posterUrl": "..."
  }
}
```

**处理流程**：
```
用户创建记录
    ↓
快速返回（记录已创建）
    ↓
发送 AI 标签生成任务
    ↓
异步调用 AI API
    ↓
生成标签并更新记录
```

#### 2.1.4 AI 推荐计算任务

**场景**：计算用户推荐内容

**消息队列**：`ai.recommend.calculate`

**消息格式**：
```json
{
  "userId": 1,
  "type": "movie",  // movie/music/photo
  "trigger": "manual"  // manual/auto/scheduled
}
```

**处理流程**：
- 分析用户历史数据
- 调用 AI API 生成推荐
- 缓存推荐结果

### 2.2 事件通知

#### 2.2.1 统计数据更新事件

**场景**：数据变更后更新统计缓存

**消息队列**：`stats.update`

**消息格式**：
```json
{
  "userId": 1,
  "type": "photo",  // photo/movie/music/book/travel/concert/match
  "action": "create",  // create/update/delete
  "entityId": 123,
  "timestamp": "2024-01-01T12:00:00"
}
```

**处理流程**：
- 数据变更时发送事件
- 异步更新相关统计缓存
- 失效相关 Dashboard 缓存

#### 2.2.2 用户活动事件

**场景**：记录用户活动，用于分析

**消息队列**：`user.activity`

**消息格式**：
```json
{
  "userId": 1,
  "activityType": "photo.upload",
  "entityType": "photo",
  "entityId": 123,
  "timestamp": "2024-01-01T12:00:00",
  "metadata": {
    "fileSize": 1024000,
    "category": "风景"
  }
}
```

### 2.3 定时任务触发

#### 2.3.1 统计数据预计算

**场景**：定时预计算统计数据

**消息队列**：`stats.precompute`

**消息格式**：
```json
{
  "userId": 1,
  "timeRange": "week",  // today/week/month/year
  "triggerTime": "2024-01-01T00:00:00"
}
```

**处理流程**：
- 定时任务发送消息
- 异步计算统计数据
- 更新缓存

#### 2.3.2 数据清理任务

**场景**：定期清理过期数据

**消息队列**：`data.cleanup`

**消息格式**：
```json
{
  "type": "temp_files",  // temp_files/old_logs/expired_cache
  "retentionDays": 30
}
```

### 2.4 第三方 API 异步调用

#### 2.4.1 TMDB API 调用

**场景**：异步调用 TMDB API，避免阻塞

**消息队列**：`api.tmdb.call`

**消息格式**：
```json
{
  "requestId": "req-20240101-001",
  "type": "search",  // search/detail
  "params": {
    "keyword": "复仇者联盟",
    "page": 1
  },
  "callback": {
    "userId": 1,
    "callbackType": "movie.create"
  }
}
```

---

## 3. 技术选型

### 3.1 消息队列选型对比

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **RabbitMQ** | 1. 功能完善<br>2. 可靠性高<br>3. 管理界面友好<br>4. Spring 官方支持 | 1. 性能略低于 Kafka<br>2. 集群配置复杂 | ⭐⭐⭐⭐⭐ |
| **Apache Kafka** | 1. 高吞吐量<br>2. 适合大数据场景 | 1. 配置复杂<br>2. 功能相对简单<br>3. 不适合小规模应用 | ⭐⭐⭐ |
| **RocketMQ** | 1. 阿里开源<br>2. 性能优秀 | 1. 社区相对较小<br>2. 文档较少 | ⭐⭐⭐ |

### 3.2 推荐方案：RabbitMQ

**理由**：
1. Spring Boot 官方支持，集成简单
2. 功能完善，支持多种消息模式
3. 可靠性高，支持消息持久化、确认机制
4. 管理界面友好，便于监控和调试
5. 适合中小规模应用，性能满足需求

### 3.3 依赖版本

```xml
<!-- Spring Boot 3.0.x 已包含 Spring AMQP -->
<!-- 需要显式添加 RabbitMQ 依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

---

## 4. 配置方案

### 4.1 Maven 依赖配置

#### 4.1.1 父 pom.xml

在 `backend/pom.xml` 的 `<dependencyManagement>` 中添加：

```xml
<!-- Spring AMQP (RabbitMQ) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
    <version>${spring-boot.version}</version>
</dependency>
```

#### 4.1.2 子模块 pom.xml

在 `backend/interest-tracker-server/pom.xml` 的 `<dependencies>` 中添加：

```xml
<!-- Spring AMQP (RabbitMQ) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### 4.2 配置文件

#### 4.2.1 application.yml

在 `backend/interest-tracker-server/src/main/resources/application.yml` 中添加：

```yaml
# RabbitMQ 配置
spring:
  rabbitmq:
    # RabbitMQ 服务器地址（默认 localhost）
    host: ${RABBITMQ_HOST:localhost}
    # RabbitMQ 服务器端口（默认 5672）
    port: ${RABBITMQ_PORT:5672}
    # 用户名（默认 guest）
    username: ${RABBITMQ_USERNAME:guest}
    # 密码（默认 guest）
    password: ${RABBITMQ_PASSWORD:guest}
    # 虚拟主机（默认 /）
    virtual-host: ${RABBITMQ_VIRTUAL_HOST:/}
    # 连接超时时间（毫秒）
    connection-timeout: 30000
    # 发布确认（生产者确认消息是否到达 Broker）
    publisher-confirm-type: correlated
    # 发布返回（生产者确认消息是否路由到队列）
    publisher-returns: true
    # 消费者确认模式
    listener:
      simple:
        # 手动确认（推荐）
        acknowledge-mode: manual
        # 并发消费者数量
        concurrency: 5
        # 最大并发消费者数量
        max-concurrency: 10
        # 预取数量（每个消费者预取的消息数）
        prefetch: 10
        # 重试配置
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          multiplier: 2
```

#### 4.2.2 application-dev.yml（开发环境）

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
```

#### 4.2.3 application-prod.yml（生产环境，可选）

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}
    virtual-host: ${RABBITMQ_VIRTUAL_HOST:/}
    connection-timeout: 60000
    listener:
      simple:
        concurrency: 10
        max-concurrency: 20
        prefetch: 20
```

### 4.3 RabbitMQ 配置类

创建 `backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/rabbitmq/config/RabbitMQConfig.java`：

```java
package com.interest.tracker.framework.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 *
 * @author interest-tracker
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    // ==================== 交换机定义 ====================

    /**
     * 照片处理交换机（Direct）
     */
    public static final String EXCHANGE_PHOTO = "exchange.photo";

    /**
     * AI 处理交换机（Direct）
     */
    public static final String EXCHANGE_AI = "exchange.ai";

    /**
     * 统计数据交换机（Direct）
     */
    public static final String EXCHANGE_STATS = "exchange.stats";

    /**
     * 事件通知交换机（Topic）
     */
    public static final String EXCHANGE_EVENTS = "exchange.events";

    // ==================== 队列定义 ====================

    /**
     * 照片处理队列
     */
    public static final String QUEUE_PHOTO_PROCESS = "queue.photo.process";

    /**
     * 批量照片处理队列
     */
    public static final String QUEUE_PHOTO_BATCH_PROCESS = "queue.photo.batch.process";

    /**
     * AI 标签生成队列
     */
    public static final String QUEUE_AI_TAG_GENERATE = "queue.ai.tag.generate";

    /**
     * AI 推荐计算队列
     */
    public static final String QUEUE_AI_RECOMMEND_CALCULATE = "queue.ai.recommend.calculate";

    /**
     * 统计数据更新队列
     */
    public static final String QUEUE_STATS_UPDATE = "queue.stats.update";

    /**
     * 统计数据预计算队列
     */
    public static final String QUEUE_STATS_PRECOMPUTE = "queue.stats.precompute";

    /**
     * 用户活动队列
     */
    public static final String QUEUE_USER_ACTIVITY = "queue.user.activity";

    // ==================== 路由键定义 ====================

    /**
     * 照片处理路由键
     */
    public static final String ROUTING_KEY_PHOTO_PROCESS = "photo.process";

    /**
     * 批量照片处理路由键
     */
    public static final String ROUTING_KEY_PHOTO_BATCH_PROCESS = "photo.batch.process";

    /**
     * AI 标签生成路由键
     */
    public static final String ROUTING_KEY_AI_TAG_GENERATE = "ai.tag.generate";

    /**
     * AI 推荐计算路由键
     */
    public static final String ROUTING_KEY_AI_RECOMMEND_CALCULATE = "ai.recommend.calculate";

    /**
     * 统计数据更新路由键
     */
    public static final String ROUTING_KEY_STATS_UPDATE = "stats.update";

    /**
     * 统计数据预计算路由键
     */
    public static final String ROUTING_KEY_STATS_PRECOMPUTE = "stats.precompute";

    /**
     * 用户活动路由键
     */
    public static final String ROUTING_KEY_USER_ACTIVITY = "user.activity";

    // ==================== 消息转换器 ====================

    /**
     * JSON 消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ==================== 交换机配置 ====================

    /**
     * 照片处理交换机（Direct）
     */
    @Bean
    public DirectExchange photoExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_PHOTO)
                .durable(true)
                .build();
    }

    /**
     * AI 处理交换机（Direct）
     */
    @Bean
    public DirectExchange aiExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_AI)
                .durable(true)
                .build();
    }

    /**
     * 统计数据交换机（Direct）
     */
    @Bean
    public DirectExchange statsExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_STATS)
                .durable(true)
                .build();
    }

    /**
     * 事件通知交换机（Topic）
     */
    @Bean
    public TopicExchange eventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_EVENTS)
                .durable(true)
                .build();
    }

    // ==================== 队列配置 ====================

    /**
     * 照片处理队列
     */
    @Bean
    public Queue photoProcessQueue() {
        return QueueBuilder.durable(QUEUE_PHOTO_PROCESS)
                .build();
    }

    /**
     * 批量照片处理队列
     */
    @Bean
    public Queue photoBatchProcessQueue() {
        return QueueBuilder.durable(QUEUE_PHOTO_BATCH_PROCESS)
                .build();
    }

    /**
     * AI 标签生成队列
     */
    @Bean
    public Queue aiTagGenerateQueue() {
        return QueueBuilder.durable(QUEUE_AI_TAG_GENERATE)
                .build();
    }

    /**
     * AI 推荐计算队列
     */
    @Bean
    public Queue aiRecommendCalculateQueue() {
        return QueueBuilder.durable(QUEUE_AI_RECOMMEND_CALCULATE)
                .build();
    }

    /**
     * 统计数据更新队列
     */
    @Bean
    public Queue statsUpdateQueue() {
        return QueueBuilder.durable(QUEUE_STATS_UPDATE)
                .build();
    }

    /**
     * 统计数据预计算队列
     */
    @Bean
    public Queue statsPrecomputeQueue() {
        return QueueBuilder.durable(QUEUE_STATS_PRECOMPUTE)
                .build();
    }

    /**
     * 用户活动队列
     */
    @Bean
    public Queue userActivityQueue() {
        return QueueBuilder.durable(QUEUE_USER_ACTIVITY)
                .build();
    }

    // ==================== 绑定配置 ====================

    /**
     * 照片处理队列绑定
     */
    @Bean
    public Binding photoProcessBinding() {
        return BindingBuilder.bind(photoProcessQueue())
                .to(photoExchange())
                .with(ROUTING_KEY_PHOTO_PROCESS);
    }

    /**
     * 批量照片处理队列绑定
     */
    @Bean
    public Binding photoBatchProcessBinding() {
        return BindingBuilder.bind(photoBatchProcessQueue())
                .to(photoExchange())
                .with(ROUTING_KEY_PHOTO_BATCH_PROCESS);
    }

    /**
     * AI 标签生成队列绑定
     */
    @Bean
    public Binding aiTagGenerateBinding() {
        return BindingBuilder.bind(aiTagGenerateQueue())
                .to(aiExchange())
                .with(ROUTING_KEY_AI_TAG_GENERATE);
    }

    /**
     * AI 推荐计算队列绑定
     */
    @Bean
    public Binding aiRecommendCalculateBinding() {
        return BindingBuilder.bind(aiRecommendCalculateQueue())
                .to(aiExchange())
                .with(ROUTING_KEY_AI_RECOMMEND_CALCULATE);
    }

    /**
     * 统计数据更新队列绑定
     */
    @Bean
    public Binding statsUpdateBinding() {
        return BindingBuilder.bind(statsUpdateQueue())
                .to(statsExchange())
                .with(ROUTING_KEY_STATS_UPDATE);
    }

    /**
     * 统计数据预计算队列绑定
     */
    @Bean
    public Binding statsPrecomputeBinding() {
        return BindingBuilder.bind(statsPrecomputeQueue())
                .to(statsExchange())
                .with(ROUTING_KEY_STATS_PRECOMPUTE);
    }

    /**
     * 用户活动队列绑定
     */
    @Bean
    public Binding userActivityBinding() {
        return BindingBuilder.bind(userActivityQueue())
                .to(eventsExchange())
                .with(ROUTING_KEY_USER_ACTIVITY);
    }

    // ==================== RabbitTemplate 配置 ====================

    /**
     * 配置 RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        
        // 设置发布确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("消息发送成功: {}", correlationData);
            } else {
                log.error("消息发送失败: {}, 原因: {}", correlationData, cause);
            }
        });
        
        // 设置返回回调
        template.setReturnsCallback(returned -> {
            log.error("消息路由失败: 消息={}, 回复码={}, 回复文本={}, 交换机={}, 路由键={}",
                    returned.getMessage(),
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey());
        });
        
        return template;
    }
}
```

---

## 5. 代码实现方案

### 5.1 消息发送工具类

创建 `backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/rabbitmq/core/RabbitMQProducer.java`：

```java
package com.interest.tracker.framework.rabbitmq.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import static com.interest.tracker.framework.rabbitmq.config.RabbitMQConfig.*;

/**
 * RabbitMQ 消息生产者
 *
 * @author interest-tracker
 */
@Component
@Slf4j
public class RabbitMQProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送照片处理消息
     *
     * @param message 消息内容
     */
    public void sendPhotoProcessMessage(Object message) {
        rabbitTemplate.convertAndSend(EXCHANGE_PHOTO, ROUTING_KEY_PHOTO_PROCESS, message);
        log.debug("发送照片处理消息: {}", message);
    }

    /**
     * 发送批量照片处理消息
     *
     * @param message 消息内容
     */
    public void sendPhotoBatchProcessMessage(Object message) {
        rabbitTemplate.convertAndSend(EXCHANGE_PHOTO, ROUTING_KEY_PHOTO_BATCH_PROCESS, message);
        log.debug("发送批量照片处理消息: {}", message);
    }

    /**
     * 发送 AI 标签生成消息
     *
     * @param message 消息内容
     */
    public void sendAITagGenerateMessage(Object message) {
        rabbitTemplate.convertAndSend(EXCHANGE_AI, ROUTING_KEY_AI_TAG_GENERATE, message);
        log.debug("发送 AI 标签生成消息: {}", message);
    }

    /**
     * 发送 AI 推荐计算消息
     *
     * @param message 消息内容
     */
    public void sendAIRecommendCalculateMessage(Object message) {
        rabbitTemplate.convertAndSend(EXCHANGE_AI, ROUTING_KEY_AI_RECOMMEND_CALCULATE, message);
        log.debug("发送 AI 推荐计算消息: {}", message);
    }

    /**
     * 发送统计数据更新消息
     *
     * @param message 消息内容
     */
    public void sendStatsUpdateMessage(Object message) {
        rabbitTemplate.convertAndSend(EXCHANGE_STATS, ROUTING_KEY_STATS_UPDATE, message);
        log.debug("发送统计数据更新消息: {}", message);
    }

    /**
     * 发送统计数据预计算消息
     *
     * @param message 消息内容
     */
    public void sendStatsPrecomputeMessage(Object message) {
        rabbitTemplate.convertAndSend(EXCHANGE_STATS, ROUTING_KEY_STATS_PRECOMPUTE, message);
        log.debug("发送统计数据预计算消息: {}", message);
    }

    /**
     * 发送用户活动消息
     *
     * @param message 消息内容
     */
    public void sendUserActivityMessage(Object message) {
        rabbitTemplate.convertAndSend(EXCHANGE_EVENTS, ROUTING_KEY_USER_ACTIVITY, message);
        log.debug("发送用户活动消息: {}", message);
    }

    /**
     * 通用发送消息方法
     *
     * @param exchange   交换机
     * @param routingKey 路由键
     * @param message    消息内容
     */
    public void sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.debug("发送消息: exchange={}, routingKey={}, message={}", exchange, routingKey, message);
    }
}
```

### 5.2 消息 DTO 定义

创建 `backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/rabbitmq/core/dto/PhotoProcessMessage.java`：

```java
package com.interest.tracker.framework.rabbitmq.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 照片处理消息
 *
 * @author interest-tracker
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoProcessMessage implements Serializable {

    /**
     * 照片ID
     */
    private Long photoId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件Key
     */
    private String fileKey;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * MIME类型
     */
    private String mimeType;
}
```

创建其他消息 DTO（类似结构）：

```java
// AI 标签生成消息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AITagGenerateMessage implements Serializable {
    private String taskId;
    private String type;  // movie/music/photo/book
    private Long entityId;
    private Map<String, Object> entityData;
}

// 统计数据更新消息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsUpdateMessage implements Serializable {
    private Long userId;
    private String type;  // photo/movie/music/book/travel/concert/match
    private String action;  // create/update/delete
    private Long entityId;
    private String timestamp;
}
```

### 5.3 消息消费者示例

创建 `backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/rabbitmq/consumer/PhotoProcessConsumer.java`：

```java
package com.interest.tracker.framework.rabbitmq.consumer;

import com.interest.tracker.framework.rabbitmq.core.dto.PhotoProcessMessage;
import com.interest.tracker.module.photo.service.PhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import static com.interest.tracker.framework.rabbitmq.config.RabbitMQConfig.QUEUE_PHOTO_PROCESS;

/**
 * 照片处理消费者
 *
 * @author interest-tracker
 */
@Component
@Slf4j
public class PhotoProcessConsumer {

    @Resource
    private PhotoService photoService;

    /**
     * 处理照片处理消息
     *
     * @param message 消息内容
     */
    @RabbitListener(queues = QUEUE_PHOTO_PROCESS)
    public void handlePhotoProcess(PhotoProcessMessage message) {
        log.info("收到照片处理消息: photoId={}, userId={}", message.getPhotoId(), message.getUserId());
        
        try {
            // 1. 生成缩略图
            photoService.generateThumbnail(message.getPhotoId());
            
            // 2. 提取 EXIF 数据
            photoService.extractExifData(message.getPhotoId());
            
            // 3. AI 内容识别（可选）
            // photoService.analyzePhotoContent(message.getPhotoId());
            
            log.info("照片处理完成: photoId={}", message.getPhotoId());
        } catch (Exception e) {
            log.error("照片处理失败: photoId={}, error={}", message.getPhotoId(), e.getMessage(), e);
            // 可以发送到死信队列或重试队列
            throw e;  // 抛出异常会触发重试
        }
    }
}
```

### 5.4 服务层集成示例

#### 5.4.1 PhotoService 集成

在 `PhotoServiceImpl` 中集成消息发送：

```java
@Resource
private RabbitMQProducer rabbitMQProducer;

@Override
@Transactional(rollbackFor = Exception.class)
public PhotoUploadRespVO uploadPhoto(MultipartFile file, PhotoUploadReqVO reqVO) {
    Long userId = UserContext.getUserId();
    if (userId == null) {
        throw exception(UNAUTHORIZED);
    }

    // 1. 文件校验
    validateFile(file);

    // 2. 上传原图到COS
    String fileKey = tencentCosService.generateFileKey(userId, file.getOriginalFilename());
    String fileUrl = tencentCosService.uploadFile(...);

    // 3. 创建照片记录（快速返回）
    PhotoDO photoDO = BeanUtils.toBean(reqVO, PhotoDO.class);
    photoDO.setUserId(userId);
    photoDO.setFilePath(fileUrl);
    photoDO.setThumbnailPath(null);  // 缩略图稍后生成
    photoMapper.insert(photoDO);

    // 4. 发送异步处理消息
    PhotoProcessMessage message = new PhotoProcessMessage();
    message.setPhotoId(photoDO.getId());
    message.setUserId(userId);
    message.setFileUrl(fileUrl);
    message.setFileKey(fileKey);
    message.setOriginalFilename(file.getOriginalFilename());
    message.setMimeType(file.getContentType());
    
    rabbitMQProducer.sendPhotoProcessMessage(message);
    log.info("照片上传成功，已发送处理消息: photoId={}", photoDO.getId());

    // 5. 返回结果（不等待处理完成）
    PhotoUploadRespVO respVO = BeanUtils.toBean(photoDO, PhotoUploadRespVO.class);
    return respVO;
}
```

#### 5.4.2 DashboardService 集成

在 `DashboardServiceImpl` 中集成事件发送：

```java
@Resource
private RabbitMQProducer rabbitMQProducer;

public void updateMovieRecord(MovieRecordUpdateReqVO reqVO) {
    // 更新数据库
    movieRecordMapper.updateById(recordDO);
    
    // 发送统计数据更新事件
    StatsUpdateMessage message = new StatsUpdateMessage();
    message.setUserId(UserContext.getUserId());
    message.setType("movie");
    message.setAction("update");
    message.setEntityId(reqVO.getId());
    message.setTimestamp(LocalDateTime.now().toString());
    
    rabbitMQProducer.sendStatsUpdateMessage(message);
}
```

---

## 6. 消息队列设计

### 6.1 交换机类型选择

| 交换机类型 | 使用场景 | 示例 |
|-----------|---------|------|
| **Direct** | 精确匹配路由键 | 照片处理、AI处理、统计计算 |
| **Topic** | 模式匹配路由键 | 事件通知、用户活动 |
| **Fanout** | 广播消息 | 系统通知（暂不使用） |
| **Headers** | 基于消息头匹配 | 特殊场景（暂不使用） |

### 6.2 消息持久化

**配置**：
- 交换机：`durable = true`
- 队列：`durable = true`
- 消息：`deliveryMode = PERSISTENT`

**代码示例**：
```java
@Bean
public Queue photoProcessQueue() {
    return QueueBuilder.durable(QUEUE_PHOTO_PROCESS)
            .build();
}

// 发送消息时设置持久化
MessageProperties properties = new MessageProperties();
properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
Message message = new Message(messageBody.getBytes(), properties);
rabbitTemplate.send(exchange, routingKey, message);
```

### 6.3 消息确认机制

#### 6.3.1 生产者确认

**配置**：
```yaml
spring:
  rabbitmq:
    publisher-confirm-type: correlated
    publisher-returns: true
```

**代码示例**：
```java
// 已在 RabbitMQConfig 中配置确认回调
template.setConfirmCallback((correlationData, ack, cause) -> {
    if (ack) {
        log.debug("消息发送成功");
    } else {
        log.error("消息发送失败: {}", cause);
    }
});
```

#### 6.3.2 消费者确认

**配置**：
```yaml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: manual  # 手动确认
```

**代码示例**：
```java
@RabbitListener(queues = QUEUE_PHOTO_PROCESS)
public void handlePhotoProcess(PhotoProcessMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
    try {
        // 处理消息
        processMessage(message);
        
        // 手动确认
        channel.basicAck(tag, false);
    } catch (Exception e) {
        log.error("消息处理失败", e);
        // 拒绝消息，不重新入队
        channel.basicNack(tag, false, false);
    }
}
```

### 6.4 死信队列

**用途**：处理失败的消息

**配置**：
```java
@Bean
public Queue photoProcessDlq() {
    return QueueBuilder.durable(QUEUE_PHOTO_PROCESS_DLQ)
            .build();
}

@Bean
public Queue photoProcessQueue() {
    return QueueBuilder.durable(QUEUE_PHOTO_PROCESS)
            .withArgument("x-dead-letter-exchange", EXCHANGE_DLQ)
            .withArgument("x-dead-letter-routing-key", ROUTING_KEY_PHOTO_PROCESS_DLQ)
            .build();
}
```

---

## 7. 最佳实践

### 7.1 消息设计原则

1. **消息要小**：避免大消息，影响性能
2. **消息要幂等**：消费者要支持重复处理
3. **消息要可追溯**：包含必要的上下文信息
4. **消息要版本化**：支持消息格式升级

### 7.2 消费者设计原则

1. **幂等性**：确保重复处理不会产生副作用
2. **异常处理**：捕获异常，记录日志，决定是否重试
3. **性能优化**：批量处理、异步处理
4. **监控告警**：监控消费速度、失败率

### 7.3 消息发送最佳实践

```java
// ✅ 推荐：使用消息对象
PhotoProcessMessage message = new PhotoProcessMessage();
message.setPhotoId(photoId);
rabbitMQProducer.sendPhotoProcessMessage(message);

// ❌ 不推荐：直接发送字符串
rabbitMQProducer.sendPhotoProcessMessage("photoId:123");
```

### 7.4 消息消费最佳实践

```java
@RabbitListener(queues = QUEUE_PHOTO_PROCESS)
public void handlePhotoProcess(PhotoProcessMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
    try {
        // 1. 参数校验
        if (message.getPhotoId() == null) {
            log.warn("消息参数无效: {}", message);
            channel.basicAck(tag, false);
            return;
        }
        
        // 2. 幂等性检查
        if (isAlreadyProcessed(message.getPhotoId())) {
            log.info("照片已处理，跳过: photoId={}", message.getPhotoId());
            channel.basicAck(tag, false);
            return;
        }
        
        // 3. 处理消息
        processMessage(message);
        
        // 4. 标记已处理
        markAsProcessed(message.getPhotoId());
        
        // 5. 确认消息
        channel.basicAck(tag, false);
        
    } catch (Exception e) {
        log.error("消息处理失败: photoId={}", message.getPhotoId(), e);
        
        // 根据异常类型决定是否重试
        if (shouldRetry(e)) {
            // 拒绝消息，重新入队
            channel.basicNack(tag, false, true);
        } else {
            // 拒绝消息，不重新入队（发送到死信队列）
            channel.basicNack(tag, false, false);
        }
    }
}
```

---

## 8. 注意事项与故障处理

### 8.1 注意事项

1. **消息顺序**：RabbitMQ 不保证消息顺序，需要顺序处理的场景要特殊处理
2. **消息丢失**：确保消息持久化，使用确认机制
3. **重复消费**：消费者要支持幂等性
4. **性能监控**：监控队列长度、消费速度、失败率

### 8.2 故障处理

#### 8.2.1 RabbitMQ 连接失败

**处理方案**：
1. 配置连接重试
2. 使用降级策略：RabbitMQ 不可用时同步处理
3. 监控 RabbitMQ 健康状态

**代码示例**：
```java
public void uploadPhoto(...) {
    try {
        // 尝试发送消息
        rabbitMQProducer.sendPhotoProcessMessage(message);
    } catch (Exception e) {
        log.warn("RabbitMQ 不可用，降级到同步处理", e);
        // 降级：同步处理
        photoService.processPhotoSync(photoId);
    }
}
```

#### 8.2.2 消息处理失败

**处理方案**：
1. 重试机制（配置重试次数和间隔）
2. 死信队列（记录失败消息）
3. 告警通知（失败率过高时告警）

### 8.3 监控指标

建议监控以下指标：

1. **队列长度**：监控队列积压情况
2. **消费速度**：监控消息处理速度
3. **失败率**：监控消息处理失败率
4. **延迟时间**：监控消息从发送到处理完成的时间
5. **连接状态**：监控 RabbitMQ 连接状态

---

## 9. 实施计划

### 9.1 第一阶段：基础接入（2-3 天）

**任务清单**：
- [ ] 添加 Maven 依赖
- [ ] 配置 RabbitMQ 连接
- [ ] 创建 RabbitMQConfig 配置类
- [ ] 创建 RabbitMQProducer 工具类
- [ ] 测试 RabbitMQ 连接和消息发送

### 9.2 第二阶段：核心功能集成（3-5 天）

**任务清单**：
- [ ] 照片处理异步化
- [ ] AI 标签生成异步化
- [ ] 统计数据更新事件
- [ ] 消息消费者实现
- [ ] 异常处理和重试机制

### 9.3 第三阶段：高级功能（2-3 天）

**任务清单**：
- [ ] 死信队列配置
- [ ] 消息监控和告警
- [ ] 性能优化
- [ ] 文档完善

### 9.4 第四阶段：测试与优化（2-3 天）

**任务清单**：
- [ ] 功能测试
- [ ] 性能测试
- [ ] 故障测试
- [ ] 监控配置

---

## 10. 总结

### 10.1 技术方案总结

本方案采用 **Spring AMQP + RabbitMQ** 作为消息队列解决方案，提供了完整的配置、工具类和最佳实践。

### 10.2 预期效果

- ✅ **用户体验提升**：耗时操作异步处理，响应速度提升 5-10 倍
- ✅ **系统解耦**：业务模块通过消息通信，降低耦合度
- ✅ **性能提升**：异步处理，系统吞吐量提升 2-3 倍
- ✅ **可靠性增强**：消息持久化，保证消息不丢失

### 10.3 后续扩展

- RabbitMQ 集群支持
- 消息追踪和监控
- 消息路由优化
- 批量消息处理优化

---

## 附录

### A. RabbitMQ 管理界面

访问地址：`http://localhost:15672`

默认账号：
- 用户名：`guest`
- 密码：`guest`

### B. 常用命令

```bash
# 启动 RabbitMQ（Docker）
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management

# 查看队列
rabbitmqctl list_queues

# 查看交换机
rabbitmqctl list_exchanges

# 查看绑定
rabbitmqctl list_bindings
```

### C. 配置文件模板

完整的 `application.yml` RabbitMQ 配置：

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}
    virtual-host: ${RABBITMQ_VIRTUAL_HOST:/}
    connection-timeout: 30000
    publisher-confirm-type: correlated
    publisher-returns: true
    listener:
      simple:
        acknowledge-mode: manual
        concurrency: 5
        max-concurrency: 10
        prefetch: 10
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          multiplier: 2
```

---

**文档版本**：v1.0  
**最后更新**：2024年

