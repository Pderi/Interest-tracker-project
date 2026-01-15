## 旅游模块 App 接口文档（V1）

> 说明：本模块当前只支持手动添加旅游地点，暂不考虑API集成（如地图API）。  
> 用户需要手动填写地点信息来创建记录。

---

## 一、通用说明

- **服务基础地址**：`http://{host}:{port}`
- **统一前缀**：`/api/travel/places`
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

## 二、创建旅游记录（含旅游记录）

- **URL**：`POST /api/travel/places`
- **说明**：
  - 手动创建旅游地点信息
  - 同时会为当前用户创建一条旅游记录
  - 如果地点已存在（相同 name + country + city），则复用地点，只创建旅游记录

### 2.1 请求体 `TravelCreateReqVO`

```json
{
  "name": "北京",
  "country": "中国",
  "city": "北京",
  "address": "北京市东城区天安门广场",
  "latitude": 39.9042,
  "longitude": 116.4074,
  "placeType": 1,
  "description": "首都北京...",
  "coverUrl": "https://example.com/cover.jpg",
  "travelStatus": 1,
  "personalRating": 8.5,
  "travelDate": "2025-06-15",
  "travelDuration": 3,
  "expense": 5000.00,
  "tags": "历史,文化",
  "comment": "很棒的旅行"
}
```

- **字段说明**：
  - `name` (String，**必填**)：地点名称
  - `country` (String，可选)：国家/地区
  - `city` (String，可选)：城市
  - `address` (String，可选)：详细地址
  - `latitude` (BigDecimal，可选)：纬度，例如 `39.9042`
  - `longitude` (BigDecimal，可选)：经度，例如 `116.4074`
  - `placeType` (Integer，可选)：地点类型  
    - `1` 城市  
    - `2` 景点  
    - `3` 国家  
    - `4` 其他
  - `description` (String，可选)：描述
  - `coverUrl` (String，可选)：封面URL
  - `travelStatus` (Integer，可选)：旅游状态  
    - `1` 想去  
    - `2` 计划中  
    - `3` 已去  
    - 若不传，后端会使用默认值（通常为「想去」）
  - `personalRating` (BigDecimal，可选)：个人评分，范围 `0-10`，例如 `8.5`
  - `travelDate` (LocalDate，可选)：旅游日期，格式 `yyyy-MM-dd`
  - `travelDuration` (Integer，可选)：旅游天数
  - `expense` (BigDecimal，可选)：费用
  - `tags` (String，可选)：标签，逗号分隔，如 `"历史,文化,古都"`
  - `comment` (String，可选)：个人评价

> **推荐前端策略**：  
> - 必填字段：`name`  
> - 其他字段可在创建时填写，也可在后续管理页面补充
> - 经纬度坐标可用于地图展示和定位

### 2.2 响应体 `TravelCreateRespVO`

```json
{
  "placeId": 1,
  "recordId": 10
}
```

- `placeId` (Long)：新建/复用的地点主键 ID
- `recordId` (Long)：新建的旅游记录 ID

---

## 三、更新旅游记录

- **URL**：`PUT /api/travel/places/records/{id}`
- **说明**：只更新旅游记录相关信息，不修改地点基础信息。

### 3.1 路径参数

- `id` (Long，必填)：旅游记录 ID

### 3.2 请求体 `TravelRecordUpdateReqVO`

```json
{
  "travelStatus": 3,
  "personalRating": 8.5,
  "travelDate": "2025-06-15",
  "travelDuration": 3,
  "expense": 5000.00,
  "comment": "重游依然很棒"
}
```

- 字段说明：
  - `travelStatus` (Integer，可选)：旅游状态，1-3
  - `personalRating` (BigDecimal，可选)：0–10
  - `travelDate` (LocalDate，可选)：旅游日期，格式 `yyyy-MM-dd`
  - `travelDuration` (Integer，可选)：旅游天数
  - `expense` (BigDecimal，可选)：费用
  - `comment` (String，可选)：记录级别的评价

> **业务规则**：
> - 状态改为「已去」时，如果 `travelDate` 为空，后端会自动设置为当前日期
> - 所有字段都是可选的（部分更新）
> - 可以记录旅游天数、费用等详细信息

### 3.3 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 四、更新旅游地点信息

- **URL**：`PUT /api/travel/places/{id}`
- **说明**：只更新旅游地点基础信息，不修改旅游记录。

### 4.1 路径参数

- `id` (Long，必填)：地点 ID

### 4.2 请求体 `TravelPlaceUpdateReqVO`

```json
{
  "name": "北京",
  "country": "中国",
  "city": "北京",
  "address": "北京市东城区天安门广场",
  "latitude": 39.9042,
  "longitude": 116.4074,
  "placeType": 1,
  "description": "更新后的描述",
  "coverUrl": "https://example.com/new-cover.jpg"
}
```

- 字段说明：
  - 所有字段均为可选，只更新传入的字段
  - `name` (String，可选)：地点名称
  - `country` (String，可选)：国家/地区
  - `city` (String，可选)：城市
  - `address` (String，可选)：详细地址
  - `latitude` (BigDecimal，可选)：纬度
  - `longitude` (BigDecimal，可选)：经度
  - `placeType` (Integer，可选)：地点类型（1-4）
  - `description` (String，可选)：描述
  - `coverUrl` (String，可选)：封面URL

### 4.3 响应

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 五、获取旅游详情

- **URL**：`GET /api/travel/places/{id}`

### 5.1 路径参数

- `id` (Long，必填)：地点 ID（`placeId`）

### 5.2 响应体 `TravelRespVO`

```json
{
  "place": {
    "id": 1,
    "name": "北京",
    "country": "中国",
    "city": "北京",
    "address": "北京市东城区天安门广场",
    "latitude": 39.9042,
    "longitude": 116.4074,
    "placeType": 1,
    "description": "首都北京...",
    "coverUrl": "https://example.com/cover.jpg"
  },
  "record": {
    "id": 1,
    "travelStatus": 3,
    "personalRating": 8.5,
    "travelDate": "2025-06-15",
    "travelDuration": 3,
    "expense": 5000.00,
    "comment": "很棒的旅行",
    "tags": "历史,文化"
  }
}
```

- **地点基础信息** (`place`)：
  - `id`：地点 ID
  - `name`：地点名称
  - `country`：国家/地区
  - `city`：城市
  - `address`：详细地址
  - `latitude`：纬度
  - `longitude`：经度
  - `placeType`：地点类型（1-4）
  - `description`：描述
  - `coverUrl`：封面 URL
- **当前用户旅游记录** (`record`)：
  - `id`：记录 ID
  - `travelStatus`：旅游状态（1-3）
  - `personalRating`：个人评分（0-10）
  - `travelDate`：旅游日期
  - `travelDuration`：旅游天数
  - `expense`：费用
  - `comment`：评价
  - `tags`：标签（逗号分隔）

> 注：如果还未为当前用户创建记录，会返回错误。

---

## 六、旅游分页列表（含旅游记录信息和统计）

- **URL**：`GET /api/travel/places`
- **说明**：返回当前登录用户的旅游地点 + 旅游记录分页列表，同时包含各状态的统计信息。

### 6.1 查询参数 `TravelPageReqVO`

```text
pageNo           页码（从 1 开始，继承 PageParam）
pageSize         每页数量
status           旅游状态：1 想去 / 2 计划中 / 3 已去
country          国家筛选（精确匹配）
city             城市筛选（精确匹配）
placeType        地点类型筛选：1 城市 / 2 景点 / 3 国家 / 4 其他
tag              标签筛选（模糊包含）
keyword          关键词（按地点名称、城市、国家模糊搜索）
startTravelDate  开始旅游日期（yyyy-MM-dd）
endTravelDate    结束旅游日期（yyyy-MM-dd）
sort             排序字段：ctime / travelDate / rating
```

示例：

```http
GET /api/travel/places?pageNo=1&pageSize=10&status=3&country=中国&keyword=北京&sort=travelDate
```

### 6.2 返回 `TravelPageWithStatsRespVO`

```json
{
  "page": {
    "total": 100,
    "list": [
      {
        "recordId": 1,
        "placeId": 1,
        "name": "北京",
        "country": "中国",
        "city": "北京",
        "address": "北京市东城区天安门广场",
        "placeType": 1,
        "coverUrl": "https://example.com/cover.jpg",
        "travelStatus": 3,
        "personalRating": 8.5,
        "travelDate": "2025-06-15",
        "travelDuration": 3,
        "expense": 5000.00,
        "comment": "很棒的旅行",
        "createTime": "2025-01-10T10:00:00"
      }
    ]
  },
  "statusCounts": {
    "1": 30,
    "2": 20,
    "3": 50
  }
}
```

- `page`：分页数据
  - `total`：总记录数
  - `list`：数组，每项包含：
    - 地点基础信息（名称、国家、城市、地址、类型、封面等）
    - 当前用户的旅游状态、评分、旅游日期、天数、费用、评价等
- `statusCounts`：各状态数量统计
  - `key`：状态值（1-想去，2-计划中，3-已去）
  - `value`：该状态下的记录数量

---

## 七、删除旅游记录

- **URL**：`DELETE /api/travel/places/records/{id}`
- **说明**：
  - 仅删除指定用户的旅游记录；
  - 不会删除全局的 `place` 地点基础信息。

### 7.1 路径参数

- `id` (Long，必填)：旅游记录 ID

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
| `1_002_001_001` | 地点不存在 |
| `1_002_001_002` | 该地点已存在，请勿重复添加 |
| `1_002_002_001` | 旅游记录不存在 |
| `1_002_002_002` | 无权操作该旅游记录 |
| `1_002_002_003` | 该地点已添加，请勿重复添加 |

---

## 九、业务规则说明

### 9.1 数据创建规则

1. **地点创建**：
   - 手动创建：必须提供 `name`
   - 同一用户下，相同 `name` + `country` + `city` 的地点应该唯一（避免重复添加）
   - 如果地点已存在，则复用，只创建旅游记录

2. **记录创建**：
   - 创建地点时自动创建旅游记录
   - 默认状态为「想去」（travelStatus=1）
   - 用户ID从当前登录用户获取

### 9.2 数据更新规则

1. **权限控制**：
   - 只能更新自己的记录
   - 管理员可以管理所有记录（后续扩展）

2. **状态变更**：
   - 状态改为「已去」时，如果 `travelDate` 为空，自动设置为当前日期
   - 可以记录旅游天数、费用等详细信息

3. **评分规则**：
   - 评分范围：0-10
   - 支持 0.5 步长（如 8.5）

4. **坐标信息**：
   - 经纬度坐标可用于地图展示和定位
   - 支持通过地图选择或手动输入坐标

### 9.3 数据查询规则

1. **权限控制**：
   - 只能查询自己的记录
   - 列表查询自动过滤已删除的记录

2. **排序规则**：
   - 默认按创建时间倒序
   - 支持按旅游日期、评分排序

3. **筛选规则**：
   - 支持多维度筛选（状态、国家、城市、类型、标签、关键词）
   - 筛选条件可以组合使用

4. **统计信息**：
   - 分页列表接口会同时返回各状态的统计数量
   - 统计信息实时计算，反映当前筛选条件下的数据

---

## 十、后续扩展方向

### 10.1 API集成（后续扩展）

- 集成地图API搜索地点
- 自动填充地点信息（坐标、描述、封面等）

### 10.2 AI标签生成（后续扩展）

- 为旅游地点自动生成个性化标签
- 基于地点信息（名称、类型、描述）生成标签

### 10.3 照片关联（后续扩展）

- 与摄影模块关联，支持添加照片到旅游记录
- 在旅游记录中展示相关照片

### 10.4 地图展示（后续扩展）

- 在地图上展示已去的地点
- 支持按国家、城市分组展示

---

## 十一、示例场景

### 场景1：手动创建旅游记录

```http
POST /api/travel/places
Content-Type: application/json

{
  "name": "北京",
  "country": "中国",
  "city": "北京",
  "placeType": 1,
  "travelStatus": 1
}
```

### 场景2：更新旅游状态和详细信息

```http
PUT /api/travel/places/records/1
Content-Type: application/json

{
  "travelStatus": 3,
  "personalRating": 8.5,
  "travelDate": "2025-06-15",
  "travelDuration": 3,
  "expense": 5000.00,
  "comment": "很棒的旅行",
  "tags": "历史,文化"
}
```

### 场景3：更新地点坐标信息

```http
PUT /api/travel/places/1
Content-Type: application/json

{
  "latitude": 39.9042,
  "longitude": 116.4074,
  "address": "北京市东城区天安门广场"
}
```

### 场景4：查询已去的旅游地点列表

```http
GET /api/travel/places?pageNo=1&pageSize=10&status=3&sort=travelDate
```

### 场景5：按国家筛选旅游地点

```http
GET /api/travel/places?country=中国&pageNo=1&pageSize=10
```

### 场景6：查询计划中的旅游地点

```http
GET /api/travel/places?status=2&pageNo=1&pageSize=10
```

---

**文档版本**：V1.0  
**最后更新**：2026-01-16

