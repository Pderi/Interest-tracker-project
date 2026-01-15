## 演唱会模块 App 接口文档（V1）

> 说明：本模块当前只支持手动添加演唱会，暂不考虑API集成。  
> 用户需要手动填写演唱会信息来创建记录。

---

## 一、通用说明

- **服务基础地址**：`http://{host}:{port}`
- **统一前缀**：`/api/concert/concerts`
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

## 二、创建演唱会记录（含观演记录）

- **URL**：`POST /api/concert/concerts`
- **说明**：
  - 手动创建演唱会信息
  - 同时会为当前用户创建一条观演记录
  - 如果演唱会已存在（相同 artist + title + concertDate），则复用演唱会，只创建观演记录

### 2.1 请求体 `ConcertCreateReqVO`

```json
{
  "artist": "The Beatles",
  "title": "Abbey Road Tour",
  "concertDate": "2025-06-15T20:00:00",
  "venue": "国家体育场",
  "city": "北京",
  "country": "中国",
  "concertType": 1,
  "description": "经典演唱会...",
  "posterUrl": "https://example.com/poster.jpg",
  "watchStatus": 1,
  "personalRating": 8.5,
  "watchDate": "2025-06-15",
  "ticketPrice": 580.00,
  "seatInfo": "A区1排1号",
  "tags": "摇滚,经典",
  "comment": "很棒的演出"
}
```

- **字段说明**：
  - `artist` (String，**必填**)：艺术家/乐队
  - `title` (String，可选)：演出名称
  - `concertDate` (LocalDateTime，可选)：演出日期，格式 `yyyy-MM-ddTHH:mm:ss`，例如 `2025-06-15T20:00:00`
  - `venue` (String，可选)：演出场地
  - `city` (String，可选)：城市
  - `country` (String，可选)：国家
  - `concertType` (Integer，可选)：演出类型  
    - `1` 演唱会  
    - `2` 音乐节  
    - `3` 演出  
    - `4` 其他
  - `description` (String，可选)：描述
  - `posterUrl` (String，可选)：海报URL
  - `watchStatus` (Integer，可选)：观演状态  
    - `1` 想看  
    - `2` 已看  
    - 若不传，后端会使用默认值（通常为「想看」）
  - `personalRating` (BigDecimal，可选)：个人评分，范围 `0-10`，例如 `8.5`
  - `watchDate` (LocalDate，可选)：观演日期，格式 `yyyy-MM-dd`
  - `ticketPrice` (BigDecimal，可选)：票价
  - `seatInfo` (String，可选)：座位信息
  - `tags` (String，可选)：标签，逗号分隔，如 `"摇滚,经典,60年代"`
  - `comment` (String，可选)：个人评价

> **推荐前端策略**：  
> - 必填字段：`artist`  
> - 其他字段可在创建时填写，也可在后续管理页面补充

### 2.2 响应体 `ConcertCreateRespVO`

```json
{
  "concertId": 1,
  "recordId": 10
}
```

- `concertId` (Long)：新建/复用的演唱会主键 ID
- `recordId` (Long)：新建的观演记录 ID

---

## 三、更新观演记录

- **URL**：`PUT /api/concert/concerts/records/{id}`
- **说明**：只更新观演记录相关信息，不修改演唱会基础信息。

### 3.1 路径参数

- `id` (Long，必填)：观演记录 ID

### 3.2 请求体 `ConcertRecordUpdateReqVO`

```json
{
  "watchStatus": 2,
  "personalRating": 8.5,
  "watchDate": "2025-06-15",
  "ticketPrice": 580.00,
  "seatInfo": "A区1排1号",
  "comment": "重看依然很顶"
}
```

- 字段说明：
  - `watchStatus` (Integer，可选)：同上，1-2
  - `personalRating` (BigDecimal，可选)：0–10
  - `watchDate` (LocalDate，可选)：观演日期，格式 `yyyy-MM-dd`
  - `ticketPrice` (BigDecimal，可选)：票价
  - `seatInfo` (String，可选)：座位信息
  - `comment` (String，可选)：记录级别的评价

> **业务规则**：
> - 状态改为「已看」时，如果 `watchDate` 为空，后端会自动设置为当前日期
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

## 四、更新演唱会信息

- **URL**：`PUT /api/concert/concerts/{id}`
- **说明**：只更新演唱会基础信息，不修改观演记录。

### 4.1 路径参数

- `id` (Long，必填)：演唱会 ID

### 4.2 请求体 `ConcertUpdateReqVO`

```json
{
  "artist": "The Beatles",
  "title": "Abbey Road Tour",
  "concertDate": "2025-06-15T20:00:00",
  "venue": "国家体育场",
  "city": "北京",
  "country": "中国",
  "concertType": 1,
  "description": "更新后的描述",
  "posterUrl": "https://example.com/new-poster.jpg"
}
```

- 字段说明：
  - 所有字段均为可选，只更新传入的字段
  - `artist` (String，可选)：艺术家/乐队
  - `title` (String，可选)：演出名称
  - `concertDate` (LocalDateTime，可选)：演出日期
  - `venue` (String，可选)：演出场地
  - `city` (String，可选)：城市
  - `country` (String，可选)：国家
  - `concertType` (Integer，可选)：演出类型（1-4）
  - `description` (String，可选)：描述
  - `posterUrl` (String，可选)：海报URL

### 4.3 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 五、获取演唱会详情

- **URL**：`GET /api/concert/concerts/{id}`

### 5.1 路径参数

- `id` (Long，必填)：演唱会 ID（`concertId`）

### 5.2 响应体 `ConcertRespVO`

```json
{
  "concert": {
    "id": 1,
    "artist": "The Beatles",
    "title": "Abbey Road Tour",
    "concertDate": "2025-06-15T20:00:00",
    "venue": "国家体育场",
    "city": "北京",
    "country": "中国",
    "concertType": 1,
    "description": "经典演唱会...",
    "posterUrl": "https://example.com/poster.jpg"
  },
  "record": {
    "id": 1,
    "watchStatus": 2,
    "personalRating": 8.5,
    "watchDate": "2025-06-15",
    "ticketPrice": 580.00,
    "seatInfo": "A区1排1号",
    "comment": "很棒的演出",
    "tags": "摇滚,经典"
  }
}
```

- **演唱会基础信息** (`concert`)：
  - `id`：演唱会 ID
  - `artist`：艺术家/乐队
  - `title`：演出名称
  - `concertDate`：演出日期
  - `venue`：演出场地
  - `city`：城市
  - `country`：国家
  - `concertType`：演出类型（1-4）
  - `description`：描述
  - `posterUrl`：海报 URL
- **当前用户观演记录** (`record`)：
  - `id`：记录 ID
  - `watchStatus`：观演状态（1-2）
  - `personalRating`：个人评分（0-10）
  - `watchDate`：观演日期
  - `ticketPrice`：票价
  - `seatInfo`：座位信息
  - `comment`：评价
  - `tags`：标签（逗号分隔）

> 注：如果还未为当前用户创建记录，会返回错误。

---

## 六、演唱会分页列表（含观演记录信息和统计）

- **URL**：`GET /api/concert/concerts`
- **说明**：返回当前登录用户的演唱会 + 观演记录分页列表，同时包含各状态的统计信息。

### 6.1 查询参数 `ConcertPageReqVO`

```text
pageNo           页码（从 1 开始，继承 PageParam）
pageSize         每页数量
status           观演状态：1 想看 / 2 已看
artist           艺术家筛选（精确匹配）
city             城市筛选（精确匹配）
concertType      演出类型筛选：1 演唱会 / 2 音乐节 / 3 演出 / 4 其他
tag              标签筛选（模糊包含）
keyword          关键词（按演出名称、艺术家模糊搜索）
startWatchDate   开始观演日期（yyyy-MM-dd）
endWatchDate     结束观演日期（yyyy-MM-dd）
sort             排序字段：ctime / watchDate / rating
```

示例：

```http
GET /api/concert/concerts?pageNo=1&pageSize=10&status=2&artist=The Beatles&keyword=Abbey&sort=ctime
```

### 6.2 返回 `ConcertPageWithStatsRespVO`

```json
{
  "page": {
    "total": 100,
    "list": [
      {
        "recordId": 1,
        "concertId": 1,
        "artist": "The Beatles",
        "title": "Abbey Road Tour",
        "concertDate": "2025-06-15T20:00:00",
        "venue": "国家体育场",
        "city": "北京",
        "country": "中国",
        "concertType": 1,
        "posterUrl": "https://example.com/poster.jpg",
        "watchStatus": 2,
        "personalRating": 8.5,
        "watchDate": "2025-06-15",
        "ticketPrice": 580.00,
        "seatInfo": "A区1排1号",
        "comment": "很棒的演出",
        "createTime": "2025-01-10T10:00:00"
      }
    ]
  },
  "statusCounts": {
    "1": 30,
    "2": 70
  }
}
```

- `page`：分页数据
  - `total`：总记录数
  - `list`：数组，每项包含：
    - 演唱会基础信息（艺术家、演出名称、日期、场地、城市、类型、海报等）
    - 当前用户的观演状态、评分、观演日期、票价、座位、评价等
- `statusCounts`：各状态数量统计
  - `key`：状态值（1-想看，2-已看）
  - `value`：该状态下的记录数量

---

## 七、删除观演记录

- **URL**：`DELETE /api/concert/concerts/records/{id}`
- **说明**：
  - 仅删除指定用户的观演记录；
  - 不会删除全局的 `concert` 演唱会基础信息。

### 7.1 路径参数

- `id` (Long，必填)：观演记录 ID

### 7.2 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 八、错误码说明

| 错误码 | 说明 |
|--------|------|
| `1_002_001_001` | 演唱会不存在 |
| `1_002_001_002` | 该演唱会已存在，请勿重复添加 |
| `1_002_002_001` | 观演记录不存在 |
| `1_002_002_002` | 无权操作该观演记录 |
| `1_002_002_003` | 该演唱会已添加，请勿重复添加 |

---

## 九、业务规则说明

### 9.1 数据创建规则

1. **演唱会创建**：
   - 手动创建：必须提供 `artist`
   - 同一用户下，相同 `artist` + `title` + `concertDate` 的演唱会应该唯一（避免重复添加）
   - 如果演唱会已存在，则复用，只创建观演记录

2. **记录创建**：
   - 创建演唱会时自动创建观演记录
   - 默认状态为「想看」（watchStatus=1）
   - 用户ID从当前登录用户获取

### 9.2 数据更新规则

1. **权限控制**：
   - 只能更新自己的记录
   - 管理员可以管理所有记录（后续扩展）

2. **状态变更**：
   - 状态改为「已看」时，如果 `watchDate` 为空，自动设置为当前日期
   - 可以记录票价、座位等详细信息

3. **评分规则**：
   - 评分范围：0-10
   - 支持 0.5 步长（如 8.5）

### 9.3 数据查询规则

1. **权限控制**：
   - 只能查询自己的记录
   - 列表查询自动过滤已删除的记录

2. **排序规则**：
   - 默认按创建时间倒序
   - 支持按观演日期、评分排序

3. **筛选规则**：
   - 支持多维度筛选（状态、艺术家、城市、类型、标签、关键词）
   - 筛选条件可以组合使用

4. **统计信息**：
   - 分页列表接口会同时返回各状态的统计数量
   - 统计信息实时计算，反映当前筛选条件下的数据

---

## 十、后续扩展方向

### 10.1 API集成（后续扩展）

- 集成第三方演唱会信息API搜索演唱会
- 自动填充演唱会信息（海报、描述等）

### 10.2 AI标签生成（后续扩展）

- 为演唱会自动生成个性化标签
- 基于演唱会信息（艺术家、类型、描述）生成标签

### 10.3 照片关联（后续扩展）

- 与摄影模块关联，支持添加照片到观演记录
- 在观演记录中展示相关照片

---

## 十一、示例场景

### 场景1：手动创建演唱会记录

```http
POST /api/concert/concerts
Content-Type: application/json

{
  "artist": "The Beatles",
  "title": "Abbey Road Tour",
  "concertDate": "2025-06-15T20:00:00",
  "venue": "国家体育场",
  "city": "北京",
  "watchStatus": 1
}
```

### 场景2：更新观演状态和评分

```http
PUT /api/concert/concerts/records/1
Content-Type: application/json

{
  "watchStatus": 2,
  "personalRating": 8.5,
  "watchDate": "2025-06-15",
  "ticketPrice": 580.00,
  "seatInfo": "A区1排1号",
  "comment": "很棒的演出"
}
```

### 场景3：查询已看的演唱会列表

```http
GET /api/concert/concerts?pageNo=1&pageSize=10&status=2&sort=watchDate
```

### 场景4：更新演唱会基础信息

```http
PUT /api/concert/concerts/1
Content-Type: application/json

{
  "posterUrl": "https://example.com/new-poster.jpg",
  "description": "更新后的描述"
}
```

---

**文档版本**：V1.0  
**最后更新**：2026-01-15

