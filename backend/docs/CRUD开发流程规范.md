# CRUD 开发流程规范文档

> 基于项目实践总结的通用增删改查开发流程
> 
> 更新时间：2025-11-25

---

## 目录

- [一、前置准备阶段](#一前置准备阶段)
- [二、数据层开发（dal 包）](#二数据层开发dal-包)
- [三、接口层开发（controller 包）](#三接口层开发controller-包)
- [四、业务层开发（service 包）](#四业务层开发service-包)
- [五、错误码配置](#五错误码配置)
- [六、开发检查清单](#六开发检查清单)
- [七、常见注意事项](#七常见注意事项)

---

## 一、前置准备阶段

### 1. 确认业务需求

在开始编码前，必须明确以下信息：

- **功能模块名称**：如期刊年度、期刊期号、期刊栏目
- **字段信息**：字段名称、数据类型、是否必填、默认值等
- **表关系**：一对多、多对多、外键关联等
- **功能范围**：需要实现哪些操作（增删改查、批量操作等）
- **特殊需求**：是否需要详情接口、是否需要额外校验逻辑

**重要提醒**：VO 类的字段必须由需求方提供，不要自行添加字段！

---

## 二、数据层开发（dal 包）

### 1. 创建 DO 类（DataObject）

**文件位置**：`{module}-server/src/main/java/.../dal/dataobject/`

**代码模板**：

```java
package com.jiayao.cloud.module.{module}.dal.dataobject.{subpackage};

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiayao.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * XXX DO
 *
 * @author {author}
 */
@TableName("table_name")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XxxDO extends TenantBaseDO {  // 或 BaseDO

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 业务字段示例
     */
    private String fieldName;

    /**
     * 状态字段
     * 枚举 {@link XxxStatusEnum}
     */
    private Integer status;

    /**
     * 日期字段
     */
    private LocalDate dateField;

    /**
     * 布尔字段
     */
    private Boolean isActive;
}
```

**注意事项**：

✅ **必须做的**：
- 继承 `TenantBaseDO`（多租户）或 `BaseDO`（非多租户）
- 主键字段添加 `@TableId` 注解
- 表名使用 `@TableName` 注解指定
- 添加完整的字段注释

✅ **数据类型规范**：
- 主键：`Long`
- 状态/枚举：`Integer`
- 日期：`LocalDate`
- 时间：`LocalDateTime`
- 布尔：`Boolean`
- 外键：根据关联表主键类型（通常 `Long` 或 `Integer`）

❌ **不要做的**：
- 不要在 DO 中添加关联对象（如 `private User user`）
- 不要添加业务计算字段（如 `private Integer age`，应该在 VO 中计算）
- 不要修改继承的基类字段

---

### 2. 创建枚举类（如需要）

**文件位置**：`{module}-api/src/main/java/.../enums/`

**代码模板**：

```java
package com.jiayao.cloud.module.{module}.enums;

import cn.hutool.core.util.ArrayUtil;
import com.jiayao.cloud.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * XXX状态枚举
 *
 * @author {author}
 */
@Getter
@AllArgsConstructor
public enum XxxStatusEnum implements ArrayValuable<Integer> {

    STATUS_1(1, "状态1"),
    STATUS_2(2, "状态2"),
    STATUS_3(3, "状态3");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(XxxStatusEnum::getValue)
            .toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer value;
    /**
     * 状态名
     */
    private final String name;

    public static XxxStatusEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(item -> item.getValue().equals(value), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
```

**使用场景**：
- 状态字段（如：启用/禁用）
- 固定选项（如：类型、级别等）
- 需要在多处共享的常量值

---

### 3. 创建 Mapper 接口

**文件位置**：`{module}-server/src/main/java/.../dal/mysql/`

**代码模板**：

```java
package com.jiayao.cloud.module.{module}.dal.mysql.{subpackage};

import com.jiayao.cloud.framework.common.pojo.PageResult;
import com.jiayao.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.jiayao.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.jiayao.cloud.module.{module}.controller.admin.{subpackage}.vo.XxxPageReqVO;
import com.jiayao.cloud.module.{module}.dal.dataobject.{subpackage}.XxxDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * XXX Mapper
 *
 * @author {author}
 */
@Mapper
public interface XxxMapper extends BaseMapperX<XxxDO> {

    /**
     * 分页查询XXX
     *
     * @param reqVO 分页请求参数
     * @return 分页结果
     */
    default PageResult<XxxDO> selectPage(XxxPageReqVO reqVO) {
        LambdaQueryWrapperX<XxxDO> wrapper = new LambdaQueryWrapperX<>();
        // 只添加 VO 中实际存在的查询字段
        wrapper.eqIfPresent(XxxDO::getFieldName, reqVO.getFieldName())
               .likeIfPresent(XxxDO::getName, reqVO.getName())
               .eqIfPresent(XxxDO::getStatus, reqVO.getStatus())
               .betweenIfPresent(XxxDO::getCreateTime, reqVO.getBeginTime(), reqVO.getEndTime());
        // 排序
        wrapper.orderByDesc(XxxDO::getId);
        return selectPage(reqVO, wrapper);
    }
}
```

**查询条件方法**：
- `eqIfPresent`：等于（= ）
- `neIfPresent`：不等于（!=）
- `likeIfPresent`：模糊查询（LIKE）
- `gtIfPresent`：大于（>）
- `geIfPresent`：大于等于（>=）
- `ltIfPresent`：小于（<）
- `leIfPresent`：小于等于（<=）
- `betweenIfPresent`：范围查询（BETWEEN）
- `inIfPresent`：在列表中（IN）

**排序方法**：
- `orderByAsc`：升序
- `orderByDesc`：降序

**注意事项**：
- ⚠️ 只使用 VO 中实际存在的字段构建查询条件
- ⚠️ 使用 `xxxIfPresent` 方法避免空值查询
- ⚠️ 合理设置排序规则（通常按 ID 或创建时间倒序）
- 如果 VO 字段名与 DO 不一致，需要在条件中手动映射

---

## 三、接口层开发（controller 包）

### 1. 创建 VO 类

**文件位置**：`{module}-server/src/main/java/.../controller/admin/{subpackage}/vo/`

每个功能模块需要创建 **5 个 VO 类**：

#### a. AddReqVO（新增请求）

```java
package com.jiayao.cloud.module.{module}.controller.admin.{subpackage}.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * 新增XXX的参数 Request VO
 *
 * @author {author}
 */
@Schema(description = "新增XXX的参数 Request VO")
@Data
public class XxxAddReqVO {

    @Schema(description = "字段名", requiredMode = REQUIRED, example = "示例值")
    private String fieldName;

    @Schema(description = "状态", requiredMode = REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "备注", example = "这是备注")
    private String remark;

    // 不包含 id 字段
}
```

#### b. UpdateReqVO（更新请求）

```java
/**
 * 修改XXX的参数 Request VO
 *
 * @author {author}
 */
@Schema(description = "修改XXX的参数 Request VO")
@Data
public class XxxUpdateReqVO {

    @Schema(description = "主键ID", requiredMode = REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "字段名", requiredMode = REQUIRED, example = "示例值")
    private String fieldName;

    @Schema(description = "状态", requiredMode = REQUIRED, example = "1")
    private Integer status;
}
```

#### c. RespVO（详情响应）

```java
/**
 * XXX详情 Response VO
 *
 * @author {author}
 */
@Schema(description = "XXX详情 Response VO")
@Data
public class XxxRespVO extends TransVO {  // 或不继承

    @Schema(description = "主键", requiredMode = REQUIRED, example = "1")
    private Long id;

    @Schema(description = "字段名", requiredMode = REQUIRED, example = "示例值")
    private String fieldName;

    @Schema(description = "状态", requiredMode = REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = REQUIRED)
    private LocalDateTime createTime;
}
```

#### d. PageReqVO（分页查询请求）

```java
/**
 * XXX分页查询参数 Request VO
 *
 * @author {author}
 */
@Schema(description = "XXX分页查询参数 Request VO")
@Data
public class XxxPageReqVO extends PageParam {

    @Schema(description = "字段名", example = "示例值")
    private String fieldName;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "开始时间", example = "2024-01-01 00:00:00")
    private LocalDateTime beginTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private LocalDateTime endTime;
}
```

#### e. PageRespVO（分页响应）

```java
/**
 * XXX分页列表 Response VO
 *
 * @author {author}
 */
@Schema(description = "XXX分页列表 Response VO")
@Data
public class XxxPageRespVO extends TransVO {  // 或不继承

    @Schema(description = "主键", requiredMode = REQUIRED, example = "1")
    private Long id;

    @Schema(description = "字段名", requiredMode = REQUIRED, example = "示例值")
    private String fieldName;

    @Schema(description = "状态", requiredMode = REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = REQUIRED)
    private LocalDateTime createTime;
}
```

**VO 类规范**：

✅ **必须遵守**：
- 新增 VO 不包含 `id` 字段
- 更新 VO 必须包含 `id` 字段
- 必填字段使用 `requiredMode = REQUIRED`
- 所有字段都要添加 `@Schema` 注解和示例值
- 查询 VO 继承 `PageParam`
- 字段名与 DO 保持一致（除非有特殊映射需求）

✅ **推荐做法**：
- 响应 VO 可继承 `TransVO`（如需翻译功能）
- 提供清晰的字段说明和示例值

❌ **不要做的**：
- ❌ 不要自行添加需求方未要求的字段
- ❌ 不要在 VO 中添加复杂的业务逻辑

---

### 2. 创建或更新 Controller

**文件位置**：`{module}-server/src/main/java/.../controller/admin/{subpackage}/`

**代码模板**：

```java
package com.jiayao.cloud.module.{module}.controller.admin.{subpackage};

import com.jiayao.cloud.framework.common.pojo.CommonResult;
import com.jiayao.cloud.framework.common.pojo.PageResult;
import com.jiayao.cloud.module.{module}.controller.admin.{subpackage}.vo.*;
import com.jiayao.cloud.module.{module}.service.{subpackage}.XxxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.jiayao.cloud.framework.common.pojo.CommonResult.success;

/**
 * XXX Controller
 *
 * @author {author}
 */
@Tag(name = "模块名 - 功能名")
@RestController
@RequestMapping("/module/xxx")
@Validated
public class XxxController {

    @Resource
    private XxxService xxxService;

    /**
     * 新增XXX
     */
    @PostMapping
    @Operation(summary = "新增XXX")
    @PreAuthorize("@ss.hasPermission('module:xxx:create')")
    public CommonResult<Boolean> addXxx(@Valid @RequestBody XxxAddReqVO reqVO) {
        xxxService.addXxx(reqVO);
        return success(true);
    }

    /**
     * 删除XXX
     */
    @DeleteMapping
    @Operation(summary = "删除XXX")
    @PreAuthorize("@ss.hasPermission('module:xxx:delete')")
    public CommonResult<Boolean> deleteXxx(@RequestParam Long id) {
        xxxService.deleteXxx(id);
        return success(true);
    }

    /**
     * 修改XXX
     */
    @PostMapping("/update")
    @Operation(summary = "修改XXX")
    @PreAuthorize("@ss.hasPermission('module:xxx:update')")
    public CommonResult<Boolean> updateXxx(@Valid @RequestBody XxxUpdateReqVO reqVO) {
        xxxService.updateXxx(reqVO);
        return success(true);
    }

    /**
     * 获取XXX详情
     */
    @GetMapping
    @Operation(summary = "获取XXX详情")
    @PreAuthorize("@ss.hasPermission('module:xxx:get')")
    public CommonResult<XxxRespVO> getXxx(@RequestParam Long id) {
        return success(xxxService.getXxx(id));
    }

    /**
     * 获取XXX分页列表
     */
    @GetMapping("/page")
    @Operation(summary = "获取XXX分页列表")
    @PreAuthorize("@ss.hasPermission('module:xxx:get')")
    public CommonResult<PageResult<XxxPageRespVO>> getXxxPage(@Valid XxxPageReqVO reqVO) {
        return success(xxxService.getXxxPage(reqVO));
    }
}
```

**Controller 规范**：

| 操作 | HTTP 方法 | 路径 | 权限 |
|-----|----------|-----|-----|
| 新增 | POST | / | module:xxx:create |
| 删除 | DELETE | / | module:xxx:delete |
| 修改 | POST | /update | module:xxx:update |
| 详情 | GET | / | module:xxx:get |
| 分页 | GET | /page | module:xxx:get |

**注意事项**：
- 使用 `@Valid` 触发参数校验
- 查询参数使用 `@RequestParam`
- 请求体使用 `@RequestBody`
- 分页查询不需要 `@RequestParam`（因为继承了 `PageParam`）

---

## 四、业务层开发（service 包）

### 1. 创建 Service 接口

**文件位置**：`{module}-server/src/main/java/.../service/{subpackage}/`

**代码模板**：

```java
package com.jiayao.cloud.module.{module}.service.{subpackage};

import com.jiayao.cloud.framework.common.pojo.PageResult;
import com.jiayao.cloud.module.{module}.controller.admin.{subpackage}.vo.*;

/**
 * XXX Service 接口
 *
 * @author {author}
 */
public interface XxxService {

    /**
     * 新增XXX
     *
     * @param reqVO 新增参数
     */
    void addXxx(XxxAddReqVO reqVO);

    /**
     * 删除XXX
     *
     * @param id 删除的XXX ID
     */
    void deleteXxx(Long id);

    /**
     * 修改XXX
     *
     * @param reqVO 修改参数
     */
    void updateXxx(XxxUpdateReqVO reqVO);

    /**
     * 获取XXX详情
     *
     * @param id XXX ID
     * @return XXX详情
     */
    XxxRespVO getXxx(Long id);

    /**
     * 获取XXX分页列表
     *
     * @param reqVO 获取参数
     * @return XXX分页列表
     */
    PageResult<XxxPageRespVO> getXxxPage(XxxPageReqVO reqVO);
}
```

---

### 2. 实现 Service

**文件位置**：`{module}-server/src/main/java/.../service/{subpackage}/`

**代码模板**：

```java
package com.jiayao.cloud.module.{module}.service.{subpackage};

import com.jiayao.cloud.framework.common.pojo.PageResult;
import com.jiayao.cloud.framework.common.util.object.BeanUtils;
import com.jiayao.cloud.module.{module}.controller.admin.{subpackage}.vo.*;
import com.jiayao.cloud.module.{module}.dal.dataobject.{subpackage}.XxxDO;
import com.jiayao.cloud.module.{module}.dal.mysql.{subpackage}.XxxMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.jiayao.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.jiayao.cloud.module.{module}.constants.ErrorCodeConstants.*;

/**
 * XXX Service 实现
 *
 * @author {author}
 */
@Service
@Validated
@Slf4j
public class XxxServiceImpl implements XxxService {

    @Resource
    private XxxMapper xxxMapper;

    @Override
    public void addXxx(XxxAddReqVO reqVO) {
        // VO → DO 转换
        XxxDO xxxDO = BeanUtils.toBean(reqVO, XxxDO.class);
        // 插入数据库
        xxxMapper.insert(xxxDO);
    }

    @Override
    public void deleteXxx(Long id) {
        // 校验存在性
        validateXxxExists(id);
        // 删除
        xxxMapper.deleteById(id);
    }

    @Override
    public void updateXxx(XxxUpdateReqVO reqVO) {
        // 校验存在性
        validateXxxExists(reqVO.getId());
        // VO → DO 转换
        XxxDO xxxDO = BeanUtils.toBean(reqVO, XxxDO.class);
        // 更新
        xxxMapper.updateById(xxxDO);
    }

    @Override
    public XxxRespVO getXxx(Long id) {
        // 查询并校验
        XxxDO xxxDO = validateXxxExists(id);
        // DO → VO 转换
        return BeanUtils.toBean(xxxDO, XxxRespVO.class);
    }

    @Override
    public PageResult<XxxPageRespVO> getXxxPage(XxxPageReqVO reqVO) {
        // 分页查询
        PageResult<XxxDO> pageResult = xxxMapper.selectPage(reqVO);
        // DO → VO 转换
        return BeanUtils.toBean(pageResult, XxxPageRespVO.class);
    }

    /* ================== 私有方法 ================== */

    /**
     * 校验XXX是否存在
     */
    private XxxDO validateXxxExists(Long id) {
        XxxDO xxxDO = xxxMapper.selectById(id);
        if (xxxDO == null) {
            throw exception(XXX_NOT_EXISTS);
        }
        return xxxDO;
    }
}
```

**Service 核心规范**：

✅ **必须遵守**：
1. **统一使用 `BeanUtils.toBean()` 进行转换**
   ```java
   // VO → DO
   XxxDO xxxDO = BeanUtils.toBean(reqVO, XxxDO.class);
   
   // DO → VO
   XxxRespVO respVO = BeanUtils.toBean(xxxDO, XxxRespVO.class);
   
   // PageResult<DO> → PageResult<VO>
   return BeanUtils.toBean(pageResult, XxxPageRespVO.class);
   ```

2. **删除和更新前必须校验存在性**
   ```java
   @Override
   public void deleteXxx(Long id) {
       validateXxxExists(id);  // 必须先校验
       xxxMapper.deleteById(id);
   }
   ```

3. **使用私有方法统一处理校验**
   ```java
   private XxxDO validateXxxExists(Long id) {
       XxxDO xxxDO = xxxMapper.selectById(id);
       if (xxxDO == null) {
           throw exception(XXX_NOT_EXISTS);
       }
       return xxxDO;
   }
   ```

❌ **不要做的**：
- ❌ 不要创建额外的 DTO 类
- ❌ 不要手动 new 对象进行字段拷贝
- ❌ 不要在 Service 中直接处理 HTTP 相关逻辑

---

## 五、错误码配置

**文件位置**：`{module}-api/src/main/java/.../constants/ErrorCodeConstants.java`

**代码模板**：

```java
package com.jiayao.cloud.module.{module}.constants;

import com.jiayao.cloud.framework.common.exception.ErrorCode;

/**
 * {Module} 错误码枚举类
 * 使用 1-xxx-000-000 错误码段
 *
 * @author {author}
 */
public interface ErrorCodeConstants {

    /** ==================== XXX功能 1-xxx-001-000 ==================== */
    ErrorCode XXX_NOT_EXISTS = new ErrorCode(1_xxx_001_001, "XXX不存在");
    ErrorCode XXX_NAME_DUPLICATE = new ErrorCode(1_xxx_001_002, "XXX名称已存在");
    ErrorCode XXX_STATUS_INVALID = new ErrorCode(1_xxx_001_003, "XXX状态不合法");
}
```

**错误码规范**：

格式：`1_模块编号_功能编号_序号`

示例：
- `1_007_001_001`：manage 模块（007）的配置功能（001）的第 1 个错误
- `1_007_003_001`：manage 模块（007）的期刊功能（003）的第 1 个错误

**常用错误码**：
- `XXX_NOT_EXISTS`：记录不存在
- `XXX_NAME_DUPLICATE`：名称重复
- `XXX_STATUS_INVALID`：状态不合法
- `XXX_DELETE_FAIL_HAS_CHILDREN`：删除失败，存在子记录
- `XXX_PARENT_NOT_EXISTS`：父级不存在

---

## 六、开发检查清单

### 必须检查项

在提交代码前，请逐项检查：

- [ ] **DO 类**
  - [ ] 字段与数据库表一致
  - [ ] 继承了正确的基类（`TenantBaseDO` 或 `BaseDO`）
  - [ ] 添加了 `@TableName` 和 `@TableId` 注解
  - [ ] 所有字段都有注释

- [ ] **VO 类**
  - [ ] 不包含用户未要求的字段
  - [ ] 必填字段标记了 `requiredMode = REQUIRED`
  - [ ] 添加了 `@Schema` 注解和示例值
  - [ ] 新增 VO 不包含 `id`，更新 VO 必须包含 `id`

- [ ] **Mapper**
  - [ ] 查询条件只使用 VO 中存在的字段
  - [ ] 使用了 `xxxIfPresent` 方法
  - [ ] 设置了合理的排序

- [ ] **Service**
  - [ ] 所有转换使用 `BeanUtils.toBean()`
  - [ ] 删除/更新操作有存在性校验
  - [ ] 使用了私有方法 `validateXxxExists()`

- [ ] **Controller**
  - [ ] 权限注解配置正确
  - [ ] 使用了 `@Valid` 校验参数
  - [ ] 路径和 HTTP 方法符合规范

- [ ] **错误码**
  - [ ] 添加了 `XXX_NOT_EXISTS` 错误码
  - [ ] 错误码格式正确
  - [ ] 错误信息简洁明确

- [ ] **代码质量**
  - [ ] 无编译错误
  - [ ] 无 Lint 警告
  - [ ] 代码格式符合项目规范

### 可选检查项

- [ ] 添加业务校验（唯一性、关联性等）
- [ ] 考虑事务处理（`@Transactional`）
- [ ] 添加操作日志
- [ ] 编写单元测试
- [ ] 添加接口文档注释

---

## 七、常见注意事项

### 1. 不要创建 DTO 类

❌ **错误做法**：
```java
// 不要创建这样的 DTO
public class XxxPageReqDTO extends PageParam {
    private String fieldName;
}

// Service 中不要这样转换
XxxPageReqDTO reqDTO = BeanUtils.toBean(reqVO, XxxPageReqDTO.class);
PageResult<XxxDO> pageResult = xxxMapper.selectPage(reqDTO);
```

✅ **正确做法**：
```java
// 直接使用 VO
PageResult<XxxDO> pageResult = xxxMapper.selectPage(reqVO);
```

---

### 2. 字段映射问题

当 VO 字段名与 DO 不一致时：

**问题场景**：
- VO 中：`yearId`（期刊年度 ID）
- DO 中：`journalYear`（期刊年度 ID）

**解决方案**：在 Mapper 中手动映射
```java
default PageResult<XxxDO> selectPage(XxxPageReqVO reqVO) {
    LambdaQueryWrapperX<XxxDO> wrapper = new LambdaQueryWrapperX<>();
    // 将 VO 的 yearId 映射到 DO 的 journalYear
    wrapper.eqIfPresent(XxxDO::getJournalYear, reqVO.getYearId());
    return selectPage(reqVO, wrapper);
}
```

---

### 3. 类型匹配

确保 VO 和 DO 对应字段类型一致：

| 场景 | VO 类型 | DO 类型 | 说明 |
|-----|--------|--------|-----|
| 主键 | `Long` | `Long` | 保持一致 |
| 外键 | `Long` 或 `Integer` | 根据关联表主键 | 必须匹配 |
| 状态 | `Integer` | `Integer` | 通过枚举管理 |
| 日期 | `LocalDate` | `LocalDate` | 保持一致 |
| 时间 | `LocalDateTime` | `LocalDateTime` | 保持一致 |

---

### 4. 枚举字段处理

**DO 中使用 Integer**：
```java
public class XxxDO extends TenantBaseDO {
    /**
     * 状态
     * 枚举 {@link XxxStatusEnum}
     */
    private Integer status;
}
```

**VO 中也使用 Integer**：
```java
@Schema(description = "状态", example = "1")
private Integer status;
```

**枚举类定义**：
```java
public enum XxxStatusEnum implements ArrayValuable<Integer> {
    ACTIVE(1, "启用"),
    INACTIVE(0, "禁用");
    // ...
}
```

---

### 5. 分页查询

**不需要创建 DTO**：
```java
// Service 实现
@Override
public PageResult<XxxPageRespVO> getXxxPage(XxxPageReqVO reqVO) {
    // 直接传入 VO，Mapper 会处理分页参数
    PageResult<XxxDO> pageResult = xxxMapper.selectPage(reqVO);
    return BeanUtils.toBean(pageResult, XxxPageRespVO.class);
}
```

**Mapper 实现**：
```java
default PageResult<XxxDO> selectPage(XxxPageReqVO reqVO) {
    LambdaQueryWrapperX<XxxDO> wrapper = new LambdaQueryWrapperX<>();
    // 构建查询条件
    wrapper.eqIfPresent(XxxDO::getStatus, reqVO.getStatus());
    // 排序
    wrapper.orderByDesc(XxxDO::getId);
    // reqVO 继承了 PageParam，包含分页信息
    return selectPage(reqVO, wrapper);
}
```

---

### 6. 关联关系处理

**一对多关系**：通过外键 ID 关联，不在 DO 中添加关联对象

❌ **错误做法**：
```java
public class IssueDO extends TenantBaseDO {
    private Long id;
    private YearDO year;  // ❌ 不要这样做
}
```

✅ **正确做法**：
```java
public class IssueDO extends TenantBaseDO {
    private Long id;
    /**
     * 期刊年度ID
     */
    private Integer journalYear;  // ✅ 只保存外键 ID
}
```

如需在返回结果中包含关联信息，在 Service 层单独查询并组装。

---

### 7. 详情接口处理

如果暂时不需要实现详情接口：

```java
@Override
public XxxRespVO getXxx(Long id) {
    // TODO: 实现待补充
    return null;
}
```

需要实现时：
```java
@Override
public XxxRespVO getXxx(Long id) {
    XxxDO xxxDO = validateXxxExists(id);
    return BeanUtils.toBean(xxxDO, XxxRespVO.class);
}
```

---

### 8. 事务处理

需要事务时添加 `@Transactional` 注解：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void deleteXxx(Long id) {
    // 校验存在性
    validateXxxExists(id);
    // 删除主记录
    xxxMapper.deleteById(id);
    // 删除关联记录
    xxxDetailMapper.deleteByXxxId(id);
}
```

---

### 9. 业务校验示例

**唯一性校验**：
```java
private void validateXxxUnique(String name, Long excludeId) {
    XxxDO existXxx = xxxMapper.selectOne(XxxDO::getName, name);
    if (existXxx != null && !existXxx.getId().equals(excludeId)) {
        throw exception(XXX_NAME_DUPLICATE);
    }
}
```

**关联性校验**：
```java
private void validateParentExists(Long parentId) {
    if (parentId == null || parentId == 0) {
        return;
    }
    XxxDO parent = xxxMapper.selectById(parentId);
    if (parent == null) {
        throw exception(XXX_PARENT_NOT_EXISTS);
    }
}
```

---

## 总结

### 核心原则

1. **层次清晰**：DO → Mapper → Service → Controller
2. **转换统一**：统一使用 `BeanUtils.toBean()`
3. **校验规范**：删除/更新前校验存在性
4. **简化设计**：不创建冗余的 DTO 类
5. **字段控制**：VO 字段由需求方决定，不擅自添加

### 开发流程图

```
1. 确认需求
   ↓
2. 创建 DO 类 + 枚举类（如需要）
   ↓
3. 创建 Mapper 接口
   ↓
4. 创建 5 个 VO 类
   ↓
5. 创建/更新 Controller
   ↓
6. 创建 Service 接口
   ↓
7. 实现 Service
   ↓
8. 配置错误码
   ↓
9. 自测 + 检查清单
   ↓
10. 提交代码
```

### 文件清单

一个完整的 CRUD 功能需要创建以下文件：

```
module-api/
  └── enums/
      └── XxxStatusEnum.java          # 枚举类（如需要）
  └── constants/
      └── ErrorCodeConstants.java     # 错误码（追加）

module-server/
  └── dal/
      └── dataobject/
          └── XxxDO.java              # DO 类
      └── mysql/
          └── XxxMapper.java          # Mapper 接口
  └── controller/admin/
      └── vo/
          ├── XxxAddReqVO.java        # 新增 VO
          ├── XxxUpdateReqVO.java     # 更新 VO
          ├── XxxRespVO.java          # 详情 VO
          ├── XxxPageReqVO.java       # 分页查询 VO
          └── XxxPageRespVO.java      # 分页响应 VO
      └── XxxController.java          # Controller
  └── service/
      ├── XxxService.java             # Service 接口
      └── XxxServiceImpl.java         # Service 实现
```

---

按此流程开发，可以保证代码结构统一、可维护性强，减少出错概率。

**文档版本**：v1.0  
**最后更新**：2025-11-25  
**维护者**：开发团队

