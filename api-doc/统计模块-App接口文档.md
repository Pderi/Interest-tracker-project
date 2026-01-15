## 统计模块 App 接口文档（V1）

> 说明：本模块提供全面的数据统计和分析功能，包括统计概览、数据洞察、最近活动和时间线等功能。

---

## 一、通用说明

- **服务基础地址**：`http://{host}:{port}`
- **统一前缀**：`/api/dashboard`
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

## 二、获取统计概览

- **URL**：`GET /api/dashboard/summary`
- **说明**：获取各类兴趣记录的总数、新增数量、状态统计和趋势数据

### 2.1 查询参数 `DashboardSummaryReqVO`

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| timeRange | String | 否 | 时间范围类型，默认 `week`<br/>可选值：`today`（今日）、`week`（本周）、`month`（本月）、`year`（本年）、`custom`（自定义） |
| startDate | LocalDate | 否 | 自定义开始日期（当 `timeRange` 为 `custom` 时必填），格式 `yyyy-MM-dd` |
| endDate | LocalDate | 否 | 自定义结束日期（当 `timeRange` 为 `custom` 时必填），格式 `yyyy-MM-dd` |

示例：

```http
GET /api/dashboard/summary?timeRange=week

GET /api/dashboard/summary?timeRange=custom&startDate=2025-01-01&endDate=2025-01-31
```

### 2.2 响应体 `DashboardSummaryRespVO`

```json
{
  "photoCount": 100,
  "movieCount": 50,
  "musicCount": 80,
  "bookCount": 30,
  "travelCount": 20,
  "concertCount": 10,
  "matchCount": 5,
  "weeklyStats": {
    "photo": 10,
    "movie": 5,
    "music": 8,
    "book": 3,
    "travel": 2,
    "concert": 1,
    "match": 0
  },
  "lastWeekStats": {
    "photo": 8,
    "movie": 4,
    "music": 6,
    "book": 2,
    "travel": 1,
    "concert": 0,
    "match": 0
  },
  "monthlyStats": {
    "photo": 40,
    "movie": 20,
    "music": 30,
    "book": 12,
    "travel": 8,
    "concert": 4,
    "match": 2
  },
  "todayStats": {
    "photo": 2,
    "movie": 1,
    "music": 1,
    "book": 0,
    "travel": 0,
    "concert": 0,
    "match": 0
  },
  "movieStatusCounts": {
    "1": 10,
    "2": 5,
    "3": 30,
    "4": 5
  },
  "musicStatusCounts": {
    "1": 15,
    "2": 10,
    "3": 50,
    "4": 5
  },
  "bookStatusCounts": {
    "1": 5,
    "2": 3,
    "3": 20,
    "4": 2
  },
  "trendData": {
    "dates": ["01-15", "01-16", "01-17", "01-18", "01-19", "01-20", "01-21"],
    "photo": [2, 1, 3, 2, 1, 0, 1],
    "movie": [1, 0, 1, 1, 1, 1, 0],
    "music": [1, 2, 1, 1, 1, 1, 1],
    "book": [0, 1, 0, 1, 0, 1, 0],
    "travel": [0, 0, 1, 0, 1, 0, 0],
    "concert": [0, 0, 0, 0, 1, 0, 0],
    "match": [0, 0, 0, 0, 0, 0, 0]
  }
}
```

- **字段说明**：
  - `photoCount` (Long)：照片总数
  - `movieCount` (Long)：影视总数
  - `musicCount` (Long)：音乐总数
  - `bookCount` (Long)：阅读总数
  - `travelCount` (Long)：旅游总数
  - `concertCount` (Long)：演唱会总数
  - `matchCount` (Long)：球赛总数
  - `weeklyStats` (WeeklyStats)：当前时间范围新增统计
  - `lastWeekStats` (WeeklyStats，可选)：上一时间范围新增统计（用于计算增长率）
  - `monthlyStats` (MonthlyStats)：本月新增统计
  - `todayStats` (TodayStats)：今日新增统计
  - `movieStatusCounts` (Map<Integer, Long>，可选)：影视状态统计
    - key：状态值（1-想看，2-在看，3-已看，4-弃剧）
    - value：该状态下的数量
  - `musicStatusCounts` (Map<Integer, Long>，可选)：音乐状态统计
    - key：状态值（1-想听，2-在听，3-已听，4-弃听）
    - value：该状态下的数量
  - `bookStatusCounts` (Map<Integer, Long>，可选)：阅读状态统计
    - key：状态值（1-想读，2-在读，3-已读，4-弃读）
    - value：该状态下的数量
  - `trendData` (TrendData，可选)：趋势数据（用于图表展示）
    - `dates`：日期列表（最近N天，格式：MM-dd）
    - `photo`、`movie`、`music`、`book`、`travel`、`concert`、`match`：各类型每日新增数量数组

> **重要说明**：
> - 所有统计都基于实际活动日期，而非记录创建时间
> - 影视记录使用 `watchDate`（观看日期）
> - 音乐记录使用 `listenDate`（收听日期）
> - 阅读记录使用 `readDate`（阅读日期）
> - 旅游记录使用 `travelDate`（旅游日期）
> - 演唱会记录使用 `watchDate`（观演日期）

---

## 三、获取数据洞察

- **URL**：`GET /api/dashboard/insights`
- **说明**：提供智能化的数据分析，包括最活跃类型、评分趋势、活跃度峰值、完成率等

### 3.1 查询参数

同统计概览接口（`DashboardSummaryReqVO`）

示例：

```http
GET /api/dashboard/insights?timeRange=month
```

### 3.2 响应体 `InsightsRespVO`

```json
{
  "mostActiveType": {
    "type": "music",
    "typeName": "音乐",
    "count": 30,
    "percentage": 45.5
  },
  "ratingTrend": {
    "currentAvgRating": 8.5,
    "previousAvgRating": 8.2,
    "changePercentage": 3.66,
    "trend": "up"
  },
  "peakActivity": {
    "date": "2025-01-18",
    "count": 8,
    "type": "music",
    "typeName": "音乐"
  },
  "completionRate": {
    "completed": 100,
    "total": 150,
    "percentage": 66.67,
    "type": "movie",
    "typeName": "影视"
  }
}
```

- **字段说明**：
  - `mostActiveType` (MostActiveType，可选)：最活跃类型
    - `type`：类型标识（photo/movie/music/book/travel/concert/match）
    - `typeName`：类型中文名称
    - `count`：新增数量
    - `percentage`：占比（百分比）
  - `ratingTrend` (RatingTrend，可选)：评分趋势
    - `currentAvgRating`：当前平均评分
    - `previousAvgRating`：上期平均评分
    - `changePercentage`：变化百分比（正数表示上升，负数表示下降）
    - `trend`：趋势（`up`-上升，`down`-下降，`stable`-稳定）
  - `peakActivity` (PeakActivity，可选)：活跃度峰值
    - `date`：峰值日期（格式：yyyy-MM-dd）
    - `count`：峰值数量
    - `type`：峰值类型（如果有多个类型，显示主要类型）
    - `typeName`：类型中文名称
  - `completionRate` (CompletionRate，可选)：完成率
    - `completed`：已完成数量
    - `total`：总计划数量（已完成 + 进行中 + 计划中）
    - `percentage`：完成率（百分比）
    - `type`：类型（movie/music/book）
    - `typeName`：类型中文名称

---

## 四、获取最近活动

- **URL**：`GET /api/dashboard/recent-activities`
- **说明**：获取用户最近的记录活动，按活动时间倒序排列

### 4.1 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| limit | Integer | 否 | 返回数量限制，默认 `10`，最大 `50` |

示例：

```http
GET /api/dashboard/recent-activities?limit=20
```

### 4.2 响应体 `List<RecentActivityRespVO>`

```json
[
  {
    "id": 1,
    "type": "movie",
    "title": "肖申克的救赎",
    "description": "很经典的电影",
    "cover": "https://example.com/poster.jpg",
    "activityTime": "2025-01-21T10:30:00",
    "tags": "经典,励志",
    "detailId": 1,
    "recordId": 10,
    "rating": 9.5,
    "status": 3,
    "subtitle": "电影"
  },
  {
    "id": 2,
    "type": "music",
    "title": "Abbey Road",
    "description": "很好听",
    "cover": "https://example.com/cover.jpg",
    "activityTime": "2025-01-20T15:20:00",
    "tags": "摇滚,经典",
    "detailId": 1,
    "recordId": 5,
    "rating": 8.5,
    "status": 3,
    "subtitle": "The Beatles"
  }
]
```

- **字段说明**：
  - `id` (Long)：活动ID
  - `type` (String)：活动类型（photo/movie/music/book/travel/concert/match）
  - `title` (String)：活动标题
  - `description` (String，可选)：活动描述（评论）
  - `cover` (String，可选)：封面图片URL
  - `activityTime` (LocalDateTime)：活动时间（用于排序）
  - `tags` (String，可选)：标签（逗号分隔）
  - `detailId` (Long，可选)：关联的详情ID（用于跳转）
  - `recordId` (Long，可选)：关联的记录ID（用于跳转）
  - `rating` (BigDecimal，可选)：评分（0-10）
  - `status` (Integer，可选)：状态
    - 影视：1-想看，2-在看，3-已看，4-弃剧
    - 音乐：1-想听，2-在听，3-已听，4-弃听
    - 阅读：1-想读，2-在读，3-已读，4-弃读
    - 旅游：1-想去，2-计划中，3-已去
    - 演唱会：1-想看，2-已看
  - `subtitle` (String，可选)：副标题（如艺术家、作者、地点等）

---

## 五、获取时间线数据

- **URL**：`GET /api/dashboard/timeline`
- **说明**：获取时间线数据，支持按类型、时间范围、标签、关键词筛选

### 5.1 查询参数 `TimelinePageReqVO`

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNo | Integer | 否 | 页码，从1开始，默认 `1` |
| pageSize | Integer | 否 | 每页数量，默认 `10`，最大 `100` |
| types | String | 否 | 类型筛选，多个用逗号分隔，如 `"photo,movie,music"`<br/>可选值：`photo`、`movie`、`music`、`book`、`travel`、`concert`、`match` |
| startTime | LocalDateTime | 否 | 开始时间，格式 `yyyy-MM-ddTHH:mm:ss` |
| endTime | LocalDateTime | 否 | 结束时间，格式 `yyyy-MM-ddTHH:mm:ss` |
| tag | String | 否 | 标签筛选（关键词） |
| keyword | String | 否 | 关键词搜索（标题、描述） |

示例：

```http
GET /api/dashboard/timeline?pageNo=1&pageSize=20&types=movie,music&startTime=2025-01-01T00:00:00&endTime=2025-01-31T23:59:59

GET /api/dashboard/timeline?tag=经典&keyword=肖申克
```

### 5.2 响应体 `PageResult<TimelineItemRespVO>`

```json
{
  "total": 100,
  "list": [
    {
      "id": 1,
      "type": "movie",
      "title": "肖申克的救赎",
      "description": "很经典的电影",
      "cover": "https://example.com/poster.jpg",
      "activityTime": "2025-01-21T10:30:00",
      "tags": "经典,励志",
      "detailId": 1,
      "recordId": 10,
      "metadata": "{\"director\":\"弗兰克·德拉邦特\",\"actors\":\"摩根·弗里曼,蒂姆·罗宾斯\"}"
    },
    {
      "id": 2,
      "type": "music",
      "title": "Abbey Road",
      "description": "很好听",
      "cover": "https://example.com/cover.jpg",
      "activityTime": "2025-01-20T15:20:00",
      "tags": "摇滚,经典",
      "detailId": 1,
      "recordId": 5,
      "metadata": "{\"artist\":\"The Beatles\"}"
    }
  ]
}
```

- **字段说明**：
  - `total`：总记录数
  - `list`：时间线项数组，每项包含：
    - `id` (Long)：活动ID
    - `type` (String)：活动类型
    - `title` (String)：活动标题
    - `description` (String，可选)：活动描述
    - `cover` (String，可选)：封面图片URL
    - `activityTime` (LocalDateTime)：活动时间（用于排序和展示）
    - `tags` (String，可选)：标签（逗号分隔）
    - `detailId` (Long，可选)：关联的详情ID（用于跳转）
    - `recordId` (Long，可选)：关联的记录ID（用于跳转）
    - `metadata` (String，可选)：类型特定的元数据（JSON格式）

---

## 六、时间范围说明

系统支持5种时间范围类型：

### 6.1 今日 (today)
- **当前范围**：今天 00:00:00 - 现在
- **上一范围**：昨天 00:00:00 - 今天 00:00:00
- **趋势天数**：1天

### 6.2 本周 (week)
- **当前范围**：7天前 00:00:00 - 现在
- **上一范围**：14天前 00:00:00 - 7天前 00:00:00
- **趋势天数**：7天

### 6.3 本月 (month)
- **当前范围**：1个月前 00:00:00 - 现在
- **上一范围**：2个月前 00:00:00 - 1个月前 00:00:00
- **趋势天数**：30天

### 6.4 本年 (year)
- **当前范围**：1年前 00:00:00 - 现在
- **上一范围**：2年前 00:00:00 - 1年前 00:00:00
- **趋势天数**：365天

### 6.5 自定义 (custom)
- **当前范围**：`startDate` 00:00:00 - `endDate` 23:59:59
- **上一范围**：根据当前范围长度计算（向前推移相同长度）
- **趋势天数**：根据范围长度计算（最多30天）

---

## 七、业务规则说明

### 7.1 统计规则

1. **基于活动日期**：
   - 所有统计都基于实际活动日期，而非记录创建时间
   - 如果活动日期为空，则使用记录创建时间

2. **状态统计**：
   - 只统计有状态的记录类型（影视、音乐、阅读）
   - 状态统计基于当前用户的所有记录

3. **趋势数据**：
   - 趋势数据按天统计，展示最近N天的每日新增数量
   - 趋势天数根据时间范围类型自动计算

### 7.2 数据洞察规则

1. **最活跃类型**：
   - 统计当前时间范围内各类型的新增数量
   - 找出数量最多的类型作为最活跃类型
   - 计算占比（该类型数量 / 所有类型总数）

2. **评分趋势**：
   - 统计当前时间范围和上一时间范围内有评分的记录
   - 计算平均评分和变化百分比
   - 根据变化百分比判断趋势（上升/下降/稳定）

3. **活跃度峰值**：
   - 按天统计当前时间范围内的活动数量
   - 找出数量最多的日期作为峰值日期

4. **完成率**：
   - 统计影视、音乐、阅读类型的完成率
   - 已完成数量：状态为「已看/已听/已读」的记录数
   - 总计划数量：状态为「想看/想听/想读 + 在看/在听/在读 + 已看/已听/已读」的记录数

### 7.3 最近活动规则

1. **排序规则**：
   - 按活动时间倒序排列
   - 活动时间优先使用实际活动日期，如果为空则使用记录创建时间

2. **数量限制**：
   - 默认返回10条，最多50条

### 7.4 时间线规则

1. **排序规则**：
   - 按活动时间倒序排列

2. **筛选规则**：
   - 支持多类型筛选（用逗号分隔）
   - 支持时间范围筛选
   - 支持标签和关键词搜索

---

## 八、示例场景

### 场景1：获取本周统计概览

```http
GET /api/dashboard/summary?timeRange=week
```

### 场景2：获取本月数据洞察

```http
GET /api/dashboard/insights?timeRange=month
```

### 场景3：获取最近20条活动

```http
GET /api/dashboard/recent-activities?limit=20
```

### 场景4：获取自定义时间范围的统计

```http
GET /api/dashboard/summary?timeRange=custom&startDate=2025-01-01&endDate=2025-01-31
```

### 场景5：获取影视和音乐的时间线数据

```http
GET /api/dashboard/timeline?pageNo=1&pageSize=20&types=movie,music
```

### 场景6：获取特定标签的时间线数据

```http
GET /api/dashboard/timeline?tag=经典&pageNo=1&pageSize=10
```

---

**文档版本**：V1.0  
**最后更新**：2026-01-16

