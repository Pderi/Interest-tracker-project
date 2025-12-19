# 豆瓣 API 实现说明

## 一、实现概述

已实现豆瓣 API 作为国内备选数据源，支持自动切换和降级。

## 二、核心组件

### 2.1 DoubanClient

豆瓣 API 客户端，负责调用豆瓣 API：
- `search()` - 搜索电影和电视剧
- `getMovieDetail()` - 获取电影详情
- `getTVDetail()` - 获取电视剧详情

### 2.2 DomesticProvider

国内 API 提供者实现：
- 调用 `DoubanClient` 获取数据
- 将豆瓣响应转换为 TMDB 格式（统一接口）
- 实现 `MovieDataProvider` 接口

### 2.3 MovieIdConverter

ID 转换工具，处理豆瓣 ID（字符串）和 Long 类型的转换：
- `doubanIdToLong()` - 豆瓣 ID → Long
- `longToDoubanId()` - Long → 豆瓣 ID
- `isDoubanId()` - 判断是否是豆瓣 ID

**ID 映射策略**：
- 豆瓣 ID 转换为 Long 时，使用负数范围（-1000000000 以下）来标识
- 这样可以避免与 TMDB ID（正数）冲突

## 三、工作流程

### 3.1 搜索流程

```
1. 用户搜索关键词
   ↓
2. MovieDataService 尝试使用 TMDBProvider
   ↓
3. 如果成功 → 返回结果
   ↓
4. 如果失败 → 自动切换到 DomesticProvider（豆瓣）
   ↓
5. 豆瓣搜索 → 转换为 TMDB 格式 → 返回结果
```

### 3.2 获取详情流程

```
1. 用户选择搜索结果（包含 ID）
   ↓
2. MovieDataService 根据 ID 类型选择数据源
   - 如果是豆瓣 ID（负数）→ 优先使用豆瓣 API
   - 如果是 TMDB ID（正数）→ 优先使用 TMDB API
   ↓
3. 如果失败 → 自动切换到另一个数据源
```

## 四、配置说明

### 4.1 启用豆瓣 API

在 `application.yml` 中：

```yaml
domestic:
  api:
    enabled: true   # 设置为 true 启用豆瓣 API
    type: douban   # API 类型
```

### 4.2 注意事项

1. **非官方 API**：豆瓣官方 API 已停止申请，使用的是非官方 API
2. **稳定性**：非官方 API 可能随时失效，需要监控
3. **法律风险**：使用前请确保遵守相关法律法规
4. **User-Agent**：豆瓣 API 可能需要设置 User-Agent，已自动设置

## 五、数据转换

### 5.1 搜索响应转换

- 豆瓣响应 → TMDB 格式
- ID 转换：字符串 → Long（使用负数范围标识）
- 类型标记：movie/tv
- 评分转换：豆瓣 0-10 → TMDB 0-10（格式相同）

### 5.2 详情转换

- 豆瓣详情 → TMDB 格式
- 字段映射：
  - `title` → `title` / `name`
  - `summary` → `overview`
  - `rating.average` → `voteAverage`
  - `images.large` → `posterPath`
  - `directors` → `credits.crew` (job: Director)
  - `casts` → `credits.cast`

## 六、ID 映射说明

### 6.1 ID 格式

- **TMDB ID**：正数 Long（如：550）
- **豆瓣 ID**：负数 Long（如：-1000001292，表示豆瓣 ID "1292"）

### 6.2 转换规则

```java
// 豆瓣 ID "1292" → Long -1000001292
Long id = MovieIdConverter.doubanIdToLong("1292");

// Long -1000001292 → 豆瓣 ID "1292"
String doubanId = MovieIdConverter.longToDoubanId(id);
```

### 6.3 判断规则

```java
// 判断是否是豆瓣 ID
if (MovieIdConverter.isDoubanId(id)) {
    // 使用豆瓣 API
} else {
    // 使用 TMDB API
}
```

## 七、已知限制

1. **年份信息**：豆瓣只有年份，没有具体日期，所以 `releaseDate` 为空
2. **时长格式**：豆瓣时长是 "139分钟" 格式，需要解析
3. **ID 冲突**：如果豆瓣 ID 无法解析为数字，使用哈希值，可能导致 ID 冲突
4. **API 稳定性**：非官方 API，可能随时失效

## 八、测试建议

1. **搜索测试**：
   - 测试中文关键词搜索
   - 测试电影和电视剧混合搜索
   - 测试分页功能

2. **详情测试**：
   - 测试电影详情获取
   - 测试电视剧详情获取
   - 测试 ID 转换是否正确

3. **切换测试**：
   - 测试 TMDB 失败时自动切换到豆瓣
   - 测试豆瓣失败时自动切换到 TMDB

## 九、后续优化

1. **添加缓存**：缓存搜索结果和详情，减少 API 调用
2. **错误重试**：添加重试机制，提高成功率
3. **监控告警**：监控 API 可用性，及时发现问题
4. **ID 映射表**：如果需要，可以建立 ID 映射表，避免 ID 冲突

## 十、使用示例

### 10.1 启用豆瓣 API

```yaml
domestic:
  api:
    enabled: true
    type: douban
```

### 10.2 测试搜索

```bash
GET /api/movies/search?keyword=肖申克的救赎&page=1
```

### 10.3 测试创建

```bash
POST /api/movies
{
  "tmdbId": -1000001292,  # 豆瓣 ID（负数）
  "type": 1
}
```

## 十一、总结

✅ **已完成**：
- 豆瓣 API 客户端实现
- 数据转换逻辑
- ID 映射工具
- 自动切换逻辑

⚠️ **注意事项**：
- 非官方 API，稳定性不确定
- 需要遵守相关法律法规
- 建议添加监控和告警

🎯 **优势**：
- 国内访问速度快
- 中文数据丰富
- 自动降级，提高可用性

