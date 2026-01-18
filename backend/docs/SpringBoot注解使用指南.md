# Spring Boot 注解使用指南

本文档详细介绍了项目中使用的 Spring Boot 相关注解，包括注解的作用、使用场景和代码示例。

---

## 目录

1. [核心启动注解](#1-核心启动注解)
2. [Web层注解](#2-web层注解)
3. [服务层注解](#3-服务层注解)
4. [依赖注入注解](#4-依赖注入注解)
5. [配置类注解](#5-配置类注解)
6. [数据校验注解](#6-数据校验注解)
7. [MyBatis-Plus注解](#7-mybatis-plus注解)
8. [事务管理注解](#8-事务管理注解)
9. [异常处理注解](#9-异常处理注解)

---

## 1. 核心启动注解

### 1.1 @SpringBootApplication

**作用**：标识 Spring Boot 应用的主启动类，是一个组合注解，包含了 `@Configuration`、`@EnableAutoConfiguration` 和 `@ComponentScan`。

**使用场景**：应用的主入口类

**代码示例**：

```10:19:backend/interest-tracker-server/src/main/java/com/interest/tracker/InterestTrackerApplication.java
@SpringBootApplication
// 暂时注释，等有业务模块后再启用
// @MapperScan(basePackages = "com.interest.tracker.module.*.dal.mysql")
public class InterestTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterestTrackerApplication.class, args);
    }

}
```

**说明**：
- 通常放在主类上，Spring Boot 会自动扫描当前包及其子包下的组件
- 启动类必须包含 `main` 方法

### 1.2 @MapperScan

**作用**：指定 MyBatis Mapper 接口的扫描路径，自动将 Mapper 接口注册为 Spring Bean。

**使用场景**：配置 MyBatis Mapper 扫描路径

**代码示例**：

```10:12:backend/interest-tracker-server/src/main/java/com/interest/tracker/InterestTrackerApplication.java
@SpringBootApplication
// 暂时注释，等有业务模块后再启用
// @MapperScan(basePackages = "com.interest.tracker.module.*.dal.mysql")
```

**说明**：
- 可以指定多个包路径
- 如果不使用 `@MapperScan`，需要在每个 Mapper 接口上使用 `@Mapper` 注解

---

## 2. Web层注解

### 2.1 @RestController

**作用**：组合了 `@Controller` 和 `@ResponseBody`，表示这是一个 REST 风格的控制器，方法的返回值会自动序列化为 JSON。

**使用场景**：RESTful API 控制器

**代码示例**：

```28:32:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/dashboard/controller/app/DashboardAppController.java
@Tag(name = "Dashboard App - 仪表盘")
@RestController
@RequestMapping("/api/dashboard")
@Validated
public class DashboardAppController {
```

**说明**：
- 等同于 `@Controller + @ResponseBody`
- 所有方法的返回值都会自动转换为 JSON 响应

### 2.2 @RequestMapping

**作用**：映射 HTTP 请求到控制器方法，可以指定请求路径、请求方法等。

**使用场景**：定义控制器的基础路径

**代码示例**：

```29:31:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/dashboard/controller/app/DashboardAppController.java
@RestController
@RequestMapping("/api/dashboard")
@Validated
```

**常用属性**：
- `value` 或 `path`：请求路径
- `method`：请求方法（GET、POST、PUT、DELETE 等）
- `produces`：响应内容类型
- `consumes`：请求内容类型

### 2.3 @GetMapping

**作用**：`@RequestMapping(method = RequestMethod.GET)` 的简写，用于处理 GET 请求。

**使用场景**：查询操作

**代码示例**：

```40:45:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/dashboard/controller/app/DashboardAppController.java
    @GetMapping("/summary")
    @Operation(summary = "获取统计概览")
    public CommonResult<DashboardSummaryRespVO> getSummary(@Valid DashboardSummaryReqVO reqVO) {
        DashboardSummaryRespVO result = dashboardService.getSummary(reqVO);
        return success(result);
    }
```

### 2.4 @PostMapping

**作用**：`@RequestMapping(method = RequestMethod.POST)` 的简写，用于处理 POST 请求。

**使用场景**：创建操作

**代码示例**：

```34:39:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/movie/controller/app/MovieAppController.java
    @PostMapping
    @Operation(summary = "创建影视记录")
    public CommonResult<MovieCreateRespVO> createMovie(@Valid @RequestBody MovieCreateReqVO reqVO) {
        MovieCreateRespVO respVO = movieService.createMovie(reqVO);
        return success(respVO);
    }
```

### 2.5 @PutMapping

**作用**：`@RequestMapping(method = RequestMethod.PUT)` 的简写，用于处理 PUT 请求。

**使用场景**：更新操作

**代码示例**：

```44:52:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/movie/controller/app/MovieAppController.java
    @PutMapping("/records/{id}")
    @Operation(summary = "更新观看记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> updateMovieRecord(@PathVariable("id") Long id,
                                                    @Valid @RequestBody MovieRecordUpdateReqVO reqVO) {
        reqVO.setId(id);
        movieService.updateMovieRecord(reqVO);
        return success(true);
    }
```

### 2.6 @DeleteMapping

**作用**：`@RequestMapping(method = RequestMethod.DELETE)` 的简写，用于处理 DELETE 请求。

**使用场景**：删除操作

**代码示例**：

```78:84:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/movie/controller/app/MovieAppController.java
    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除观看记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> deleteMovieRecord(@PathVariable("id") Long id) {
        movieService.deleteMovieRecord(id);
        return success(true);
    }
```

### 2.7 @PathVariable

**作用**：从 URL 路径中获取变量值。

**使用场景**：RESTful API 中的路径参数

**代码示例**：

```57:63:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/movie/controller/app/MovieAppController.java
    @GetMapping("/{id}")
    @Operation(summary = "获取影视详情")
    @Parameter(name = "id", description = "影视ID", required = true)
    public CommonResult<MovieRespVO> getMovie(@PathVariable("id") Long id) {
        MovieRespVO respVO = movieService.getMovie(id);
        return success(respVO);
    }
```

**说明**：
- 如果路径变量名和方法参数名相同，可以省略 `@PathVariable` 的值
- 例如：`@PathVariable Long id` 等同于 `@PathVariable("id") Long id`

### 2.8 @RequestParam

**作用**：从 HTTP 请求参数中获取值（查询参数）。

**使用场景**：获取 URL 查询参数或表单参数

**代码示例**：

```60:67:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/dashboard/controller/app/DashboardAppController.java
    @GetMapping("/recent-activities")
    @Operation(summary = "获取最近活动")
    public CommonResult<List<RecentActivityRespVO>> getRecentActivities(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        List<RecentActivityRespVO> result = dashboardService.getRecentActivities(limit);
        return success(result);
    }
```

**常用属性**：
- `value` 或 `name`：参数名
- `required`：是否必需，默认 `true`
- `defaultValue`：默认值

### 2.9 @RequestBody

**作用**：将 HTTP 请求体（通常是 JSON）绑定到方法参数对象。

**使用场景**：接收 JSON 格式的请求数据

**代码示例**：

```36:38:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/movie/controller/app/MovieAppController.java
    public CommonResult<MovieCreateRespVO> createMovie(@Valid @RequestBody MovieCreateReqVO reqVO) {
        MovieCreateRespVO respVO = movieService.createMovie(reqVO);
        return success(respVO);
```

**说明**：
- 通常配合 `@Valid` 或 `@Validated` 使用进行参数校验
- Content-Type 必须是 `application/json`

### 2.10 @ModelAttribute

**作用**：将请求参数绑定到模型对象，通常用于表单提交。

**使用场景**：文件上传时同时接收其他表单参数

**代码示例**：

```38:42:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/photo/controller/app/PhotoAppController.java
    @PostMapping("/upload")
    @Operation(summary = "上传照片")
    public CommonResult<PhotoRespVO> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute @Valid PhotoUploadReqVO reqVO) {
```

**说明**：
- 适用于 `application/x-www-form-urlencoded` 或 `multipart/form-data` 请求
- 可以自动将表单字段映射到对象属性

---

## 3. 服务层注解

### 3.1 @Service

**作用**：标识一个类为业务服务层组件，Spring 会自动扫描并注册为 Bean。

**使用场景**：业务逻辑服务类

**代码示例**：

```57:60:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/dashboard/service/impl/DashboardServiceImpl.java
@Service
@Validated
@Slf4j
public class DashboardServiceImpl implements DashboardService {
```

**说明**：
- 是 `@Component` 的特化形式
- 语义上更明确，表示这是业务服务层

### 3.2 @Component

**作用**：通用的组件注解，标识一个类为 Spring 组件，会被自动扫描并注册为 Bean。

**使用场景**：通用组件类

**说明**：
- `@Service`、`@Repository`、`@Controller` 都是 `@Component` 的特化形式
- 通常用于工具类、辅助类等

### 3.3 @Repository

**作用**：标识一个类为数据访问层组件，是 `@Component` 的特化形式。

**使用场景**：数据访问层（DAO）类

**说明**：
- 在 Spring Data JPA 中使用较多
- 本项目使用 MyBatis-Plus，Mapper 接口使用 `@Mapper` 注解

---

## 4. 依赖注入注解

### 4.1 @Resource

**作用**：按名称（byName）注入依赖，如果找不到名称匹配的 Bean，则按类型（byType）注入。

**使用场景**：注入依赖对象

**代码示例**：

```34:35:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/dashboard/controller/app/DashboardAppController.java
    @Resource
    private DashboardService dashboardService;
```

**说明**：
- 来自 JSR-250 标准（javax.annotation.Resource）
- 优先按名称匹配，名称匹配失败则按类型匹配
- 可以指定 `name` 属性指定 Bean 名称

### 4.2 @Autowired

**作用**：按类型（byType）自动注入依赖。

**使用场景**：注入依赖对象

**说明**：
- Spring 框架提供的注解
- 按类型匹配，如果找到多个同类型 Bean，会按名称匹配
- 可以配合 `@Qualifier` 指定 Bean 名称

**注意**：本项目主要使用 `@Resource`，因为它是 JSR 标准，更通用。

### 4.3 @Value

**作用**：从配置文件中读取值并注入到字段或方法参数。

**使用场景**：读取配置文件中的配置项

**代码示例**：

```17:21:backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/security/config/SecurityConfig.java
    @Value("${jwt.secret:interest-tracker-default-secret}")
    private String jwtSecret;

    @Value("${jwt.expire-seconds:604800}") // 默认 7 天
    private long jwtExpireSeconds;
```

**说明**：
- 支持 SpEL 表达式
- 可以使用 `${property:defaultValue}` 格式指定默认值
- 可以注入基本类型、String、数组等

---

## 5. 配置类注解

### 5.1 @Configuration

**作用**：标识一个类为配置类，相当于 XML 配置文件，可以定义 Bean。

**使用场景**：Spring 配置类

**代码示例**：

```13:14:backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/mybatis/core/config/MybatisPlusConfig.java
@Configuration
public class MybatisPlusConfig {
```

**说明**：
- 配置类本身也是 Spring Bean
- 可以配合 `@Bean` 注解定义 Bean

### 5.2 @Bean

**作用**：在配置类中定义 Bean，方法的返回值会被注册为 Spring Bean。

**使用场景**：在配置类中创建 Bean 实例

**代码示例**：

```16:22:backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/mybatis/core/config/MybatisPlusConfig.java
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
```

**说明**：
- 方法名默认作为 Bean 名称
- 可以通过 `@Bean(name = "beanName")` 指定名称
- Bean 默认是单例的

---

## 6. 数据校验注解

### 6.1 @Valid

**作用**：触发 JSR-303 Bean Validation 校验，通常用于方法参数。

**使用场景**：校验请求参数

**代码示例**：

```42:42:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/dashboard/controller/app/DashboardAppController.java
    public CommonResult<DashboardSummaryRespVO> getSummary(@Valid DashboardSummaryReqVO reqVO) {
```

**说明**：
- 需要配合 JSR-303 注解（如 `@NotNull`、`@NotBlank`、`@Size` 等）使用
- 校验失败会抛出 `MethodArgumentNotValidException` 或 `BindException`

### 6.2 @Validated

**作用**：Spring 提供的校验注解，功能类似 `@Valid`，但支持分组校验和方法级别校验。

**使用场景**：类级别校验和方法参数校验

**代码示例**：

```28:32:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/dashboard/controller/app/DashboardAppController.java
@Tag(name = "Dashboard App - 仪表盘")
@RestController
@RequestMapping("/api/dashboard")
@Validated
public class DashboardAppController {
```

**说明**：
- 可以放在类上，表示该类支持方法级别校验
- 支持分组校验（`@Validated(Group.class)`）
- 可以校验方法参数和返回值

---

## 7. MyBatis-Plus注解

### 7.1 @Mapper

**作用**：标识一个接口为 MyBatis Mapper 接口，MyBatis 会为其生成代理实现类。

**使用场景**：MyBatis Mapper 接口

**代码示例**：

```13:18:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/travel/dal/mysql/TravelPlaceMapper.java
@Mapper
public interface TravelPlaceMapper extends BaseMapperX<TravelPlaceDO> {
    // 自定义查询方法
}
```

**说明**：
- 如果不使用 `@MapperScan`，每个 Mapper 接口都需要此注解
- 接口需要继承 `BaseMapper<T>` 或 `BaseMapperX<T>`

### 7.2 @TableName

**作用**：指定实体类对应的数据库表名。

**使用场景**：实体类（DO）与数据库表映射

**代码示例**：

```19:23:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/travel/dal/dataobject/TravelRecordDO.java
@TableName("travel_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TravelRecordDO extends BaseDO {
```

**说明**：
- 如果表名与类名相同（忽略大小写），可以省略此注解
- 支持动态表名（需要配置）

### 7.3 @TableId

**作用**：标识主键字段，可以指定主键生成策略。

**使用场景**：实体类的主键字段

**代码示例**：

```28:29:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/travel/dal/dataobject/TravelRecordDO.java
    @TableId(type = IdType.AUTO)
    private Long id;
```

**常用主键策略**：
- `IdType.AUTO`：数据库自增
- `IdType.ASSIGN_ID`：雪花算法生成 ID
- `IdType.ASSIGN_UUID`：UUID
- `IdType.NONE`：无策略

### 7.4 @TableField

**作用**：指定实体类字段与数据库列的映射关系。

**使用场景**：字段名与列名不一致，或需要特殊处理

**代码示例**：

```36:37:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/photo/dal/dataobject/PhotoDO.java
    @TableField("user_id")
    private Long userId;
```

**常用属性**：
- `value`：数据库列名
- `exist`：字段是否存在于数据库，默认 `true`
- `fill`：字段自动填充策略（如创建时间、更新时间）

---

## 8. 事务管理注解

### 8.1 @Transactional

**作用**：声明式事务管理，方法执行时自动开启事务，方法执行完成后自动提交或回滚。

**使用场景**：需要事务保证的方法

**代码示例**：

```45:86:backend/interest-tracker-server/src/main/java/com/interest/tracker/module/travel/service/impl/TravelServiceImpl.java
    @Transactional(rollbackFor = Exception.class)
    @Override
    public TravelCreateRespVO createTravel(TravelCreateReqVO reqVO) {
        // 业务逻辑...
    }
```

**常用属性**：
- `rollbackFor`：指定哪些异常需要回滚，默认只回滚 `RuntimeException` 和 `Error`
- `propagation`：事务传播行为（如 `REQUIRED`、`REQUIRES_NEW` 等）
- `isolation`：事务隔离级别
- `readOnly`：是否只读事务，默认 `false`
- `timeout`：事务超时时间（秒）

**说明**：
- 本项目使用 `rollbackFor = Exception.class`，确保所有异常都回滚
- 事务注解只能作用于 public 方法
- 同一个类内部方法调用不会触发事务（需要使用代理）

---

## 9. 异常处理注解

### 9.1 @RestControllerAdvice

**作用**：全局异常处理器，组合了 `@ControllerAdvice` 和 `@ResponseBody`，用于统一处理异常。

**使用场景**：全局异常处理

**代码示例**：

```24:26:backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/web/core/handler/GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
```

**说明**：
- 可以指定 `basePackages` 限定扫描范围
- 可以指定 `annotations` 限定特定注解的控制器
- 配合 `@ExceptionHandler` 使用

### 9.2 @ExceptionHandler

**作用**：指定方法处理特定类型的异常。

**使用场景**：在异常处理器中定义异常处理方法

**代码示例**：

```31:37:backend/interest-tracker-server/src/main/java/com/interest/tracker/framework/web/core/handler/GlobalExceptionHandler.java
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionHandler]", ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        assert fieldError != null;
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }
```

**说明**：
- 可以处理多个异常类型：`@ExceptionHandler({Exception1.class, Exception2.class})`
- 方法返回值会自动序列化为 JSON（因为使用了 `@RestControllerAdvice`）
- 异常处理方法的优先级：精确匹配 > 父类匹配

---

## 总结

### 注解分类速查表

| 分类 | 注解 | 主要作用 |
|------|------|----------|
| **核心启动** | `@SpringBootApplication` | 启动类标识 |
| | `@MapperScan` | Mapper 扫描 |
| **Web层** | `@RestController` | REST 控制器 |
| | `@RequestMapping` | 请求映射 |
| | `@GetMapping` | GET 请求 |
| | `@PostMapping` | POST 请求 |
| | `@PutMapping` | PUT 请求 |
| | `@DeleteMapping` | DELETE 请求 |
| | `@PathVariable` | 路径变量 |
| | `@RequestParam` | 请求参数 |
| | `@RequestBody` | 请求体 |
| | `@ModelAttribute` | 模型属性 |
| **服务层** | `@Service` | 服务组件 |
| | `@Component` | 通用组件 |
| | `@Repository` | 数据访问组件 |
| **依赖注入** | `@Resource` | 按名称/类型注入 |
| | `@Autowired` | 按类型注入 |
| | `@Value` | 配置值注入 |
| **配置类** | `@Configuration` | 配置类标识 |
| | `@Bean` | Bean 定义 |
| **数据校验** | `@Valid` | 参数校验 |
| | `@Validated` | 分组校验 |
| **MyBatis-Plus** | `@Mapper` | Mapper 接口 |
| | `@TableName` | 表名映射 |
| | `@TableId` | 主键标识 |
| | `@TableField` | 字段映射 |
| **事务管理** | `@Transactional` | 事务管理 |
| **异常处理** | `@RestControllerAdvice` | 全局异常处理 |
| | `@ExceptionHandler` | 异常处理方法 |

### 最佳实践建议

1. **Controller 层**：
   - 使用 `@RestController` 而非 `@Controller`
   - 使用 `@Validated` 启用参数校验
   - 使用 `@Valid` 或 `@Validated` 校验请求参数

2. **Service 层**：
   - 使用 `@Service` 标识服务类
   - 使用 `@Validated` 启用方法级别校验
   - 使用 `@Transactional` 管理事务，建议使用 `rollbackFor = Exception.class`

3. **依赖注入**：
   - 优先使用 `@Resource`（JSR 标准）
   - 使用 `@Value` 读取配置值

4. **异常处理**：
   - 使用 `@RestControllerAdvice` 统一处理异常
   - 针对不同异常类型使用 `@ExceptionHandler` 分别处理

5. **MyBatis-Plus**：
   - 实体类使用 `@TableName` 指定表名
   - 主键使用 `@TableId(type = IdType.AUTO)` 指定自增策略
   - 字段名与列名不一致时使用 `@TableField` 映射

---

## 参考资料

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Framework 官方文档](https://spring.io/projects/spring-framework)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [JSR-303 Bean Validation](https://beanvalidation.org/)

---

**文档版本**：v1.0  
**最后更新**：2026年1月18日

