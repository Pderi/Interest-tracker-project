# COS配置文件说明

## 配置文件说明

为了安全起见，腾讯云COS的敏感配置信息（SecretId、SecretKey等）已从 `application.yml` 中分离出来，存放在独立的配置文件中。

## 配置文件

- **`application-cos.yml`** - 实际的COS配置（包含敏感信息，已添加到.gitignore）
- **`application-cos.yml.example`** - 配置示例文件（可提交到代码仓库）

## 使用步骤

1. **复制示例文件**
   ```bash
   cd backend/interest-tracker-server/src/main/resources
   cp application-cos.yml.example application-cos.yml
   ```

2. **填写实际配置**
   打开 `application-cos.yml`，填写你的腾讯云COS配置信息：
   - `region` - 地域
   - `secret-id` - 访问密钥ID
   - `secret-key` - 访问密钥Key
   - `bucket-name` - 存储桶名称
   - `domain` - 访问域名

3. **验证配置**
   启动应用后，检查日志中是否有COS客户端初始化成功的消息。

## 配置优先级

Spring Boot会按以下优先级加载配置：

1. **`application-cos.yml`**（最高优先级）
2. **环境变量**（如 `TENCENT_COS_SECRET_ID`）
3. **`application.yml` 中的默认值**（最低优先级）

## 安全提示

⚠️ **重要**：
- `application-cos.yml` 文件已添加到 `.gitignore`，不会被提交到代码仓库
- 请勿将包含真实密钥的配置文件提交到Git
- 建议使用环境变量或配置中心管理敏感信息
- 如果密钥泄露，请立即在腾讯云控制台重置密钥

## 其他配置方式

如果不想使用外部配置文件，也可以通过以下方式配置：

### 方式1：环境变量
```bash
export TENCENT_COS_SECRET_ID=your-secret-id
export TENCENT_COS_SECRET_KEY=your-secret-key
export TENCENT_COS_REGION=ap-shanghai
export TENCENT_COS_BUCKET_NAME=your-bucket-name
export TENCENT_COS_DOMAIN=your-domain
```

### 方式2：IDE运行配置
在IDE的运行配置中设置环境变量（推荐用于本地开发）。

## 参考文档

详细配置步骤请参考：`backend/docs/腾讯云COS配置指南.md`

