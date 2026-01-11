# 腾讯云COS配置指南

本文档将详细说明如何在腾讯云COS上获取配置信息，并在后端程序中完成配置。

## 目录

1. [准备工作](#准备工作)
2. [在腾讯云控制台获取配置信息](#在腾讯云控制台获取配置信息)
3. [添加Maven依赖](#添加maven依赖)
4. [配置application.yml](#配置applicationyml)
5. [验证配置](#验证配置)
6. [常见问题](#常见问题)

---

## 准备工作

### 1. 注册腾讯云账号

如果没有腾讯云账号，请先访问 [腾讯云官网](https://cloud.tencent.com/) 注册账号并完成实名认证。

### 2. 开通COS服务

1. 登录腾讯云控制台
2. 进入 [对象存储COS控制台](https://console.cloud.tencent.com/cos)
3. 如果首次使用，系统会提示开通服务，按照提示完成开通

---

## 在腾讯云控制台获取配置信息

### 步骤1：创建存储桶（Bucket）

1. 登录 [COS控制台](https://console.cloud.tencent.com/cos)
2. 点击左侧菜单 **存储桶列表**
3. 点击 **创建存储桶** 按钮
4. 填写存储桶信息：
   - **名称**：自定义存储桶名称（例如：`interest-tracker-photos-1234567890`）
     - 注意：存储桶名称必须全局唯一，建议使用项目名称+随机数字
   - **所属地域**：选择离你最近的地域（例如：`ap-beijing` 北京）
     - 常用地域代码：
       - `ap-beijing` - 北京
       - `ap-shanghai` - 上海
       - `ap-guangzhou` - 广州
       - `ap-chengdu` - 成都
   - **访问权限**：选择 **私有读写**（推荐）或 **公有读私有写**
   - **其他设置**：保持默认即可
5. 点击 **确定** 完成创建

**记录以下信息：**
- ✅ 存储桶名称（Bucket Name）：`interest-tracker-photos-1234567890`
- ✅ 所属地域（Region）：`ap-beijing`

### 步骤2：获取访问密钥（SecretId 和 SecretKey）

1. 登录腾讯云控制台
2. 鼠标悬停在右上角头像上，点击 **访问管理**
3. 在左侧菜单选择 **API密钥管理**
4. 如果已有密钥，直接查看；如果没有，点击 **新建密钥** 创建
5. 点击 **显示** 查看 SecretKey（首次查看后请妥善保存，后续无法再次查看）

**记录以下信息：**
- ✅ SecretId：`YOUR_SECRET_ID_HERE`
- ✅ SecretKey：`YOUR_SECRET_KEY_HERE`

**⚠️ 安全提示：**
- SecretKey 是敏感信息，请妥善保管，不要泄露
- 建议使用子账号密钥，并设置最小权限原则
- 不要将密钥提交到代码仓库

### 步骤3：获取存储桶访问域名

1. 在COS控制台，进入你创建的存储桶
2. 点击左侧菜单 **域名与传输管理** > **默认域名**
3. 找到 **默认CDN加速域名** 或 **默认COS域名**

**记录以下信息：**
- ✅ 访问域名（Domain）：`interest-tracker-photos-1234567890.cos.ap-beijing.myqcloud.com`
  - 或者使用CDN加速域名（如果已配置CDN）

### 步骤4：配置CORS（跨域资源共享，可选）

如果前端需要直接上传文件到COS，需要配置CORS：

1. 在存储桶页面，点击 **安全管理** > **跨域访问CORS设置**
2. 点击 **添加规则**
3. 配置如下：
   - **来源Origin**：填写前端域名（例如：`http://localhost:3000` 或 `https://yourdomain.com`）
   - **操作Methods**：勾选 `GET`、`PUT`、`POST`、`DELETE`、`HEAD`
   - **Allow-Headers**：`*`
   - **Expose-Headers**：`ETag`、`x-cos-request-id`
   - **Max-Age**：`600`
4. 点击 **确定**

---

## 添加Maven依赖

### 在 `interest-tracker-server/pom.xml` 中添加腾讯云COS SDK依赖

打开 `backend/interest-tracker-server/pom.xml`，在 `<dependencies>` 标签内添加：

```xml
<!-- 腾讯云COS SDK -->
<dependency>
    <groupId>com.qcloud</groupId>
    <artifactId>cos_api</artifactId>
    <version>5.6.89</version>
</dependency>
```

**完整示例：**

```xml
<dependencies>
    <!-- 其他依赖... -->
    
    <!-- 腾讯云COS SDK -->
    <dependency>
        <groupId>com.qcloud</groupId>
        <artifactId>cos_api</artifactId>
        <version>5.6.89</version>
    </dependency>
</dependencies>
```

### 刷新Maven依赖

在IDE中刷新Maven项目，或执行命令：

```bash
mvn clean install
```

---

## 配置application.yml

### 在 `application.yml` 中添加COS配置

打开 `backend/interest-tracker-server/src/main/resources/application.yml`，在文件末尾添加：

```yaml
# 腾讯云COS配置
tencent:
  cos:
    # 地域（Region），例如：ap-beijing、ap-shanghai、ap-guangzhou
    region: ap-beijing
    
    # 访问密钥ID（SecretId）
    secret-id: YOUR_SECRET_ID_HERE
    
    # 访问密钥Key（SecretKey）
    secret-key: YOUR_SECRET_KEY_HERE
    
    # 存储桶名称（Bucket Name）
    bucket-name: interest-tracker-photos-1234567890
    
    # 访问域名（Domain），用于生成文件访问URL
    domain: interest-tracker-photos-1234567890.cos.ap-beijing.myqcloud.com
    
    # 文件路径前缀（可选，默认为 photos/）
    path-prefix: photos/
    
    # 缩略图路径前缀（可选，默认为 thumbnails/）
    thumbnail-prefix: thumbnails/
    
    # 连接超时时间（毫秒，可选，默认30000）
    connection-timeout: 30000
    
    # Socket超时时间（毫秒，可选，默认30000）
    socket-timeout: 30000
```

### 使用环境变量（推荐，更安全）

为了安全起见，建议将敏感信息（SecretId、SecretKey）配置为环境变量：

**方式1：在 `application.yml` 中使用环境变量**

```yaml
# 腾讯云COS配置
tencent:
  cos:
    region: ${TENCENT_COS_REGION:ap-beijing}
    secret-id: ${TENCENT_COS_SECRET_ID:}
    secret-key: ${TENCENT_COS_SECRET_KEY:}
    bucket-name: ${TENCENT_COS_BUCKET_NAME:interest-tracker-photos-1234567890}
    domain: ${TENCENT_COS_DOMAIN:interest-tracker-photos-1234567890.cos.ap-beijing.myqcloud.com}
    path-prefix: photos/
    thumbnail-prefix: thumbnails/
    connection-timeout: 30000
    socket-timeout: 30000
```

**方式2：在系统环境变量中设置**

**Windows：**
```cmd
set TENCENT_COS_SECRET_ID=YOUR_SECRET_ID_HERE
set TENCENT_COS_SECRET_KEY=YOUR_SECRET_KEY_HERE
```

**Linux/Mac：**
```bash
export TENCENT_COS_SECRET_ID=YOUR_SECRET_ID_HERE
export TENCENT_COS_SECRET_KEY=YOUR_SECRET_KEY_HERE
```

**方式3：在IDE运行配置中设置环境变量**

- **IntelliJ IDEA**：Run > Edit Configurations > Environment variables
- **Eclipse**：Run > Run Configurations > Environment

### 配置示例（完整）

```yaml
# 腾讯云COS配置
tencent:
  cos:
    # 地域（必填）
    region: ap-beijing
    
    # 访问密钥（必填，建议使用环境变量）
    secret-id: ${TENCENT_COS_SECRET_ID:}
    secret-key: ${TENCENT_COS_SECRET_KEY:}
    
    # 存储桶配置（必填）
    bucket-name: interest-tracker-photos-1234567890
    domain: interest-tracker-photos-1234567890.cos.ap-beijing.myqcloud.com
    
    # 路径前缀（可选）
    path-prefix: photos/
    thumbnail-prefix: thumbnails/
    
    # 超时配置（可选）
    connection-timeout: 30000
    socket-timeout: 30000
```

---

## 验证配置

### 1. 检查配置类是否正确加载

启动应用后，检查日志中是否有配置加载错误。

### 2. 测试COS连接（可选）

可以创建一个简单的测试接口来验证COS配置：

```java
@RestController
@RequestMapping("/api/test")
public class CosTestController {
    
    @Resource
    private TencentCosProperties cosProperties;
    
    @GetMapping("/cos-config")
    public CommonResult<Map<String, String>> getCosConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("region", cosProperties.getRegion());
        config.put("bucketName", cosProperties.getBucketName());
        config.put("domain", cosProperties.getDomain());
        // 注意：不要返回 SecretId 和 SecretKey
        return success(config);
    }
}
```

访问 `http://localhost:8080/api/test/cos-config` 查看配置是否正确加载。

---

## 常见问题

### 1. 配置不生效

**问题**：配置了 `application.yml`，但程序读取不到配置。

**解决方案**：
- 检查 `TencentCosProperties` 类上的 `@ConfigurationProperties(prefix = "tencent.cos")` 是否正确
- 确保 `@Configuration` 注解存在
- 检查YAML格式是否正确（缩进使用空格，不是Tab）
- 重启应用

### 2. SecretKey 包含特殊字符

**问题**：SecretKey 中包含特殊字符（如 `+`、`/`、`=`），导致配置解析错误。

**解决方案**：
- 使用环境变量方式配置
- 或者在YAML中使用引号包裹：`secret-key: "your-secret-key"`

### 3. 地域代码错误

**问题**：上传文件时提示地域错误。

**解决方案**：
- 确认存储桶创建时选择的地域
- 检查 `region` 配置是否正确
- 常用地域代码参考：
  - `ap-beijing` - 北京
  - `ap-shanghai` - 上海
  - `ap-guangzhou` - 广州
  - `ap-chengdu` - 成都
  - `ap-chongqing` - 重庆
  - `ap-singapore` - 新加坡

### 4. 访问权限问题

**问题**：上传文件失败，提示权限不足。

**解决方案**：
- 检查 SecretId 和 SecretKey 是否正确
- 确认存储桶的访问权限设置
- 检查子账号权限（如果使用子账号）

### 5. 域名访问问题

**问题**：文件上传成功，但无法通过域名访问。

**解决方案**：
- 检查存储桶访问权限是否为"公有读"或"公有读写"
- 如果使用私有存储桶，需要使用预签名URL访问
- 确认域名配置正确

### 6. Maven依赖下载失败

**问题**：无法下载 `cos_api` 依赖。

**解决方案**：
- 检查网络连接
- 尝试添加阿里云Maven镜像（在 `pom.xml` 或 `settings.xml` 中）
- 确认Maven版本是否支持

---

## 下一步

配置完成后，可以：

1. 实现 `TencentCosService` 服务类（上传、下载、删除文件）
2. 在 `PhotoServiceImpl` 中集成COS服务
3. 实现图片处理功能（缩略图生成、EXIF提取）

详细实现请参考：`backend/docs/摄影模块开发方案-腾讯云COS集成.md`

---

## 参考链接

- [腾讯云COS官方文档](https://cloud.tencent.com/document/product/436)
- [COS Java SDK文档](https://cloud.tencent.com/document/product/436/10199)
- [COS控制台](https://console.cloud.tencent.com/cos)
- [API密钥管理](https://console.cloud.tencent.com/cam/capi)

---

## 配置检查清单

在完成配置后，请确认以下项目：

- [ ] 已在腾讯云创建存储桶
- [ ] 已获取 SecretId 和 SecretKey
- [ ] 已记录存储桶名称和地域
- [ ] 已获取访问域名
- [ ] 已在 `pom.xml` 中添加COS SDK依赖
- [ ] 已在 `application.yml` 中配置COS参数
- [ ] 已设置环境变量（如果使用）
- [ ] 已测试配置是否正确加载
- [ ] 已配置CORS（如果需要前端直传）

完成以上所有步骤后，COS配置就完成了！🎉

