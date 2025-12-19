# TMDB API 配置说明

## 一、获取 TMDB API Key

### 1. 注册 TMDB 账号

1. 访问 TMDB 官网：https://www.themoviedb.org/
2. 点击右上角 **Sign Up** 注册账号
3. 填写邮箱、用户名、密码等信息完成注册
4. 登录账号

### 2. 申请 API Key

1. 登录后，访问 API 设置页面：**https://www.themoviedb.org/settings/api**
2. 点击 **Request an API Key** 按钮
3. 选择 **Developer** 类型（免费）
4. 填写申请信息：
   - **Application Name**: Interest Tracker
   - **Application URL**: http://localhost（或你的实际应用地址）
   - **Application Summary**: 使用下面的英文描述（见下方）
5. 阅读并同意 **Terms of Use**
6. 点击 **Submit** 提交申请

### 3. 获取认证信息

- 申请提交后，通常需要几分钟审核时间
- 审核通过后，在 **API Settings** 页面可以看到两种认证方式：
  - **API Key (v3 auth)** - API 密钥
  - **API Read Access Token** - 读访问令牌

#### 两种认证方式的区别

| 特性 | API Key | Read Access Token |
|------|---------|-------------------|
| **使用方式** | 作为查询参数 `?api_key=xxx` | 作为 Bearer Token 在请求头中 |
| **安全性** | 较低（会出现在 URL 中） | 较高（不会出现在 URL 中） |
| **API 版本** | 仅支持 v3 API | 支持 v3 和 v4 API |
| **推荐场景** | 简单快速集成 | 生产环境（推荐） |

**建议**：优先使用 **Read Access Token**，更安全且功能更强大。

## 二、配置认证信息

### 方式一：使用 API Key（简单快速）

编辑 `backend/interest-tracker-server/src/main/resources/application.yml`：

```yaml
tmdb:
  api:
    key: your-api-key-here  # 替换为你的实际 API Key
```

或通过环境变量：
```bash
export TMDB_API_KEY=your-api-key-here
```

### 方式二：使用 Read Access Token（推荐，更安全）

编辑 `backend/interest-tracker-server/src/main/resources/application.yml`：

```yaml
tmdb:
  api:
    read-access-token: your-read-access-token-here  # 替换为你的实际 Token
```

或通过环境变量：
```bash
export TMDB_READ_ACCESS_TOKEN=your-read-access-token-here
```

**注意**：
- 如果同时配置了 `key` 和 `read-access-token`，系统会**优先使用 Token 认证方式**
- 推荐使用 **Read Access Token**，因为更安全且支持 v3/v4 API

### 完整配置示例

```yaml
tmdb:
  api:
    # 方式一：使用 API Key
    key: ${TMDB_API_KEY:}
    # 方式二：使用 Read Access Token（推荐）
    read-access-token: ${TMDB_READ_ACCESS_TOKEN:}
    # 其他配置
    base-url: https://api.themoviedb.org/3
    image-base-url: https://image.tmdb.org/t/p/w500
    language: zh-CN
    timeout: 5000
```

### 环境变量配置

#### Windows (PowerShell)
```powershell
# 使用 API Key
$env:TMDB_API_KEY="your-api-key-here"

# 或使用 Read Access Token（推荐）
$env:TMDB_READ_ACCESS_TOKEN="your-read-access-token-here"
```

#### Windows (CMD)
```cmd
set TMDB_API_KEY=your-api-key-here
set TMDB_READ_ACCESS_TOKEN=your-read-access-token-here
```

#### Linux/Mac
```bash
export TMDB_API_KEY=your-api-key-here
export TMDB_READ_ACCESS_TOKEN=your-read-access-token-here
```

### 多环境配置

创建 `application-dev.yml`（开发环境）：

```yaml
tmdb:
  api:
    read-access-token: your-dev-token-here
```

创建 `application-prod.yml`（生产环境）：

```yaml
tmdb:
  api:
    read-access-token: ${TMDB_READ_ACCESS_TOKEN}  # 从环境变量读取
```

## 三、API 限制说明

- **免费额度**：每分钟 40 次请求
- **建议**：在生产环境中实现请求限流和缓存机制
- **注意**：不要将 API Key 提交到代码仓库，使用环境变量或配置文件（已加入 .gitignore）

## 四、测试配置

配置完成后，启动应用，调用搜索接口测试：

```bash
GET http://localhost:8080/api/movies/search?keyword=Inception&page=1
```

如果返回搜索结果，说明配置成功。

## 五、应用描述（Application Summary）

### 简洁版（推荐，适合表单填写）

```
Interest Tracker is a personal content management and analytics platform that helps users track and analyze their movie and TV series viewing habits. The application allows users to record viewing history, rate content, manage watchlists, and generate personalized statistics. We use TMDB API to fetch comprehensive movie and TV series metadata, including titles, descriptions, cast, ratings, and posters, to enhance the user experience and provide accurate content information.
```

### 详细版（备用）

```
Interest Tracker is an integrated personal content management and analytics platform covering four major interest areas: photography, movies/TV series, music, and sports. 

For the movie and TV series module, we use TMDB API to:
- Search and discover movies and TV series
- Fetch detailed metadata (titles, descriptions, cast, crew, ratings, posters)
- Automatically populate content information when users add viewing records
- Provide rich content data for analytics and visualization

The platform helps users track their viewing history, manage watchlists, rate content, and generate personalized viewing statistics and insights. All data is stored locally, and TMDB API is used solely for content metadata retrieval to enhance user experience.
```

### 中文翻译（参考）

**简洁版**：
兴趣追踪器（Interest Tracker）是一个个人内容管理与分析平台，帮助用户追踪和分析电影、电视剧观看习惯。应用允许用户记录观看历史、评分、管理想看列表，并生成个性化统计数据。我们使用 TMDB API 获取电影和电视剧的元数据（包括标题、描述、演员、评分、海报等），以提升用户体验并提供准确的内容信息。

**详细版**：
兴趣追踪器（Interest Tracker）是一个整合摄影、影视、音乐、球赛四大兴趣领域的个人内容管理与数据分析平台。

在影视模块中，我们使用 TMDB API 来：
- 搜索和发现电影、电视剧
- 获取详细元数据（标题、描述、演员、工作人员、评分、海报等）
- 在用户添加观看记录时自动填充内容信息
- 为数据分析和可视化提供丰富的内容数据

平台帮助用户追踪观看历史、管理想看列表、评分内容，并生成个性化观看统计和洞察。所有数据本地存储，TMDB API 仅用于内容元数据获取，以提升用户体验。

## 六、相关链接

- **TMDB 官网**：https://www.themoviedb.org/
- **API 文档**：https://developers.themoviedb.org/3
- **API 设置页面**：https://www.themoviedb.org/settings/api
- **API 使用指南**：https://developers.themoviedb.org/3/getting-started/introduction

