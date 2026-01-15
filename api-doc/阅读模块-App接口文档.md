## 阅读模块 App 接口文档（V1）

> 说明：本模块当前只支持手动添加图书，暂不考虑API集成（如豆瓣图书）。  
> 用户需要手动填写图书信息来创建记录。

---

## 一、通用说明

- **服务基础地址**：`http://{host}:{port}`
- **统一前缀**：`/api/books`
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

## 二、创建图书记录（含阅读记录）

- **URL**：`POST /api/books`
- **说明**：
  - 手动创建图书信息
  - 同时会为当前用户创建一条阅读记录
  - 如果图书已存在（相同 title + author），则复用图书，只创建阅读记录

### 2.1 请求体 `BookCreateReqVO`

```json
{
  "title": "百年孤独",
  "author": "加西亚·马尔克斯",
  "genre": "小说,魔幻现实主义",
  "description": "简介...",
  "coverUrl": "https://example.com/cover.jpg",
  "readStatus": 1,
  "personalRating": 9.0,
  "comment": "很经典"
}
```

- **字段说明**：
  - `title` (String，**必填**)：书名
  - `author` (String，可选)：作者（逗号分隔，如 `"加西亚·马尔克斯"` 或 `"作者1,作者2"`）
  - `genre` (String，可选)：类型（小说、历史等，逗号分隔），如 `"小说,魔幻现实主义"`
  - `description` (String，可选)：简介
  - `coverUrl` (String，可选)：封面URL
  - `readStatus` (Integer，可选)：阅读状态  
    - `1` 想读  
    - `2` 在读  
    - `3` 已读  
    - `4` 弃读  
    - 若不传，后端会使用默认值（通常为「想读」）
  - `personalRating` (BigDecimal，可选)：个人评分，范围 `0-10`，例如 `9.0`
  - `comment` (String，可选)：个人评价

> **推荐前端策略**：  
> - 必填字段：`title`  
> - 其他字段可在创建时填写，也可在后续管理页面补充

### 2.2 响应体 `BookCreateRespVO`

```json
{
  "bookId": 1,
  "recordId": 10
}
```

- `bookId` (Long)：新建/复用的图书主键 ID
- `recordId` (Long)：新建的阅读记录 ID

---

## 三、更新阅读记录

- **URL**：`PUT /api/books/records/{id}`
- **说明**：只更新阅读记录相关信息，不修改图书基础信息。

### 3.1 路径参数

- `id` (Long，必填)：阅读记录 ID

### 3.2 请求体 `BookRecordUpdateReqVO`

```json
{
  "readStatus": 3,
  "personalRating": 9.0,
  "readDate": "2025-01-15",
  "readProgress": 100.0,
  "comment": "重读依然很经典",
  "tags": "经典,文学",
  "coverUrl": "https://example.com/new-cover.jpg"
}
```

- 字段说明：
  - `readStatus` (Integer，可选)：阅读状态，1-4
  - `personalRating` (BigDecimal，可选)：0–10
  - `readDate` (LocalDate，可选)：阅读日期，格式 `yyyy-MM-dd`
  - `readProgress` (BigDecimal，可选)：阅读进度百分比，0–100，例如 `100.0`
  - `comment` (String，可选)：记录级别的评价
  - `tags` (String，可选)：标签（逗号分隔），会覆盖原有标签
  - `coverUrl` (String，可选)：封面URL（可选，用于更新封面）

> **业务规则**：
> - 状态改为「已读」时，如果 `readDate` 为空，后端会自动设置为当前日期
> - 所有字段都是可选的（部分更新）
> - 阅读进度可以随时更新，方便记录阅读进度

### 3.3 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 四、获取图书详情

- **URL**：`GET /api/books/{id}`

### 4.1 路径参数

- `id` (Long，必填)：图书 ID（`bookId`）

### 4.2 响应体 `BookRespVO`

```json
{
  "book": {
    "id": 1,
    "title": "百年孤独",
    "author": "加西亚·马尔克斯",
    "publisher": "南海出版公司",
    "publishYear": 2011,
    "isbn": "9787544253994",
    "genre": "小说,魔幻现实主义",
    "description": "简介...",
    "coverUrl": "https://example.com/cover.jpg",
    "pageCount": 360,
    "language": "中文"
  },
  "record": {
    "id": 1,
    "readStatus": 3,
    "personalRating": 9.0,
    "readDate": "2025-01-15",
    "readProgress": 100.0,
    "comment": "很经典",
    "tags": "经典,文学"
  }
}
```

- **图书基础信息** (`book`)：
  - `id`：图书 ID
  - `title`：书名
  - `author`：作者（逗号分隔）
  - `publisher`：出版社
  - `publishYear`：出版年份
  - `isbn`：ISBN
  - `genre`：类型（逗号分隔）
  - `description`：简介
  - `coverUrl`：封面 URL
  - `pageCount`：页数
  - `language`：语言
- **当前用户阅读记录** (`record`)：
  - `id`：记录 ID
  - `readStatus`：阅读状态（1-4）
  - `personalRating`：个人评分（0-10）
  - `readDate`：阅读日期
  - `readProgress`：阅读进度（0-100）
  - `comment`：评价
  - `tags`：标签（逗号分隔）

> 注：如果还未为当前用户创建记录，会返回错误。

---

## 五、图书分页列表（含阅读记录信息和统计）

- **URL**：`GET /api/books`
- **说明**：返回当前登录用户的图书 + 阅读记录分页列表，同时包含各状态的统计信息。

### 5.1 查询参数 `BookPageReqVO`

```text
pageNo           页码（从 1 开始，继承 PageParam）
pageSize         每页数量
readStatus       阅读状态：1 想读 / 2 在读 / 3 已读 / 4 弃读
author           作者筛选（精确匹配）
tag              标签筛选（模糊包含）
keyword          关键词（按书名模糊搜索）
startReadDate    开始阅读日期（yyyy-MM-dd）
endReadDate      结束阅读日期（yyyy-MM-dd）
sort             排序字段：ctime / readDate / rating
```

示例：

```http
GET /api/books?pageNo=1&pageSize=10&readStatus=3&author=马尔克斯&keyword=百年&sort=ctime
```

### 5.2 返回 `BookPageWithStatsRespVO`

```json
{
  "page": {
    "total": 100,
    "list": [
      {
        "recordId": 1,
        "bookId": 1,
        "title": "百年孤独",
        "author": "加西亚·马尔克斯",
        "genre": "小说,魔幻现实主义",
        "coverUrl": "https://example.com/cover.jpg",
        "readStatus": 3,
        "personalRating": 9.0,
        "readDate": "2025-01-15",
        "readProgress": 100.0,
        "tags": "经典,文学",
        "comment": "很经典",
        "createTime": "2025-01-10T10:00:00"
      }
    ]
  },
  "statusCounts": {
    "1": 20,
    "2": 10,
    "3": 60,
    "4": 10
  }
}
```

- `page`：分页数据
  - `total`：总记录数
  - `list`：数组，每项包含：
    - 图书基础信息（书名、作者、类型、封面等）
    - 当前用户的阅读状态、评分、阅读日期、阅读进度、标签等
- `statusCounts`：各状态数量统计
  - `key`：状态值（1-想读，2-在读，3-已读，4-弃读）
  - `value`：该状态下的记录数量

---

## 六、删除阅读记录

- **URL**：`DELETE /api/books/records/{id}`
- **说明**：
  - 仅删除指定用户的阅读记录；
  - 不会删除全局的 `book` 图书基础信息。

### 6.1 路径参数

- `id` (Long，必填)：阅读记录 ID

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
| `1_002_001_001` | 图书不存在 |
| `1_002_001_002` | 该图书已存在，请勿重复添加 |
| `1_002_002_001` | 阅读记录不存在 |
| `1_002_002_002` | 无权操作该阅读记录 |
| `1_002_002_003` | 该图书已添加，请勿重复添加 |

---

## 八、业务规则说明

### 8.1 数据创建规则

1. **图书创建**：
   - 手动创建：必须提供 `title`
   - 同一用户下，相同 `title` + `author` 的图书应该唯一（避免重复添加）
   - 如果图书已存在，则复用，只创建阅读记录

2. **记录创建**：
   - 创建图书时自动创建阅读记录
   - 默认状态为「想读」（readStatus=1）
   - 用户ID从当前登录用户获取

### 8.2 数据更新规则

1. **权限控制**：
   - 只能更新自己的记录
   - 管理员可以管理所有记录（后续扩展）

2. **状态变更**：
   - 状态改为「已读」时，如果 `readDate` 为空，自动设置为当前日期
   - 状态改为「弃读」时，可以记录原因（comment字段）

3. **评分规则**：
   - 评分范围：0-10
   - 支持 0.5 步长（如 9.5）

4. **阅读进度规则**：
   - 阅读进度范围：0-100
   - 可以随时更新阅读进度，方便记录阅读状态
   - 进度为 100 时通常表示已读完

### 8.3 数据查询规则

1. **权限控制**：
   - 只能查询自己的记录
   - 列表查询自动过滤已删除的记录

2. **排序规则**：
   - 默认按创建时间倒序
   - 支持按阅读日期、评分排序

3. **筛选规则**：
   - 支持多维度筛选（状态、作者、类型、标签、关键词）
   - 筛选条件可以组合使用

4. **统计信息**：
   - 分页列表接口会同时返回各状态的统计数量
   - 统计信息实时计算，反映当前筛选条件下的数据

---

## 九、后续扩展方向

### 9.1 API集成（后续扩展）

- 集成豆瓣图书API搜索图书
- 自动填充图书信息（封面、简介、ISBN、出版社等）

### 9.2 AI标签生成（后续扩展）

- 为图书自动生成个性化标签
- 基于图书信息（书名、作者、类型、简介）生成标签

### 9.3 阅读统计（后续扩展）

- 阅读时长统计
- 阅读速度分析
- 年度阅读报告

---

## 十、示例场景

### 场景1：手动创建图书记录

```http
POST /api/books
Content-Type: application/json

{
  "title": "百年孤独",
  "author": "加西亚·马尔克斯",
  "genre": "小说,魔幻现实主义",
  "readStatus": 1
}
```

### 场景2：更新阅读状态和进度

```http
PUT /api/books/records/1
Content-Type: application/json

{
  "readStatus": 2,
  "readProgress": 50.0,
  "comment": "正在阅读中"
}
```

### 场景3：标记为已读并评分

```http
PUT /api/books/records/1
Content-Type: application/json

{
  "readStatus": 3,
  "personalRating": 9.0,
  "readDate": "2025-01-15",
  "readProgress": 100.0,
  "comment": "很经典",
  "tags": "经典,文学"
}
```

### 场景4：查询已读的图书列表

```http
GET /api/books?pageNo=1&pageSize=10&readStatus=3&sort=readDate
```

### 场景5：按作者筛选图书

```http
GET /api/books?author=马尔克斯&pageNo=1&pageSize=10
```

---

**文档版本**：V1.0  
**最后更新**：2026-01-16

