## 摄影模块 App 接口文档（V1）

> 说明：本模块提供照片上传、管理、分类等功能，支持与旅游模块、演唱会模块的关联。

---

## 一、通用说明

- **服务基础地址**：`http://{host}:{port}`
- **统一前缀**：`/api/photos`
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

## 二、照片管理接口

### 2.1 上传照片

- **URL**：`POST /api/photos/upload`
- **说明**：上传单张照片，支持自动生成缩略图，可关联到旅游记录或观演记录

#### 请求参数

**Content-Type**: `multipart/form-data`

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | MultipartFile | 是 | 照片文件（支持 jpg、png、gif、webp，最大10MB） |
| title | String | 否 | 照片标题 |
| description | String | 否 | 照片描述 |
| tags | String | 否 | 标签（逗号分隔），如：`"风景,自然"` |
| categoryId | Long | 否 | 分类ID |
| travelRecordId | Long | 否 | 关联的旅游记录ID |
| concertRecordId | Long | 否 | 关联的观演记录ID |
| location | String | 否 | 拍摄地点 |

#### 请求示例

```bash
curl -X POST "http://localhost:8080/api/photos/upload" \
  -H "Authorization: Bearer {token}" \
  -F "file=@photo.jpg" \
  -F "title=美丽的风景" \
  -F "description=拍摄于2025年1月" \
  -F "tags=风景,自然" \
  -F "categoryId=1" \
  -F "location=北京"
```

#### 响应体 `PhotoUploadRespVO`

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "filePath": "https://interest-tracker-1353400836.cos.ap-shanghai.myqcloud.com/photos/1/2025/01/15/uuid_photo.jpg",
    "thumbnailPath": "https://interest-tracker-1353400836.cos.ap-shanghai.myqcloud.com/thumbnails/1/2025/01/15/uuid_photo.jpg",
    "fileSize": 1024000,
    "width": 1920,
    "height": 1080,
    "createTime": "2025-01-15T10:30:00"
  }
}
```

**字段说明**：
- `id` (Long)：照片ID
- `filePath` (String)：原图访问URL（COS地址）
- `thumbnailPath` (String)：缩略图访问URL（如果原图大于800x800会自动生成）
- `fileSize` (Long)：文件大小（字节）
- `width` (Integer)：图片宽度（像素）
- `height` (Integer)：图片高度（像素）
- `createTime` (LocalDateTime)：创建时间

---

### 2.2 批量上传照片

- **URL**：`POST /api/photos/batch-upload`
- **说明**：批量上传多张照片，适用于一次选择多张照片的场景

#### 请求参数

**Content-Type**: `multipart/form-data`

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| files | MultipartFile[] | 是 | 照片文件数组 |
| categoryId | Long | 否 | 分类ID（应用到所有照片） |
| travelRecordId | Long | 否 | 关联的旅游记录ID（应用到所有照片） |
| concertRecordId | Long | 否 | 关联的观演记录ID（应用到所有照片） |

#### 请求示例

```bash
curl -X POST "http://localhost:8080/api/photos/batch-upload" \
  -H "Authorization: Bearer {token}" \
  -F "files=@photo1.jpg" \
  -F "files=@photo2.jpg" \
  -F "files=@photo3.jpg" \
  -F "categoryId=1"
```

#### 响应体

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "filePath": "...",
      "thumbnailPath": "...",
      ...
    },
    {
      "id": 2,
      "filePath": "...",
      "thumbnailPath": "...",
      ...
    }
  ]
}
```

---

### 2.3 获取照片详情

- **URL**：`GET /api/photos/{id}`
- **说明**：获取照片的详细信息，访问时会自动增加查看次数

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 照片ID |

#### 响应体 `PhotoRespVO`

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "title": "美丽的风景",
    "description": "拍摄于2025年1月",
    "filePath": "https://...",
    "thumbnailPath": "https://...",
    "fileSize": 1024000,
    "width": 1920,
    "height": 1080,
    "mimeType": "image/jpeg",
    "exifData": null,
    "location": "北京",
    "tags": "风景,自然",
    "category": "风景",
    "categoryId": 1,
    "shootTime": "2025-01-15T10:30:00",
    "travelRecordId": null,
    "concertRecordId": null,
    "viewCount": 10,
    "likeCount": 5,
    "createTime": "2025-01-15T10:30:00",
    "updateTime": "2025-01-15T10:30:00"
  }
}
```

---

### 2.4 更新照片信息

- **URL**：`PUT /api/photos/{id}`
- **说明**：更新照片的标题、描述、标签、分类、关联记录等信息

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 照片ID |

#### 请求体 `PhotoUpdateReqVO`

```json
{
  "title": "更新后的标题",
  "description": "更新后的描述",
  "tags": "新标签1,新标签2",
  "categoryId": 2,
  "travelRecordId": 1,
  "concertRecordId": null,
  "location": "上海"
}
```

**字段说明**：
- 所有字段均为可选，只更新传入的字段
- `travelRecordId` 和 `concertRecordId` 可以设置为 `null` 来取消关联

#### 响应体

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

### 2.5 删除照片

- **URL**：`DELETE /api/photos/{id}`
- **说明**：删除照片，同时会删除COS中的原图和缩略图文件

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 照片ID |

#### 响应体

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

### 2.6 获取照片列表（分页）

- **URL**：`GET /api/photos`
- **说明**：分页查询照片列表，支持多种筛选条件

#### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNo | Integer | 否 | 页码，从1开始，默认1 |
| pageSize | Integer | 否 | 每页条数，最大100，默认10 |
| categoryId | Long | 否 | 分类ID筛选 |
| travelRecordId | Long | 否 | 旅游记录ID筛选（查询关联到特定旅游记录的照片） |
| concertRecordId | Long | 否 | 观演记录ID筛选（查询关联到特定观演记录的照片） |
| unlinkedOnly | Boolean | 否 | 是否只查询未关联的照片（`true` 表示只查询未关联到旅游或观演记录的照片） |
| shootTimeStart | LocalDateTime | 否 | 拍摄时间范围（开始），格式：`2025-01-01T00:00:00` |
| shootTimeEnd | LocalDateTime | 否 | 拍摄时间范围（结束），格式：`2025-12-31T23:59:59` |
| keyword | String | 否 | 关键词搜索（搜索标题、描述、标签） |

#### 请求示例

```bash
# 查询所有照片
GET /api/photos?pageNo=1&pageSize=20

# 按分类筛选
GET /api/photos?categoryId=1&pageNo=1&pageSize=20

# 查询关联到特定旅游记录的照片
GET /api/photos?travelRecordId=1

# 查询未关联的照片
GET /api/photos?unlinkedOnly=true

# 按时间范围筛选
GET /api/photos?shootTimeStart=2025-01-01T00:00:00&shootTimeEnd=2025-12-31T23:59:59

# 关键词搜索
GET /api/photos?keyword=风景
```

#### 响应体 `PageResult<PhotoPageRespVO>`

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "美丽的风景",
        "filePath": "https://...",
        "thumbnailPath": "https://...",
        "category": "风景",
        "categoryId": 1,
        "shootTime": "2025-01-15T10:30:00",
        "location": "北京",
        "tags": "风景,自然",
        "viewCount": 10,
        "likeCount": 5,
        "createTime": "2025-01-15T10:30:00"
      },
      ...
    ],
    "total": 100
  }
}
```

---

## 三、照片分类管理接口

### 3.1 创建分类

- **URL**：`POST /api/photos/categories`
- **说明**：创建新的照片分类，每个用户最多创建20个分类

#### 请求体 `PhotoCategoryCreateReqVO`

```json
{
  "name": "风景",
  "color": "#FF5733",
  "icon": "camera",
  "description": "记录自然风景照片",
  "sortOrder": 0
}
```

**字段说明**：
- `name` (String，必填)：分类名称，最大长度64
- `color` (String，可选)：分类颜色（十六进制），如：`#FF5733`，最大长度16
- `icon` (String，可选)：分类图标，如：`camera`、`nature` 等，最大长度64
- `description` (String，可选)：分类描述，最大长度255
- `sortOrder` (Integer，可选)：排序顺序，数字越小越靠前，如果不传会自动设置为最大值+1

#### 响应体 `PhotoCategoryRespVO`

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "风景",
    "color": "#FF5733",
    "icon": "camera",
    "description": "记录自然风景照片",
    "sortOrder": 0,
    "photoCount": 0,
    "createTime": "2025-01-15T10:30:00",
    "updateTime": "2025-01-15T10:30:00"
  }
}
```

---

### 3.2 更新分类

- **URL**：`PUT /api/photos/categories/{id}`
- **说明**：更新分类信息

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

#### 请求体 `PhotoCategoryUpdateReqVO`

```json
{
  "name": "更新后的分类名",
  "color": "#00FF00",
  "icon": "nature",
  "description": "更新后的描述",
  "sortOrder": 1
}
```

**字段说明**：
- 所有字段均为可选，只更新传入的字段
- 如果更新分类名称，系统会检查新名称是否已存在

#### 响应体

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

### 3.3 删除分类

- **URL**：`DELETE /api/photos/categories/{id}`
- **说明**：删除分类，如果该分类下还有照片则无法删除

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

#### 响应体

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

**错误情况**：
- 如果分类下还有照片，会返回错误：`该分类下还有照片，无法删除`

---

### 3.4 获取分类详情

- **URL**：`GET /api/photos/categories/{id}`
- **说明**：获取分类的详细信息

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID |

#### 响应体 `PhotoCategoryRespVO`

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "风景",
    "color": "#FF5733",
    "icon": "camera",
    "description": "记录自然风景照片",
    "sortOrder": 0,
    "photoCount": 10,
    "createTime": "2025-01-15T10:30:00",
    "updateTime": "2025-01-15T10:30:00"
  }
}
```

---

### 3.5 获取用户的所有分类列表

- **URL**：`GET /api/photos/categories`
- **说明**：获取当前用户创建的所有分类，按排序顺序返回

#### 响应体

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "风景",
      "color": "#FF5733",
      "icon": "camera",
      "description": "记录自然风景照片",
      "sortOrder": 0,
      "photoCount": 10,
      "createTime": "2025-01-15T10:30:00",
      "updateTime": "2025-01-15T10:30:00"
    },
    {
      "id": 2,
      "name": "人物",
      "color": "#00FF00",
      "icon": "person",
      "description": "人物照片",
      "sortOrder": 1,
      "photoCount": 5,
      "createTime": "2025-01-15T11:00:00",
      "updateTime": "2025-01-15T11:00:00"
    }
  ]
}
```

---

### 3.6 更新分类排序

- **URL**：`PUT /api/photos/categories/sort`
- **说明**：批量更新分类的排序顺序

#### 请求体

```json
[1, 3, 2, 4]
```

**说明**：
- 传入分类ID数组，数组的顺序即为新的排序顺序
- 例如：`[1, 3, 2, 4]` 表示ID为1的分类排第一，ID为3的分类排第二，以此类推

#### 响应体

```json
{
  "code": 0,
  "msg": "success",
  "data": true
}
```

---

## 四、错误码说明

| 错误码 | 说明 |
|--------|------|
| 1_002_001_001 | 照片不存在 |
| 1_002_001_002 | 无权操作该照片 |
| 1_002_001_003 | 照片文件过大（超过10MB） |
| 1_002_001_004 | 照片文件类型不支持（仅支持 jpg、png、gif、webp） |
| 1_002_001_005 | 照片上传失败 |
| 1_002_002_001 | 照片分类不存在 |
| 1_002_002_002 | 无权操作该分类 |
| 1_002_002_003 | 分类名称已存在 |
| 1_002_002_004 | 分类数量已达上限（最多20个） |
| 1_002_002_005 | 该分类下还有照片，无法删除 |

---

## 五、功能特性说明

### 5.1 文件上传

- **支持格式**：jpg、jpeg、png、gif、webp
- **文件大小限制**：最大10MB
- **自动处理**：
  - 自动生成缩略图（如果原图大于800x800像素）
  - 自动提取图片尺寸信息（宽度、高度）
  - 自动保存文件大小和MIME类型

### 5.2 跨模块关联

照片可以关联到以下模块：
- **旅游模块**：通过 `travelRecordId` 关联到旅游记录
- **演唱会模块**：通过 `concertRecordId` 关联到观演记录

关联后可以通过以下方式查询：
- 在照片列表接口中使用 `travelRecordId` 或 `concertRecordId` 筛选
- 在旅游/演唱会模块中调用照片服务的相关方法获取关联照片

### 5.3 分类管理

- **数量限制**：每个用户最多创建20个分类
- **名称唯一性**：同一用户的分类名称不能重复
- **删除限制**：如果分类下还有照片，无法删除分类
- **自动统计**：分类下的照片数量会自动更新

### 5.4 文件存储

- **存储方式**：使用腾讯云COS对象存储
- **文件路径规则**：`photos/{userId}/{yyyy/MM/dd}/{uuid}_{originalFileName}`
- **缩略图路径规则**：`thumbnails/{userId}/{yyyy/MM/dd}/{uuid}_{originalFileName}`
- **访问方式**：通过COS域名直接访问，支持CDN加速

---

## 六、使用示例

### 6.1 完整上传流程

```javascript
// 1. 创建分类
const category = await fetch('/api/photos/categories', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: '风景',
    color: '#FF5733',
    icon: 'camera'
  })
});

// 2. 上传照片
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('title', '美丽的风景');
formData.append('categoryId', category.data.id);
formData.append('tags', '风景,自然');
formData.append('location', '北京');

const photo = await fetch('/api/photos/upload', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token
  },
  body: formData
});

// 3. 查询照片列表
const photos = await fetch('/api/photos?categoryId=1&pageNo=1&pageSize=20', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
});
```

### 6.2 关联照片到旅游记录

```javascript
// 上传照片时直接关联
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('travelRecordId', travelRecordId);

await fetch('/api/photos/upload', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token
  },
  body: formData
});

// 或者更新已有照片
await fetch(`/api/photos/${photoId}`, {
  method: 'PUT',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    travelRecordId: travelRecordId
  })
});
```

---

## 七、注意事项

1. **文件上传**：
   - 必须使用 `multipart/form-data` 格式
   - 文件大小不能超过10MB
   - 仅支持图片格式

2. **权限控制**：
   - 用户只能操作自己创建的照片和分类
   - 系统会自动从Token中获取用户ID

3. **关联记录**：
   - 照片可以同时关联到旅游记录和观演记录
   - 可以通过设置 `null` 来取消关联

4. **分类限制**：
   - 每个用户最多创建20个分类
   - 分类名称在同一用户下必须唯一
   - 删除分类前需要先移除该分类下的所有照片

5. **缩略图**：
   - 如果原图尺寸小于800x800，不会生成缩略图
   - 缩略图生成失败不会影响原图上传

---

## 八、更新日志

### V1.0.0 (2025-01-15)
- 初始版本
- 支持照片上传、查询、更新、删除
- 支持照片分类管理
- 支持与旅游模块、演唱会模块的关联
- 支持自动生成缩略图

