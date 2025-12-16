# AI 开发提示词指南

## 文档信息
- 文档名称：AI 开发提示词指南
- 版本号：v1.0.0
- 创建日期：2025-12-16
- 用途：为后端开发提供 AI 使用建议和提示词模板，提升开发效率和代码质量

---

## 目录

- [1. AI 辅助开发最佳实践](#1-ai-辅助开发最佳实践)
- [2. 代码生成类提示词](#2-代码生成类提示词)
- [3. 代码重构类提示词](#3-代码重构类提示词)
- [4. 问题调试类提示词](#4-问题调试类提示词)
- [5. 代码审查类提示词](#5-代码审查类提示词)
- [6. 文档生成类提示词](#6-文档生成类提示词)
- [7. 测试代码生成](#7-测试代码生成)
- [8. 性能优化提示词](#8-性能优化提示词)
- [9. 安全相关提示词](#9-安全相关提示词)
- [10. 项目特定场景提示词](#10-项目特定场景提示词)
- [11. 提示词使用技巧](#11-提示词使用技巧)
- [12. 常见问题与解决方案](#12-常见问题与解决方案)

---

## 1. AI 辅助开发最佳实践

### 1.1 提供充分的上下文

**✅ 好的做法**：
```
请根据项目的架构规范，创建一个用户管理模块的 CRUD 功能。
项目使用 Spring Boot + MyBatis Plus，遵循以下规范：
- Controller 层使用 @RestController，路径为 /admin/user
- Service 层使用 @Service，实现接口和实现类分离
- DO 类继承 BaseDO，使用 @TableName 指定表名
- VO 类分为 AddReqVO、UpdateReqVO、RespVO、PageReqVO、PageRespVO
- 使用 BeanUtils.toBean() 进行对象转换
- 错误码定义在 ErrorCodeConstants 中
```

**❌ 不好的做法**：
```
创建一个用户管理的 CRUD 功能
```

### 1.2 明确技术栈和版本

**✅ 好的做法**：
```
使用 Spring Boot 3.x、MyBatis Plus 3.5.x、Java 17，
创建一个照片管理模块，包含以下功能：
- 照片上传（支持多文件）
- 照片列表查询（分页、按标签筛选）
- 照片详情查询
- 照片删除
```

**❌ 不好的做法**：
```
创建一个照片管理功能
```

### 1.3 分步骤提出需求

**✅ 好的做法**：
```
第一步：创建 PhotoDO 类，包含字段：id、url、title、tags、createTime
第二步：创建 PhotoMapper 接口，实现分页查询方法
第三步：创建 5 个 VO 类（AddReqVO、UpdateReqVO、RespVO、PageReqVO、PageRespVO）
第四步：创建 PhotoService 接口和实现类
第五步：创建 PhotoController，实现 CRUD 接口
```

**❌ 不好的做法**：
```
一次性创建完整的照片管理模块，包括所有类和方法
```

### 1.4 引用现有代码作为参考

**✅ 好的做法**：
```
参考项目中的 ArtWorkController 和 ArtWorkService 的实现方式，
创建一个类似的 MovieController 和 MovieService，
但需要添加评分字段（rating）和观看状态字段（watchStatus）
```

**❌ 不好的做法**：
```
创建一个电影管理功能
```

---

## 2. 代码生成类提示词

### 2.1 创建 DO 类

#### 标准 DO 类生成
```
根据以下数据库表结构，创建对应的 DO 类：

表名：interest_photo
字段：
- id BIGINT PRIMARY KEY AUTO_INCREMENT
- url VARCHAR(500) NOT NULL COMMENT '照片URL'
- title VARCHAR(200) COMMENT '标题'
- description TEXT COMMENT '描述'
- tags VARCHAR(500) COMMENT '标签，逗号分隔'
- create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
- update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- deleted TINYINT NOT NULL DEFAULT 0

要求：
1. 继承 BaseDO
2. 使用 @TableName("interest_photo")
3. 主键使用 @TableId(type = IdType.AUTO)
4. 添加完整的字段注释
5. 使用 Lombok 注解（@Data、@EqualsAndHashCode、@ToString）
```

#### 带枚举的 DO 类
```
创建 PhotoDO 类，包含以下字段：
- id: Long（主键）
- url: String（照片URL）
- status: Integer（状态，使用 PhotoStatusEnum 枚举）
- type: Integer（类型，使用 PhotoTypeEnum 枚举）

在 DO 类的 status 和 type 字段注释中引用对应的枚举类。
```

### 2.2 创建 Mapper 接口

#### 标准 Mapper 生成
```
创建 PhotoMapper 接口，继承 BaseMapperX<PhotoDO>，包含以下方法：

1. selectPage(PhotoPageReqVO reqVO): PageResult<PhotoDO>
   - 支持按 title 模糊查询（likeIfPresent）
   - 支持按 tags 查询（likeIfPresent）
   - 支持按 createTime 范围查询（betweenIfPresent）
   - 按 createTime 倒序排列

2. selectByTags(List<String> tags): List<PhotoDO>
   - 查询包含指定标签的照片
   - 使用 IN 查询

要求：
- 使用 LambdaQueryWrapperX 构建查询条件
- 所有查询条件使用 xxxIfPresent 方法避免空值
- 添加方法注释
```

#### 复杂查询 Mapper
```
创建 PhotoMapper 接口，包含以下复杂查询：

1. selectPageWithUser(PhotoPageReqVO reqVO): PageResult<PhotoRespVO>
   - 分页查询照片，同时关联查询用户信息
   - 使用 LEFT JOIN 关联 user 表
   - 返回的 VO 中包含 userName 字段

2. selectStatistics(Long userId): PhotoStatisticsVO
   - 统计用户照片数量、总大小、按类型分组统计
   - 使用聚合函数 COUNT、SUM、GROUP BY

要求：
- 复杂查询使用 @Select 注解编写 SQL
- 简单查询使用 LambdaQueryWrapperX
```

### 2.3 创建 VO 类

#### 完整的 5 个 VO 类生成
```
创建照片管理模块的 5 个 VO 类：

1. PhotoAddReqVO（新增请求）
   - url: String（必填，最大长度500）
   - title: String（可选，最大长度200）
   - description: String（可选）
   - tags: List<String>（可选，标签列表）

2. PhotoUpdateReqVO（更新请求）
   - id: Long（必填）
   - title: String（可选）
   - description: String（可选）
   - tags: List<String>（可选）

3. PhotoRespVO（详情响应）
   - id: Long
   - url: String
   - title: String
   - description: String
   - tags: List<String>
   - createTime: LocalDateTime

4. PhotoPageReqVO（分页查询请求）
   - title: String（可选，模糊查询）
   - tags: String（可选，标签查询）
   - beginTime: LocalDateTime（可选，开始时间）
   - endTime: LocalDateTime（可选，结束时间）
   - 继承 PageParam

5. PhotoPageRespVO（分页响应）
   - id: Long
   - url: String
   - title: String
   - tags: List<String>
   - createTime: LocalDateTime

要求：
- 所有字段添加 @Schema 注解
- 必填字段使用 requiredMode = REQUIRED
- 提供示例值（example）
- 添加类和方法注释
```

### 2.4 创建 Service 层

#### Service 接口生成
```
创建 PhotoService 接口，包含以下方法：

1. Long createPhoto(PhotoAddReqVO reqVO)
   - 创建照片记录
   - 返回照片ID

2. void updatePhoto(PhotoUpdateReqVO reqVO)
   - 更新照片信息

3. void deletePhoto(Long id)
   - 删除照片（逻辑删除）

4. PhotoRespVO getPhoto(Long id)
   - 获取照片详情

5. PageResult<PhotoPageRespVO> getPhotoPage(PhotoPageReqVO reqVO)
   - 分页查询照片列表

要求：
- 方法添加 JavaDoc 注释
- 参数和返回值类型明确
```

#### Service 实现类生成
```
实现 PhotoServiceImpl 类，要求：

1. 实现 PhotoService 接口的所有方法
2. 使用 @Service、@Validated、@Slf4j 注解
3. 注入 PhotoMapper
4. 所有对象转换使用 BeanUtils.toBean()
5. 删除和更新操作前必须调用 validatePhotoExists() 校验存在性
6. 创建和更新操作使用 @Transactional 注解
7. 使用统一的异常处理：throw exception(PHOTO_NOT_EXISTS)

参考项目中的 ArtWorkServiceImpl 的实现风格。
```

### 2.5 创建 Controller 层

#### 标准 Controller 生成
```
创建 PhotoController，实现照片管理的 RESTful API：

路径：/admin/photo
权限前缀：photo

接口：
1. POST /admin/photo - 创建照片
   - 权限：photo:create
   - 参数：@RequestBody PhotoAddReqVO
   - 返回：CommonResult<Long>

2. PUT /admin/photo - 更新照片
   - 权限：photo:update
   - 参数：@RequestBody PhotoUpdateReqVO
   - 返回：CommonResult<Boolean>

3. DELETE /admin/photo - 删除照片
   - 权限：photo:delete
   - 参数：@RequestParam Long id
   - 返回：CommonResult<Boolean>

4. GET /admin/photo - 获取照片详情
   - 权限：photo:get
   - 参数：@RequestParam Long id
   - 返回：CommonResult<PhotoRespVO>

5. GET /admin/photo/page - 分页查询照片
   - 权限：photo:get
   - 参数：PhotoPageReqVO（继承 PageParam）
   - 返回：CommonResult<PageResult<PhotoPageRespVO>>

要求：
- 使用 @Tag、@Operation 注解生成 API 文档
- 使用 @PreAuthorize 进行权限控制
- 使用 @Valid 进行参数校验
- 统一返回 CommonResult
```

---

## 3. 代码重构类提示词

### 3.1 提取公共方法

```
分析以下 Service 方法，提取公共的校验逻辑：

```java
public void createPhoto(PhotoAddReqVO reqVO) {
    PhotoDO photoDO = BeanUtils.toBean(reqVO, PhotoDO.class);
    // 校验 URL 格式
    if (!isValidUrl(photoDO.getUrl())) {
        throw exception(PHOTO_URL_INVALID);
    }
    // 校验标题长度
    if (photoDO.getTitle() != null && photoDO.getTitle().length() > 200) {
        throw exception(PHOTO_TITLE_TOO_LONG);
    }
    photoMapper.insert(photoDO);
}

public void updatePhoto(PhotoUpdateReqVO reqVO) {
    validatePhotoExists(reqVO.getId());
    PhotoDO photoDO = BeanUtils.toBean(reqVO, PhotoDO.class);
    // 校验 URL 格式
    if (photoDO.getUrl() != null && !isValidUrl(photoDO.getUrl())) {
        throw exception(PHOTO_URL_INVALID);
    }
    // 校验标题长度
    if (photoDO.getTitle() != null && photoDO.getTitle().length() > 200) {
        throw exception(PHOTO_TITLE_TOO_LONG);
    }
    photoMapper.updateById(photoDO);
}
```

要求：
1. 提取公共的校验方法 validatePhoto(PhotoDO photoDO)
2. 在两个方法中复用
3. 保持代码简洁
```

### 3.2 优化查询性能

```
优化以下查询方法，解决 N+1 问题：

```java
public List<PhotoRespVO> getPhotoList(List<Long> ids) {
    List<PhotoRespVO> result = new ArrayList<>();
    for (Long id : ids) {
        PhotoDO photoDO = photoMapper.selectById(id);
        UserDO user = userMapper.selectById(photoDO.getUserId());
        PhotoRespVO vo = BeanUtils.toBean(photoDO, PhotoRespVO.class);
        vo.setUserName(user.getName());
        result.add(vo);
    }
    return result;
}
```

要求：
1. 使用批量查询替代循环查询
2. 减少数据库查询次数
3. 保持功能不变
```

### 3.3 统一异常处理

```
重构以下代码，使用统一的异常处理方式：

```java
public PhotoRespVO getPhoto(Long id) {
    PhotoDO photoDO = photoMapper.selectById(id);
    if (photoDO == null) {
        throw new RuntimeException("照片不存在");
    }
    return BeanUtils.toBean(photoDO, PhotoRespVO.class);
}
```

要求：
1. 使用项目统一的异常处理方式
2. 使用 ErrorCodeConstants 中定义的错误码
3. 参考项目中其他 Service 的实现方式
```

---

## 4. 问题调试类提示词

### 4.1 分析错误日志

```
分析以下错误日志，找出问题原因并提供解决方案：

```
2025-01-15 10:30:45 ERROR [http-nio-8080-exec-1] c.i.t.m.p.s.PhotoServiceImpl : 创建照片失败
org.springframework.dao.DataIntegrityViolationException: 
Could not execute statement; SQL [n/a]; constraint [uk_photo_url]; 
nested exception is org.hibernate.exception.ConstraintViolationException: 
Could not execute statement
```

上下文信息：
- 表 interest_photo 有唯一索引 uk_photo_url 在 url 字段上
- 代码在 PhotoServiceImpl.createPhoto() 方法中
- 使用的是 MyBatis Plus 的 insert() 方法
```

### 4.2 排查空指针异常

```
分析以下代码，找出可能的空指针异常位置：

```java
public PhotoRespVO getPhoto(Long id) {
    PhotoDO photoDO = photoMapper.selectById(id);
    PhotoRespVO respVO = BeanUtils.toBean(photoDO, PhotoRespVO.class);
    respVO.setUserName(photoDO.getUser().getName());
    return respVO;
}
```

要求：
1. 列出所有可能的空指针异常位置
2. 提供修复方案
3. 添加必要的空值检查
```

### 4.3 调试事务问题

```
分析以下事务问题：

```java
@Transactional(rollbackFor = Exception.class)
public void batchCreatePhotos(List<PhotoAddReqVO> reqVOList) {
    for (PhotoAddReqVO reqVO : reqVOList) {
        PhotoDO photoDO = BeanUtils.toBean(reqVO, PhotoDO.class);
        photoMapper.insert(photoDO);
        
        // 调用外部服务
        tagService.createTags(photoDO.getId(), reqVO.getTags());
    }
}
```

问题：当 tagService.createTags() 抛出异常时，已插入的照片没有被回滚。

要求：
1. 分析问题原因
2. 提供解决方案
3. 考虑事务传播机制
```

---

## 5. 代码审查类提示词

### 5.1 代码规范检查

```
审查以下 Controller 代码，检查是否符合项目规范：

```java
@RestController
@RequestMapping("/admin/photo")
public class PhotoController {
    
    @Autowired
    private PhotoService photoService;
    
    @PostMapping("/create")
    public ResponseEntity<Long> createPhoto(@RequestBody PhotoAddReqVO reqVO) {
        Long id = photoService.createPhoto(reqVO);
        return ResponseEntity.ok(id);
    }
}
```

要求：
1. 检查注解使用是否正确
2. 检查依赖注入方式
3. 检查返回类型是否符合规范
4. 检查是否缺少权限控制
5. 检查是否缺少参数校验
6. 提供修改建议
```

### 5.2 安全检查

```
审查以下代码，检查是否存在安全问题：

```java
@GetMapping("/list")
public CommonResult<List<PhotoRespVO>> getPhotoList(@RequestParam String userId) {
    String sql = "SELECT * FROM interest_photo WHERE user_id = " + userId;
    List<PhotoDO> photos = jdbcTemplate.query(sql, ...);
    return success(BeanUtils.toBean(photos, PhotoRespVO.class));
}
```

要求：
1. 识别安全问题
2. 提供安全的实现方式
3. 使用项目规范的方式重写
```

### 5.3 性能问题检查

```
审查以下代码，检查是否存在性能问题：

```java
public PageResult<PhotoPageRespVO> getPhotoPage(PhotoPageReqVO reqVO) {
    PageResult<PhotoDO> pageResult = photoMapper.selectPage(reqVO);
    List<PhotoPageRespVO> voList = new ArrayList<>();
    for (PhotoDO photoDO : pageResult.getList()) {
        PhotoPageRespVO vo = BeanUtils.toBean(photoDO, PhotoPageRespVO.class);
        // 查询用户信息
        UserDO user = userMapper.selectById(photoDO.getUserId());
        vo.setUserName(user.getName());
        // 查询标签信息
        List<TagDO> tags = tagMapper.selectByPhotoId(photoDO.getId());
        vo.setTagNames(tags.stream().map(TagDO::getName).collect(Collectors.toList()));
        voList.add(vo);
    }
    return new PageResult<>(voList, pageResult.getTotal());
}
```

要求：
1. 识别性能问题（N+1 查询）
2. 提供优化方案
3. 使用批量查询优化
```

---

## 6. 文档生成类提示词

### 6.1 API 文档生成

```
为以下 Controller 方法生成完整的 API 文档注释：

```java
@PostMapping
public CommonResult<Long> createPhoto(@RequestBody PhotoAddReqVO reqVO) {
    return success(photoService.createPhoto(reqVO));
}
```

要求：
1. 添加 @Operation 注解，包含 summary 和 description
2. 为 PhotoAddReqVO 的每个字段添加 @Schema 注解
3. 提供示例值
4. 说明可能的错误码
```

### 6.2 JavaDoc 注释生成

```
为以下 Service 方法生成完整的 JavaDoc 注释：

```java
public PageResult<PhotoPageRespVO> getPhotoPage(PhotoPageReqVO reqVO);
```

要求：
1. 添加方法描述
2. 添加 @param 注释
3. 添加 @return 注释
4. 添加 @throws 注释（如果有）
5. 添加使用示例（可选）
```

---

## 7. 测试代码生成

### 7.1 单元测试生成

```
为 PhotoServiceImpl 创建单元测试类，要求：

1. 测试类名：PhotoServiceImplTest
2. 使用 JUnit 5 和 Mockito
3. 测试以下方法：
   - createPhoto()：测试正常创建、测试参数校验、测试异常情况
   - updatePhoto()：测试正常更新、测试记录不存在的情况
   - deletePhoto()：测试正常删除、测试记录不存在的情况
   - getPhoto()：测试正常查询、测试记录不存在的情况
   - getPhotoPage()：测试分页查询、测试查询条件

4. Mock PhotoMapper
5. 使用 @ExtendWith(MockitoExtension.class)
6. 每个测试方法添加清晰的注释
```

### 7.2 集成测试生成

```
创建 PhotoController 的集成测试，要求：

1. 使用 @SpringBootTest 和 @AutoConfigureMockMvc
2. 测试所有 CRUD 接口
3. 使用 MockMvc 发送 HTTP 请求
4. 验证响应状态码和响应体
5. 测试权限控制
6. 测试参数校验
```

---

## 8. 性能优化提示词

### 8.1 查询优化

```
优化以下查询方法，提升性能：

```java
public List<PhotoRespVO> getPhotosByUserId(Long userId) {
    List<PhotoDO> photos = photoMapper.selectList(
        new LambdaQueryWrapper<PhotoDO>()
            .eq(PhotoDO::getUserId, userId)
    );
    
    List<PhotoRespVO> result = new ArrayList<>();
    for (PhotoDO photo : photos) {
        PhotoRespVO vo = BeanUtils.toBean(photo, PhotoRespVO.class);
        // 查询标签
        List<TagDO> tags = tagMapper.selectByPhotoId(photo.getId());
        vo.setTags(tags);
        result.add(vo);
    }
    return result;
}
```

要求：
1. 解决 N+1 查询问题
2. 使用批量查询
3. 考虑使用缓存（如需要）
4. 提供优化后的代码
```

### 8.2 缓存优化

```
为以下方法添加 Redis 缓存：

```java
public PhotoRespVO getPhoto(Long id) {
    PhotoDO photoDO = photoMapper.selectById(id);
    if (photoDO == null) {
        throw exception(PHOTO_NOT_EXISTS);
    }
    return BeanUtils.toBean(photoDO, PhotoRespVO.class);
}
```

要求：
1. 使用 Spring Cache 注解
2. 缓存 key 格式：photo:{id}
3. 缓存过期时间：1 小时
4. 更新和删除操作时清除缓存
5. 提供完整的实现代码
```

---

## 9. 安全相关提示词

### 9.1 SQL 注入防护

```
检查以下代码是否存在 SQL 注入风险，并提供安全实现：

```java
public List<PhotoDO> searchPhotos(String keyword) {
    String sql = "SELECT * FROM interest_photo WHERE title LIKE '%" + keyword + "%'";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PhotoDO.class));
}
```

要求：
1. 识别安全问题
2. 使用 MyBatis Plus 的安全查询方式重写
3. 添加参数校验
```

### 9.2 权限控制

```
为以下接口添加权限控制：

```java
@DeleteMapping("/{id}")
public CommonResult<Boolean> deletePhoto(@PathVariable Long id) {
    photoService.deletePhoto(id);
    return success(true);
}
```

要求：
1. 添加 @PreAuthorize 注解
2. 权限标识：photo:delete
3. 考虑数据权限（只能删除自己的照片）
4. 提供完整的实现代码
```

---

## 10. 项目特定场景提示词

### 10.1 完整的 CRUD 模块生成

```
根据项目的架构规范，创建一个完整的"音乐"管理模块，包含以下功能：

1. 数据库表：interest_music
   字段：id、title、artist、album、duration、url、rating、status、create_time、update_time、deleted

2. 创建以下文件：
   - MusicDO（继承 BaseDO）
   - MusicMapper（继承 BaseMapperX）
   - MusicService 接口和实现类
   - MusicController（路径：/admin/music）
   - 5 个 VO 类（AddReqVO、UpdateReqVO、RespVO、PageReqVO、PageRespVO）
   - 错误码定义（MUSIC_NOT_EXISTS）

3. 遵循项目规范：
   - Controller 使用 @Tag、@Operation 注解
   - Service 使用 BeanUtils.toBean() 转换
   - Mapper 使用 LambdaQueryWrapperX 构建查询
   - 所有删除和更新操作前校验存在性
   - 使用统一的异常处理

4. 分页查询支持：
   - 按 title 模糊查询
   - 按 artist 查询
   - 按 rating 范围查询
   - 按 createTime 范围查询
   - 按 createTime 倒序排列

请按照项目的标准结构和命名规范生成所有代码。
```

### 10.2 关联查询优化

```
优化以下关联查询，使用项目规范的方式：

当前实现：
```java
public PhotoRespVO getPhoto(Long id) {
    PhotoDO photoDO = photoMapper.selectById(id);
    PhotoRespVO respVO = BeanUtils.toBean(photoDO, PhotoRespVO.class);
    
    // 查询用户信息
    UserDO user = userMapper.selectById(photoDO.getUserId());
    respVO.setUserName(user.getName());
    
    // 查询标签
    List<TagDO> tags = tagMapper.selectByPhotoId(id);
    respVO.setTagNames(tags.stream().map(TagDO::getName).collect(Collectors.toList()));
    
    return respVO;
}
```

要求：
1. 如果查询频繁，考虑在 Mapper 中使用 JOIN 查询
2. 或者使用批量查询优化（如果是在列表查询中）
3. 保持代码符合项目规范
4. 提供优化后的代码
```

### 10.3 批量操作实现

```
实现批量删除照片的功能，要求：

1. 接口路径：DELETE /admin/photo/batch
2. 参数：List<Long> ids
3. 权限：photo:delete
4. 要求：
   - 批量删除前校验所有记录是否存在
   - 如果部分记录不存在，返回详细的错误信息
   - 使用事务保证原子性
   - 使用 MyBatis Plus 的批量删除方法

提供完整的 Controller、Service 实现代码。
```

---

## 11. 提示词使用技巧

### 11.1 组合使用多个提示词

**示例**：
```
第一步：创建 PhotoDO 类（参考 2.1 节的标准 DO 类生成提示词）
第二步：创建 PhotoMapper 接口（参考 2.2 节的标准 Mapper 生成提示词）
第三步：创建 5 个 VO 类（参考 2.3 节的完整 VO 类生成提示词）
第四步：创建 PhotoService（参考 2.4 节的 Service 生成提示词）
第五步：创建 PhotoController（参考 2.5 节的 Controller 生成提示词）
第六步：添加错误码定义（参考项目中的 ErrorCodeConstants）
```

### 11.2 引用现有代码

**示例**：
```
参考项目中 interest-tracker-server/src/main/java/com/interest/tracker/module/interest/service/ 下的实现方式，
创建一个类似的 MusicService，但需要添加以下特殊逻辑：
1. 创建音乐时，自动从 URL 中提取元数据（标题、艺术家等）
2. 支持批量导入音乐列表
3. 添加音乐去重逻辑（根据 URL）
```

### 11.3 分步骤迭代

**示例**：
```
第一步：先创建基础的 CRUD 功能
第二步：添加业务校验逻辑（如：URL 格式校验、标题长度校验）
第三步：添加关联查询（如：查询音乐时关联查询标签）
第四步：添加缓存优化
第五步：添加批量操作功能
```

### 11.4 明确约束条件

**示例**：
```
创建 PhotoService，要求：
1. 必须使用 BeanUtils.toBean() 进行对象转换（不要手动 new 对象）
2. 删除和更新操作前必须调用 validatePhotoExists() 方法
3. 所有事务方法使用 @Transactional(rollbackFor = Exception.class)
4. 异常处理使用 throw exception(ERROR_CODE) 的方式
5. 不要创建额外的 DTO 类，直接使用 VO
6. 参考项目中的 ArtWorkServiceImpl 的实现风格
```

---

## 12. 常见问题与解决方案

### 12.1 AI 生成的代码不符合项目规范

**问题**：AI 生成的代码使用了项目中没有的依赖或不符合命名规范。

**解决方案**：
```
在提示词中明确说明：
1. 项目的技术栈和版本
2. 项目的代码规范和命名规范
3. 参考项目中现有的类似代码
4. 明确禁止使用的技术或方式

示例：
"使用 Spring Boot 3.x + MyBatis Plus 3.5.x，参考项目中 ArtWorkServiceImpl 的实现方式，
不要使用 JPA，不要使用 @Autowired，使用 @Resource 注入依赖。"
```

### 12.2 AI 生成的代码缺少必要的校验

**问题**：AI 生成的代码没有添加参数校验、权限控制等。

**解决方案**：
```
在提示词中明确要求：
1. 添加参数校验注解（@Valid、@NotNull、@NotBlank 等）
2. 添加权限控制注解（@PreAuthorize）
3. 添加业务校验逻辑
4. 添加异常处理

示例：
"创建 PhotoController，要求：
1. 所有接口添加 @PreAuthorize 权限控制
2. 所有请求参数添加 @Valid 校验
3. 所有删除和更新操作前校验记录存在性
4. 使用统一的异常处理方式"
```

### 12.3 AI 生成的代码存在性能问题

**问题**：AI 生成的代码存在 N+1 查询、没有使用批量操作等问题。

**解决方案**：
```
在提示词中明确要求：
1. 避免 N+1 查询，使用批量查询
2. 合理使用缓存
3. 优化数据库查询

示例：
"实现 getPhotoList() 方法，要求：
1. 使用批量查询，避免循环查询数据库
2. 如果查询频繁，考虑添加 Redis 缓存
3. 使用 MyBatis Plus 的 selectBatchIds() 方法"
```

### 12.4 AI 生成的代码缺少注释

**问题**：AI 生成的代码没有 JavaDoc 注释或注释不完整。

**解决方案**：
```
在提示词中明确要求：
1. 为所有类添加类注释
2. 为所有公共方法添加 JavaDoc 注释
3. 为复杂逻辑添加行内注释

示例：
"创建 PhotoService 接口和实现类，要求：
1. 所有方法添加完整的 JavaDoc 注释，包括 @param、@return、@throws
2. 复杂业务逻辑添加行内注释说明
3. 类注释说明类的职责"
```

### 12.5 AI 不理解业务逻辑

**问题**：AI 生成的代码逻辑不正确，不符合业务需求。

**解决方案**：
```
在提示词中详细说明：
1. 业务规则和约束条件
2. 特殊处理逻辑
3. 边界情况处理

示例：
"创建音乐管理功能，业务规则：
1. 音乐 URL 必须唯一，如果重复则更新现有记录
2. 删除音乐时，如果有关联的播放记录，不允许删除
3. 音乐评分范围是 1-10，超出范围抛出异常
4. 批量导入音乐时，如果部分失败，已成功的记录不回滚"
```

---

## 13. 实用提示词模板库

### 13.1 快速生成 CRUD 模块

```
根据项目规范，快速生成 {模块名} 的完整 CRUD 功能：

数据库表：{表名}
主要字段：{字段列表}

要求：
1. 创建完整的 DO、Mapper、Service、Controller、VO 类
2. 遵循项目的架构规范和命名规范
3. 参考项目中 {参考模块} 的实现方式
4. 包含完整的参数校验、权限控制、异常处理
5. 分页查询支持 {查询条件列表}
6. 添加必要的错误码定义

请分步骤生成，每步生成后等待确认再继续下一步。
```

### 13.2 代码审查模板

```
审查以下代码，检查：
1. 是否符合项目代码规范
2. 是否存在安全问题（SQL 注入、XSS 等）
3. 是否存在性能问题（N+1 查询、缺少索引等）
4. 是否缺少必要的校验和异常处理
5. 是否遵循分层架构原则
6. 代码可读性和可维护性

代码：
{粘贴代码}

请提供详细的审查报告和改进建议。
```

### 13.3 重构代码模板

```
重构以下代码，要求：
1. 提取公共方法，减少代码重复
2. 优化性能（解决 N+1 查询等）
3. 改进异常处理
4. 提升代码可读性
5. 符合项目规范

代码：
{粘贴代码}

请提供重构后的代码和重构说明。
```

---

## 14. 最佳实践总结

### 14.1 提示词编写原则

1. **明确具体**：提供详细的需求说明，避免模糊描述
2. **提供上下文**：说明项目技术栈、架构规范、参考代码
3. **分步骤**：复杂需求分步骤提出，逐步迭代
4. **明确约束**：说明必须遵守的规范和禁止的做法
5. **验证结果**：生成代码后要验证是否符合预期

### 14.2 代码生成流程

```
1. 明确需求 → 2. 编写提示词 → 3. AI 生成代码 → 4. 审查代码 → 5. 测试验证 → 6. 迭代优化
```

### 14.3 持续改进

1. **积累模板**：将常用的提示词保存为模板
2. **总结经验**：记录哪些提示词效果好，哪些需要改进
3. **分享交流**：与团队成员分享有效的提示词
4. **更新文档**：根据实践经验不断更新本指南

---

## 15. 参考资源

### 15.1 项目文档
- [通用架构设计与代码结构规范](./通用架构设计与代码结构规范.md)
- [CRUD 开发流程规范](./CRUD开发流程规范.md)
- [项目设计方案](./项目设计方案.md)

### 15.2 技术文档
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis Plus 官方文档](https://baomidou.com/)
- [Spring Security 官方文档](https://spring.io/projects/spring-security)

### 15.3 AI 工具推荐
- **Cursor**：AI 代码编辑器，支持代码生成和重构
- **GitHub Copilot**：AI 代码补全工具
- **ChatGPT**：通用 AI 助手，可用于代码审查和问题解答

---

## 16. 更新日志

### v1.0.0 (2025-01-XX)
- 初始版本
- 包含代码生成、重构、调试等各类提示词模板
- 添加项目特定场景的提示词示例
- 整理最佳实践和常见问题解决方案

---

**文档维护**：开发团队  
**最后更新**：2025-01-XX  
**反馈建议**：如有问题或建议，请联系开发团队

