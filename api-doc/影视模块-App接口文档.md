## 影视模块 App 接口文档（V1）

> 说明：本模块当前不依赖外部电影数据库（TMDB/豆瓣/WMDB）也可正常使用。  
> 若 `tmdbId` 不传，则按「手动录入」模式创建影视。

---

## 一、通用说明

- **服务基础地址**：`http://{host}:{port}`
- **统一前缀**：`/api/movies`
- **鉴权**：需先登录，携带用户 Token（具体见 `api-doc/用户认证与鉴权接口文档.md`）
- **返回结构**：统一使用 `CommonResult<T>`

```json
{
  "code": 0,
  "msg": "success",
  "data": { ... }   // 具体业务数据
}
```

---

## 二、创建影视记录（含观影记录）

- **URL**：`POST /api/movies`
- **说明**：
  - 支持两种创建方式：
    1. 通过 `tmdbId` 从外部数据源创建（当前受网络影响，可暂不使用）
    2. 直接手动录入电影信息（推荐当前阶段）
  - 同时会为当前用户创建一条观影记录。

### 2.1 请求体 `MovieCreateReqVO`

```json
{
  "tmdbId": 550,
  "title": "肖申克的救赎",
  "type": 1,
  "watchStatus": 1,
  "personalRating": 9.5,
  "tags": "经典,励志",
  "comment": "必须收藏"
}
```

- **字段说明**：
  - `tmdbId` (Long，可选)：  
    - 从外部数据源创建时使用；当前网络不可用时建议 **不传**。
  - `title` (String，手动创建时必填)：电影/剧集名称。
  - `type` (Integer，必填)：类型  
    - `1`：电影  
    - `2`：电视剧
  - `watchStatus` (Integer，可选)：观看状态  
    - `1` 想看  
    - `2` 在看  
    - `3` 已看  
    - `4` 弃剧  
    - 若不传，后端会使用默认值（通常为「想看」）。
  - `personalRating` (BigDecimal，可选)：个人评分，范围 `0-10`，例如 `8.5`。
  - `tags` (String，可选)：标签，逗号分隔，如 `"经典,动作,科幻"`。
  - `comment` (String，可选)：个人评价。

> **推荐前端当前策略**：  
> - 不使用 `tmdbId`，只传 `title + type` + 观影记录相关字段。  
> - 后端 MovieDO 中其它字段（年份、封面等）可在后续管理页面补充或通过单独接口维护。

### 2.2 响应体 `MovieCreateRespVO`

```json
{
  "movieId": 1,
  "recordId": 10
}
```

- `movieId` (Long)：新建/复用的影视主键 ID
- `recordId` (Long)：新建的观影记录 ID

---

## 三、更新观影记录

- **URL**：`PUT /api/movies/records/{id}`
- **说明**：只更新观影记录相关信息，不修改电影基础信息。

### 3.1 路径参数

- `id` (Long，必填)：观影记录 ID

### 3.2 请求体 `MovieRecordUpdateReqVO`

```json
{
  "watchStatus": 3,
  "personalRating": 9.0,
  "watchDate": "2025-01-15",
  "watchDuration": 140,
  "progress": 100,
  "comment": "重看依然很顶",
  "tags": "经典,治愈"
}
```

- 字段说明：
  - `watchStatus` (Integer，可选)：同上，1-4。
  - `personalRating` (BigDecimal，可选)：0–10。
  - `watchDate` (LocalDate，可选)：观看日期，格式 `yyyy-MM-dd`。
  - `watchDuration` (Integer，可选)：本次观看时长，单位：分钟。
  - `progress` (BigDecimal，可选)：观看进度百分比，0–100，例如 `100`。
  - `comment` (String，可选)：记录级别的评价。
  - `tags` (String，可选)：标签（逗号分隔），会覆盖原有标签。

### 3.3 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 四、获取影视详情

- **URL**：`GET /api/movies/{id}`

### 4.1 路径参数

- `id` (Long，必填)：影视 ID（`movieId`）

### 4.2 响应体 `MovieRespVO`（结构简要）

> 具体字段以后端 `MovieRespVO` 为准，这里列出主要关注项：

- 影视基础信息：
  - `id`：影视 ID
  - `title`：标题
  - `type`：类型（1 电影 / 2 电视剧）
  - `releaseYear`：年份
  - `posterUrl`：封面 URL
  - `description`：简介
  - `director`：导演
  - `actors`：演员（逗号分隔）
  - `genre`：类型标签
- 当前用户观影记录（如果存在）：
  - `recordId`：记录 ID
  - `watchStatus`
  - `personalRating`
  - `watchDate`
  - `watchDuration`
  - `progress`
  - `comment`
  - `tags`

> 注：如果还未为当前用户创建记录，记录相关字段可能为空。

---

## 五、影视分页列表（含观影记录信息）

- **URL**：`GET /api/movies`
- **说明**：返回当前登录用户的影视 + 观影记录分页列表。

### 5.1 查询参数 `MoviePageReqVO`

```text
pageNo       页码（从 1 开始，继承 PageParam）
pageSize     每页数量
watchStatus  观看状态：1 想看 / 2 在看 / 3 已看 / 4 弃剧
type         类型：1 电影 / 2 电视剧
tag          标签筛选（模糊包含）
keyword      关键词（按标题模糊搜索）
startWatchDate  开始观看日期（yyyy-MM-dd）
endWatchDate    结束观看日期（yyyy-MM-dd）
sort         排序字段：ctime / watchDate / rating
```

示例：

```http
GET /api/movies?pageNo=1&pageSize=10&watchStatus=3&type=1&keyword=肖申克&sort=ctime
```

### 5.2 返回 `PageResult<MoviePageRespVO>`

- `total`：总记录数
- `list`：数组，每项结构类似 `MovieRespVO` 的简化版（列表视图），包含：
  - 影视基础信息（标题、封面、年份、类型等）
  - 当前用户的观影状态、评分、标签等

---

## 六、删除观影记录

- **URL**：`DELETE /api/movies/records/{id}`
- **说明**：
  - 仅删除指定用户的观影记录；
  - 不会删除全局的 `movie` 影视基础信息。

### 6.1 路径参数

- `id` (Long，必填)：观影记录 ID

### 6.2 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 七、第三方搜索（TMDB/豆瓣/WMDB 混合，当前受网络限制）

- **URL**：`GET /api/movies/search`
- **说明**：
  - 后端会按优先级尝试外部数据源（当前实现：豆瓣为主，TMDB 为备选；但网络可能全部超时）；
  - 适合做「搜索后一键带出信息再保存」的场景；
  - 在当前网络环境下，可以暂时只依赖“手动录入”模式。

### 7.1 查询参数

```text
keyword   搜索关键词（必填）
page      页码（从 1 开始，可选，默认 1）
```

示例：

```http
GET /api/movies/search?keyword=Inception&page=1
```

### 7.2 响应 `PageResult<MovieSearchRespVO>`

每条 `MovieSearchRespVO`：

- `tmdbId`：外部数据源返回的 ID（Long）
- `title`：标题
- `originalTitle`：原始标题
- `type`：1 电影 / 2 电视剧
- `releaseYear`：年份
- `posterUrl`：海报 URL（已拼好完整地址）
- `overview`：简介
- `rating`：评分（0–10）

> 建议前端用这个接口的结果，仅作为「搜索+预填表单」用，而不是直接依赖它作为核心功能。  
> 在外网不稳定时，继续保留“手动录入”通道，保证业务可用。


