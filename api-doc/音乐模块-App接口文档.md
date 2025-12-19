## 音乐模块 App 接口文档（V1）

> 说明：本模块当前只支持手动添加专辑，暂不考虑API集成（如Last.fm）。  
> 用户需要手动填写专辑信息来创建记录。

---

## 一、通用说明

- **服务基础地址**：`http://{host}:{port}`
- **统一前缀**：`/api/music/albums`
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

## 二、创建专辑记录（含听歌记录）

- **URL**：`POST /api/music/albums`
- **说明**：
  - 手动创建专辑信息
  - 同时会为当前用户创建一条听歌记录
  - 如果专辑已存在（相同 title + artist），则复用专辑，只创建听歌记录

### 2.1 请求体 `AlbumCreateReqVO`

```json
{
  "title": "Abbey Road",
  "artist": "The Beatles",
  "releaseYear": 1969,
  "genre": "摇滚,流行",
  "description": "经典专辑...",
  "coverUrl": "https://example.com/cover.jpg",
  "totalTracks": 17,
  "duration": 2556,
  "listenStatus": 1,
  "personalRating": 8.5,
  "tags": "摇滚,经典",
  "comment": "很好听"
}
```

- **字段说明**：
  - `title` (String，**必填**)：专辑名称
  - `artist` (String，**必填**)：艺术家/乐队
  - `releaseYear` (Integer，可选)：发行年份
  - `genre` (String，可选)：音乐类型，逗号分隔，如 `"摇滚,流行,爵士"`
  - `description` (String，可选)：专辑简介
  - `coverUrl` (String，可选)：封面URL
  - `totalTracks` (Integer，可选)：总曲目数
  - `duration` (Integer，可选)：总时长（秒）
  - `listenStatus` (Integer，可选)：听歌状态  
    - `1` 想听  
    - `2` 在听  
    - `3` 已听  
    - `4` 弃听  
    - 若不传，后端会使用默认值（通常为「想听」）
  - `personalRating` (BigDecimal，可选)：个人评分，范围 `0-10`，例如 `8.5`
  - `tags` (String，可选)：标签，逗号分隔，如 `"摇滚,经典,60年代"`
  - `comment` (String，可选)：个人评价

> **推荐前端策略**：  
> - 必填字段：`title`、`artist`  
> - 其他字段可在创建时填写，也可在后续管理页面补充

### 2.2 响应体 `AlbumCreateRespVO`

```json
{
  "albumId": 1,
  "recordId": 10
}
```

- `albumId` (Long)：新建/复用的专辑主键 ID
- `recordId` (Long)：新建的听歌记录 ID

---

## 三、更新听歌记录

- **URL**：`PUT /api/music/albums/records/{id}`
- **说明**：只更新听歌记录相关信息，不修改专辑基础信息。

### 3.1 路径参数

- `id` (Long，必填)：听歌记录 ID

### 3.2 请求体 `AlbumRecordUpdateReqVO`

```json
{
  "listenStatus": 3,
  "personalRating": 8.5,
  "listenDate": "2025-01-15",
  "listenCount": 10,
  "comment": "重听依然很顶",
  "tags": "摇滚,治愈"
}
```

- 字段说明：
  - `listenStatus` (Integer，可选)：同上，1-4
  - `personalRating` (BigDecimal，可选)：0–10
  - `listenDate` (LocalDate，可选)：听歌日期，格式 `yyyy-MM-dd`
  - `listenCount` (Integer，可选)：听歌次数
  - `comment` (String，可选)：记录级别的评价
  - `tags` (String，可选)：标签（逗号分隔），会覆盖原有标签

> **业务规则**：
> - 状态改为「已听」时，如果 `listenDate` 为空，后端会自动设置为当前日期
> - 所有字段都是可选的（部分更新）

### 3.3 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 四、获取专辑详情

- **URL**：`GET /api/music/albums/{id}`

### 4.1 路径参数

- `id` (Long，必填)：专辑 ID（`albumId`）

### 4.2 响应体 `AlbumRespVO`

```json
{
  "album": {
    "id": 1,
    "title": "Abbey Road",
    "artist": "The Beatles",
    "releaseYear": 1969,
    "genre": "摇滚,流行",
    "description": "简介...",
    "coverUrl": "https://example.com/cover.jpg",
    "totalTracks": 17,
    "duration": 2556
  },
  "record": {
    "id": 1,
    "listenStatus": 3,
    "personalRating": 8.5,
    "listenDate": "2025-01-15",
    "listenCount": 10,
    "comment": "很好听",
    "tags": "摇滚,经典"
  }
}
```

- **专辑基础信息** (`album`)：
  - `id`：专辑 ID
  - `title`：专辑名称
  - `artist`：艺术家
  - `releaseYear`：发行年份
  - `genre`：音乐类型（逗号分隔）
  - `description`：简介
  - `coverUrl`：封面 URL
  - `totalTracks`：总曲目数
  - `duration`：总时长（秒）
- **当前用户听歌记录** (`record`)：
  - `id`：记录 ID
  - `listenStatus`：听歌状态（1-4）
  - `personalRating`：个人评分（0-10）
  - `listenDate`：听歌日期
  - `listenCount`：听歌次数
  - `comment`：评价
  - `tags`：标签（逗号分隔）

> 注：如果还未为当前用户创建记录，会返回错误。

---

## 五、专辑分页列表（含听歌记录信息）

- **URL**：`GET /api/music/albums`
- **说明**：返回当前登录用户的专辑 + 听歌记录分页列表。

### 5.1 查询参数 `AlbumPageReqVO`

```text
pageNo           页码（从 1 开始，继承 PageParam）
pageSize         每页数量
status           听歌状态：1 想听 / 2 在听 / 3 已听 / 4 弃听
artist           艺术家筛选（精确匹配）
genre            音乐类型筛选（精确匹配）
tag              标签筛选（模糊包含）
keyword          关键词（按专辑名、艺术家模糊搜索）
startListenDate  开始听歌日期（yyyy-MM-dd）
endListenDate    结束听歌日期（yyyy-MM-dd）
sort             排序字段：ctime / listenDate / rating
```

示例：

```http
GET /api/music/albums?pageNo=1&pageSize=10&status=3&artist=The Beatles&keyword=Abbey&sort=ctime
```

### 5.2 返回 `PageResult<AlbumPageRespVO>`

```json
{
  "total": 100,
  "list": [
    {
      "recordId": 1,
      "albumId": 1,
      "title": "Abbey Road",
      "artist": "The Beatles",
      "releaseYear": 1969,
      "coverUrl": "https://example.com/cover.jpg",
      "listenStatus": 3,
      "personalRating": 8.5,
      "listenDate": "2025-01-15",
      "listenCount": 10,
      "tags": "摇滚,经典",
      "createTime": "2025-01-10T10:00:00"
    }
  ]
}
```

- `total`：总记录数
- `list`：数组，每项包含：
  - 专辑基础信息（标题、艺术家、年份、封面等）
  - 当前用户的听歌状态、评分、标签等

---

## 六、删除听歌记录

- **URL**：`DELETE /api/music/albums/records/{id}`
- **说明**：
  - 仅删除指定用户的听歌记录；
  - 不会删除全局的 `album` 专辑基础信息。

### 6.1 路径参数

- `id` (Long，必填)：听歌记录 ID

### 6.2 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 七、错误码说明

| 错误码 | 说明 |
|--------|------|
| `1_002_001_001` | 专辑不存在 |
| `1_002_001_002` | 该专辑已存在，请勿重复添加 |
| `1_002_002_001` | 听歌记录不存在 |
| `1_002_002_002` | 无权操作该听歌记录 |
| `1_002_002_003` | 该专辑已添加，请勿重复添加 |

---

## 八、业务规则说明

### 8.1 数据创建规则

1. **专辑创建**：
   - 手动创建：必须提供 `title` 和 `artist`
   - 同一用户下，相同 `title` + `artist` 的专辑应该唯一（避免重复添加）
   - 如果专辑已存在，则复用，只创建听歌记录

2. **记录创建**：
   - 创建专辑时自动创建听歌记录
   - 默认状态为「想听」（listenStatus=1）
   - 用户ID从当前登录用户获取

### 8.2 数据更新规则

1. **权限控制**：
   - 只能更新自己的记录
   - 管理员可以管理所有记录（后续扩展）

2. **状态变更**：
   - 状态改为「已听」时，如果 `listenDate` 为空，自动设置为当前日期
   - 状态改为「弃听」时，可以记录原因（comment字段）

3. **评分规则**：
   - 评分范围：0-10
   - 支持 0.5 步长（如 8.5）

### 8.3 数据查询规则

1. **权限控制**：
   - 只能查询自己的记录
   - 列表查询自动过滤已删除的记录

2. **排序规则**：
   - 默认按创建时间倒序
   - 支持按听歌日期、评分排序

3. **筛选规则**：
   - 支持多维度筛选（状态、艺术家、类型、标签、关键词）
   - 筛选条件可以组合使用

---

## 九、后续扩展方向

### 9.1 API集成（后续扩展）

- 集成 Last.fm API 搜索专辑
- 集成 Spotify API 获取专辑信息
- 自动填充专辑信息（封面、简介等）

### 9.2 AI标签生成（后续扩展）

- 为专辑自动生成个性化标签
- 基于专辑信息（专辑名、艺术家、类型）生成标签

---

## 十、示例场景

### 场景1：手动创建专辑记录

```http
POST /api/music/albums
Content-Type: application/json

{
  "title": "Abbey Road",
  "artist": "The Beatles",
  "releaseYear": 1969,
  "genre": "摇滚,流行",
  "listenStatus": 1
}
```

### 场景2：更新听歌状态和评分

```http
PUT /api/music/albums/records/1
Content-Type: application/json

{
  "listenStatus": 3,
  "personalRating": 8.5,
  "listenDate": "2025-01-15",
  "comment": "很好听"
}
```

### 场景3：查询已听的专辑列表

```http
GET /api/music/albums?pageNo=1&pageSize=10&status=3&sort=listenDate
```

---

**文档版本**：V1.0  
**最后更新**：2025-01-XX

