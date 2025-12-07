# Interest Tracker - 个人兴趣内容管理与分析平台

## 项目简介

一个整合摄影、影视、音乐、球赛四大兴趣领域的个人内容管理与数据分析平台，通过数据可视化帮助用户了解自己的兴趣习惯，发现更多相关内容。

## 技术栈

### 后端
- **Java 22** - 开发语言
- **Spring Boot 3.3.0** - 核心框架
- **MyBatis-Plus 3.5.5** - ORM框架
- **MySQL 8.0** - 数据库
- **Swagger** - API文档

### 前端（待开发）
- Vue 3
- Element Plus / Ant Design Vue
- ECharts

## 项目结构

```
interest-tracker/
├── interest-tracker-api/          # API接口模块
│   └── src/main/java/com/interest/tracker/
│       ├── framework/common/exception/   # 错误码基础类
│       └── module/interest/constants/    # 业务错误码
│
├── interest-tracker-server/       # 服务实现模块
│   └── src/main/java/com/interest/tracker/
│       ├── framework/                     # 框架层
│       │   ├── common/                    # 通用类
│       │   │   ├── pojo/                  # 通用对象（CommonResult、PageResult等）
│       │   │   ├── exception/             # 异常处理
│       │   │   └── util/                  # 工具类
│       │   ├── mybatis/                   # MyBatis配置
│       │   └── web/                       # Web相关
│       └── module/                        # 业务模块（待开发）
│
└── docs/                          # 文档目录
    ├── database/                  # 数据库脚本
    └── *.md                       # 架构设计文档
```

## 核心功能

### 已实现
- ✅ Maven多模块项目结构
- ✅ 通用返回数据结构（CommonResult）
- ✅ 统一异常处理
- ✅ 分页参数和结果封装
- ✅ MyBatis-Plus基础配置
- ✅ 数据库表设计

### 待开发
- ⏳ 用户管理模块
- ⏳ 摄影作品管理
- ⏳ 影视剧追踪
- ⏳ 音乐管理
- ⏳ 球赛追踪
- ⏳ 数据可视化分析

## 数据库设计

数据库表包括：

- **用户表**：sys_user
- **摄影相关**：photo、photo_album、photo_album_photo
- **影视相关**：movie、movie_record
- **音乐相关**：song、playlist、playlist_song、play_record
- **球赛相关**：team、user_team、match_record
- **通用**：tag

详细表结构请查看 `docs/database/init.sql`

## 快速开始

### 前置要求
- JDK 22+
- Maven 3.6+
- MySQL 8.0+

### 初始化数据库

1. 创建数据库：
```sql
CREATE DATABASE interest_tracker DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p interest_tracker < docs/database/init.sql
```

### 配置应用

修改 `interest-tracker-server/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/interest_tracker?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 运行应用

```bash
cd interest-tracker-server
mvn spring-boot:run
```

### 访问API文档

启动后访问：http://localhost:8080/swagger-ui.html

## 开发规范

项目遵循严格的代码规范，详细请参考：

- [通用架构设计与代码结构规范](docs/通用架构设计与代码结构规范.md)
- [CRUD开发流程规范](docs/CRUD开发流程规范.md)

## 目录说明

### API模块（interest-tracker-api）
- 定义对外暴露的服务契约
- 错误码常量定义
- 枚举定义

### Server模块（interest-tracker-server）
- Controller层：处理HTTP请求
- Service层：业务逻辑实现
- DAL层：数据访问
- Framework层：框架配置和通用类

## 核心类说明

### CommonResult
统一返回结果封装，所有接口统一使用此格式返回。

### PageParam / PageResult
分页参数和结果封装。

### BaseDO
所有DO对象的基类，包含公共字段（createTime、updateTime、creator、updater、deleted）。

### BaseMapperX
MyBatis-Plus Mapper的扩展，提供更便捷的查询方法。

### GlobalExceptionHandler
全局异常处理器，统一处理所有异常并返回标准格式。

## 后续开发计划

1. 完成用户认证和授权模块
2. 实现四大兴趣领域的CRUD功能
3. 开发数据统计和分析功能
4. 实现数据可视化接口
5. 前端界面开发

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎提出Issue。

