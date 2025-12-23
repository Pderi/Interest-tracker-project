# Interest Tracker - 个人兴趣内容管理与分析平台

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.10-brightgreen.svg)
![Vue](https://img.shields.io/badge/Vue-3.4.21-4fc08d.svg)
![TypeScript](https://img.shields.io/badge/TypeScript-5.4.2-blue.svg)

一个整合摄影、影视、音乐、球赛四大兴趣领域的个人内容管理与数据分析平台

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [项目结构](#-项目结构) • [技术栈](#-技术栈)

</div>

---

## 📖 项目简介

**Interest Tracker** 是一个现代化的个人兴趣内容管理与分析平台，帮助用户统一管理摄影、影视、音乐、球赛等兴趣领域的记录，通过数据可视化分析用户的兴趣习惯，并提供智能推荐功能。

### 核心价值

- 🎯 **统一管理**：四大兴趣领域的统一管理平台，告别数据分散
- 📊 **数据分析**：深度分析用户兴趣习惯和趋势，生成个性化报告
- 🤖 **智能推荐**：基于 AI 和数据的个性化内容推荐
- 🎨 **美观界面**：深色主题、渐变配色、现代化 UI 设计
- 📱 **响应式设计**：完美适配桌面端和移动端

---

## ✨ 功能特性

### 已实现功能

#### 🎬 影视模块
- ✅ TMDB 影视搜索与信息获取
- ✅ 观看记录管理（想看/在看/已看/弃剧）
- ✅ 个人评分与评价
- ✅ 观看进度追踪
- ✅ 标签分类管理

#### 🎵 音乐模块
- ✅ 专辑记录管理
- ✅ 听歌状态追踪（想听/在听/已听/弃听）
- ✅ 个人评分与评价
- ✅ 音乐类型分类
- ✅ 听歌次数统计

#### 📸 摄影模块
- ✅ 照片上传与管理
- ✅ EXIF 数据提取
- ✅ 照片预览与展示
- ✅ 相册分类

#### ⚽ 其他模块
- ✅ 旅游记录管理（含照片上传）
- ✅ 演唱会记录管理（含照片上传）
- ✅ 图书记录管理
- ✅ 时间线统一展示

#### 👤 用户系统
- ✅ 用户注册与登录
- ✅ JWT 认证
- ✅ 个人中心

#### 🎨 前端特性
- ✅ 深色/浅色主题切换
- ✅ 响应式布局
- ✅ 现代化 UI 设计
- ✅ 流畅动画效果

### 待开发功能

- ⏳ 数据统计与可视化分析
- ⏳ AI 智能标签生成
- ⏳ 照片质量评估
- ⏳ 个性化推荐系统
- ⏳ 年度报告生成

---

## 🛠 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.0.10 | 核心框架 |
| MyBatis-Plus | 3.5.3.2 | ORM 框架 |
| MySQL | 8.0+ | 数据库 |
| Spring Security | 6.3.0 | 安全框架 |
| JWT | 4.4.0 | 认证方案 |
| Swagger | 2.3.0 | API 文档 |
| Lombok | 1.18.32 | 代码简化 |
| Hutool | 5.8.23 | 工具类库 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.21 | 前端框架 |
| TypeScript | 5.4.2 | 类型系统 |
| Vite | 5.1.6 | 构建工具 |
| Element Plus | 2.6.0 | UI 组件库 |
| Pinia | 2.1.7 | 状态管理 |
| Vue Router | 4.3.0 | 路由管理 |
| Axios | 1.6.7 | HTTP 客户端 |
| Tailwind CSS | 3.4.1 | CSS 框架 |
| Day.js | 1.11.10 | 日期处理 |

---

## 📁 项目结构

```
interest-tracker-project/
├── backend/                          # 后端项目
│   ├── interest-tracker-api/        # API 接口模块
│   │   └── src/main/java/com/interest/tracker/
│   │       ├── framework/common/exception/    # 错误码基础类
│   │       └── module/                        # 业务模块常量
│   │
│   ├── interest-tracker-server/      # 服务实现模块
│   │   └── src/main/java/com/interest/tracker/
│   │       ├── framework/                    # 框架层
│   │       │   ├── common/                  # 通用类
│   │       │   ├── mybatis/                # MyBatis 配置
│   │       │   ├── security/               # 安全配置
│   │       │   └── web/                    # Web 配置
│   │       └── module/                     # 业务模块
│   │           ├── movie/                  # 影视模块
│   │           ├── music/                  # 音乐模块
│   │           └── user/                   # 用户模块
│   │
│   ├── database/                      # 数据库脚本
│   │   └── init.sql                   # 初始化 SQL
│   │
│   ├── docs/                          # 后端文档
│   │   ├── 通用架构设计与代码结构规范.md
│   │   ├── CRUD开发流程规范.md
│   │   └── ...
│   │
│   └── pom.xml                        # Maven 父 POM
│
├── frontend/                          # 前端项目
│   ├── src/
│   │   ├── api/                       # API 接口封装
│   │   ├── components/                # 公共组件
│   │   │   ├── layout/                # 布局组件
│   │   │   └── ...
│   │   ├── layouts/                   # 布局页面
│   │   ├── router/                    # 路由配置
│   │   ├── store/                     # Pinia 状态管理
│   │   ├── styles/                    # 全局样式
│   │   ├── types/                     # TypeScript 类型
│   │   ├── utils/                     # 工具函数
│   │   └── views/                     # 页面组件
│   │       ├── Movie/                 # 影视页面
│   │       ├── Music/                 # 音乐页面
│   │       ├── Photo/                 # 摄影页面
│   │       └── ...
│   │
│   ├── docs/                          # 前端文档
│   └── package.json                   # 项目配置
│
├── api-doc/                           # API 接口文档
│   ├── 用户认证与鉴权接口文档.md
│   ├── 影视模块-App接口文档.md
│   └── 音乐模块-App接口文档.md
│
├── requirements/                      # 需求文档
│   ├── 01-需求调研文档.md
│   ├── 02-需求分析文档.md
│   └── ...
│
└── README.md                          # 项目说明文档
```

---

## 🚀 快速开始

### 前置要求

- **JDK 17+**
- **Maven 3.6+**
- **MySQL 8.0+**
- **Node.js 18+**
- **npm/yarn/pnpm**

### 1. 克隆项目

```bash
git clone <repository-url>
cd interest-tracker-project
```

### 2. 数据库初始化

#### 创建数据库

```sql
CREATE DATABASE interest_tracker DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 执行初始化脚本

```bash
mysql -u root -p interest_tracker < backend/database/init.sql
```

### 3. 后端配置与启动

#### 修改配置文件

编辑 `backend/interest-tracker-server/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/interest_tracker?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

#### 启动后端服务

```bash
cd backend
mvn clean install
cd interest-tracker-server
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

#### 访问 API 文档

启动后访问：http://localhost:8080/swagger-ui.html

### 4. 前端配置与启动

#### 安装依赖

```bash
cd frontend
npm install
# 或
yarn install
# 或
pnpm install
```

#### 配置环境变量

创建 `.env.development` 文件：

```env
VITE_APP_TITLE=兴趣追踪平台
VITE_APP_BASE_API=http://localhost:8080/api
VITE_APP_ENV=development
```

#### 启动开发服务器

```bash
npm run dev
```

前端应用将在 `http://localhost:3000` 启动

### 5. 访问应用

- 前端地址：http://localhost:3000
- 后端 API：http://localhost:8080/api
- API 文档：http://localhost:8080/swagger-ui.html

---

## 📝 开发规范

### 后端开发规范

- 遵循 [通用架构设计与代码结构规范](backend/docs/通用架构设计与代码结构规范.md)
- 遵循 [CRUD开发流程规范](backend/docs/CRUD开发流程规范.md)
- 使用统一的异常处理和返回格式
- 所有 DO 对象继承 `BaseDO`，包含公共字段
- 使用 MyBatis-Plus 进行数据访问

### 前端开发规范

- 使用 TypeScript 进行类型约束
- 遵循 ESLint 和 Prettier 代码规范
- 组件使用 Composition API
- 使用 Pinia 进行状态管理
- API 请求统一使用封装的 request 工具

---

## 🗄 数据库设计

### 核心表结构

- **用户表**：`sys_user` - 用户基本信息
- **影视表**：`movie`、`movie_record` - 影视信息和观看记录
- **音乐表**：`album`、`album_record` - 专辑信息和听歌记录
- **照片表**：`photo` - 照片信息
- **标签表**：`tag` - 标签管理
- **其他表**：`book`、`book_record`、`travel_place`、`travel_record`、`concert`、`concert_record`

详细表结构请查看 [数据库初始化脚本](backend/database/init.sql)

---

## 📚 相关文档

### 后端文档

- [通用架构设计与代码结构规范](backend/docs/通用架构设计与代码结构规范.md)
- [CRUD开发流程规范](backend/docs/CRUD开发流程规范.md)
- [后端开发计划](backend/docs/后端开发计划.md)
- [项目设计方案](backend/docs/项目设计方案.md)

### 前端文档

- [前端开发计划](frontend/docs/前端开发计划.md)
- [前端项目初始化总结](frontend/docs/前端项目初始化总结.md)

### API 文档

- [用户认证与鉴权接口文档](api-doc/用户认证与鉴权接口文档.md)
- [影视模块-App接口文档](api-doc/影视模块-App接口文档.md)
- [音乐模块-App接口文档](api-doc/音乐模块-App接口文档.md)

### 需求文档

- [需求调研文档](requirements/01-需求调研文档.md)
- [需求分析文档](requirements/02-需求分析文档.md)
- [业务需求规格说明](requirements/03-业务需求规格说明.md)

---

## 🧪 测试

### 后端测试

```bash
cd backend
mvn test
```

### 前端测试

```bash
cd frontend
npm run lint
```

---

## 📦 构建部署

### 后端构建

```bash
cd backend
mvn clean package
```

生成的 JAR 文件位于：`backend/interest-tracker-server/target/interest-tracker-server-1.0.0-SNAPSHOT.jar`

### 前端构建

```bash
cd frontend
npm run build
```

构建产物位于：`frontend/dist/`

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

---

## 👥 作者

- **Interest Tracker Team**

---

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis-Plus](https://baomidou.com/)
- [TMDB API](https://www.themoviedb.org/)

---

## 📮 联系方式

如有问题或建议，欢迎提出 Issue 或 Pull Request。

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star！**

Made with ❤️ by Interest Tracker Team

</div>

