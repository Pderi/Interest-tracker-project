# TMDB 网络连接问题解决方案

## 问题诊断

从错误日志和网络测试来看，当前网络环境**无法连接到 TMDB API**（`api.themoviedb.org`）。

**错误信息**：`Connect timed out` - 连接超时

**网络测试结果**：ping 测试显示 100% 丢包，说明网络环境无法访问 TMDB 服务器。

## 解决方案

### 方案一：配置代理（推荐）

如果你的网络环境需要通过代理才能访问外网，需要配置代理。

#### 1. 在 `application.yml` 中配置代理

```yaml
spring:
  http:
    proxy:
      host: your-proxy-host      # 代理服务器地址（如：proxy.company.com）
      port: 8080                 # 代理服务器端口
      username: your-username     # 如果需要认证（可选）
      password: your-password     # 如果需要认证（可选）
```

#### 2. 通过环境变量配置代理

**Windows (PowerShell)**：
```powershell
$env:HTTP_PROXY="http://proxy-host:port"
$env:HTTPS_PROXY="http://proxy-host:port"
```

**Windows (CMD)**：
```cmd
set HTTP_PROXY=http://proxy-host:port
set HTTPS_PROXY=http://proxy-host:port
```

**Linux/Mac**：
```bash
export HTTP_PROXY=http://proxy-host:port
export HTTPS_PROXY=http://proxy-host:port
```

#### 3. 如何获取代理信息

- **公司网络**：联系 IT 部门获取代理服务器地址和端口
- **个人网络**：检查系统代理设置
  - Windows：设置 → 网络和 Internet → 代理
  - Mac：系统偏好设置 → 网络 → 高级 → 代理

### 方案二：使用 VPN

如果网络环境限制访问国外 API，可以使用 VPN：

1. 连接 VPN
2. 重启应用
3. 再次测试

### 方案三：更换网络环境

- 使用手机热点
- 使用其他网络环境
- 在可以访问外网的环境中部署

### 方案四：临时使用 Mock 数据（仅用于开发测试）

如果暂时无法解决网络问题，可以启用 Mock 模式使用模拟数据继续开发：

#### 1. 启用 Mock 模式

在 `application.yml` 中：

```yaml
tmdb:
  mock:
    enabled: true  # 启用 Mock 模式
```

#### 2. 实现 Mock 数据

需要修改 `TMDBClient` 添加 Mock 数据返回逻辑（当前未实现，需要时再添加）。

**注意**：Mock 模式仅用于开发测试，生产环境必须使用真实的 TMDB API。

## 快速诊断步骤

### 1. 测试网络连接

```bash
# 测试 ping
ping api.themoviedb.org

# 测试 HTTPS 连接
curl -I https://api.themoviedb.org
```

### 2. 检查系统代理设置

**Windows**：
- 打开"设置" → "网络和 Internet" → "代理"
- 查看是否配置了代理服务器

**Mac**：
- 打开"系统偏好设置" → "网络" → "高级" → "代理"
- 查看是否配置了代理服务器

### 3. 测试代理连接

如果配置了代理，测试代理是否可用：

```bash
# 通过代理测试连接
curl -x http://proxy-host:port https://api.themoviedb.org
```

## 推荐操作步骤

1. **检查是否需要代理**
   - 查看系统代理设置
   - 询问 IT 部门是否需要配置代理

2. **配置代理**（如果需要）
   - 在 `application.yml` 中配置代理信息
   - 或通过环境变量配置

3. **重启应用**
   - 重启应用使配置生效

4. **测试连接**
   - 再次调用搜索接口测试

## 常见问题

### Q: 如何知道是否需要代理？

A: 
- 如果公司网络，通常需要代理
- 如果个人网络，检查系统代理设置
- 如果无法访问 Google、GitHub 等国外网站，通常需要代理或 VPN

### Q: 代理配置后仍然无法连接？

A:
1. 检查代理地址和端口是否正确
2. 检查是否需要认证（用户名/密码）
3. 检查代理是否支持 HTTPS
4. 联系 IT 部门确认代理配置

### Q: 可以使用 VPN 吗？

A: 可以，连接 VPN 后重启应用即可。

## 总结

当前问题是**网络环境限制**，不是代码问题。解决方案：

1. ✅ **配置代理**（如果网络需要代理）
2. ✅ **使用 VPN**（如果网络限制访问国外 API）
3. ✅ **更换网络环境**（使用可以访问外网的环境）

配置代理后，重启应用即可正常使用 TMDB API。

