# 兴趣追踪平台 - 前端项目

## 项目简介

兴趣追踪平台是一个整合摄影、影视、音乐、球赛四大兴趣领域的个人内容管理与数据分析平台。通过数据可视化帮助用户了解自己的兴趣习惯，发现更多相关内容。

## 技术栈

- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **UI框架**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **样式**: Tailwind CSS
- **代码规范**: ESLint + Prettier

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口封装
│   ├── assets/            # 资源文件
│   ├── components/        # 公共组件
│   │   └── layout/        # 布局组件
│   ├── layouts/           # 布局页面
│   ├── router/            # 路由配置
│   ├── store/             # Pinia状态管理
│   ├── styles/            # 全局样式
│   ├── types/             # TypeScript类型定义
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── .env.development       # 开发环境变量
├── .env.production        # 生产环境变量
├── index.html             # HTML模板
├── package.json           # 项目配置
├── tsconfig.json          # TypeScript配置
├── vite.config.ts         # Vite配置
└── tailwind.config.js     # Tailwind配置
```

## 快速开始

### 安装依赖

```bash
npm install
# 或
yarn install
# 或
pnpm install
```

### 开发运行

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

### 代码检查

```bash
npm run lint
```

### 代码格式化

```bash
npm run format
```

## 功能模块

- ✅ 用户认证（登录/注册）
- ✅ 摄影模块（照片上传、列表、详情）
- ✅ 影视模块（TMDB搜索、记录管理）
- ✅ 音乐模块（Last.fm搜索、记录管理）
- ✅ 球赛模块（比赛记录）
- ✅ 时间线页面（统一展示所有记录）
- ✅ 统计仪表板（数据可视化）
- ✅ 个人中心

## 设计特色

- 🌙 **深色主题**: 默认深色主题，支持浅色模式切换
- 🎨 **渐变配色**: 深紫到粉红的渐变配色方案
- ✨ **科技感**: 玻璃态效果、霓虹光效、流畅动画
- 📱 **响应式**: 完美适配桌面端和移动端
- 🎯 **现代化**: 使用最新的Vue 3 Composition API

## 环境变量

创建 `.env.development` 和 `.env.production` 文件：

```env
VITE_APP_TITLE=兴趣追踪平台
VITE_APP_BASE_API=http://localhost:8080/api
VITE_APP_ENV=development
```

## 后端接口

- 基础URL: `http://localhost:8080/api`
- 统一返回格式: `CommonResult<T>`
- 认证方式: JWT Token（存储在localStorage）

## 开发规范

- 使用 TypeScript 进行类型约束
- 遵循 ESLint 和 Prettier 代码规范
- 组件使用 Composition API
- 使用 Pinia 进行状态管理
- API 请求统一使用封装的 request 工具

## 浏览器支持

- Chrome (最新)
- Firefox (最新)
- Safari (最新)
- Edge (最新)

## 许可证

MIT

