# VPN 使用说明

## 问题

即使打开了 VPN 并且浏览器可以正常访问外网，Java 应用仍然无法连接到 TMDB API。

## 原因

Java 应用默认**不会自动使用系统代理设置**，需要手动配置。

## 解决方案

### 方案一：让 Java 使用系统代理（已自动配置）

我已经更新了 `RestTemplateConfig`，现在会自动检测并使用系统代理设置。

**操作步骤**：

1. **确保 VPN 已连接**
   - 检查 VPN 是否正常工作（可以访问 ChatGPT 等外网）

2. **检查 VPN 是否设置了系统代理**
   - Windows：设置 → 网络和 Internet → 代理
   - 查看是否启用了"使用代理服务器"

3. **重启应用**
   - 重启 Spring Boot 应用
   - 代码会自动检测系统代理并使用

### 方案二：通过 JVM 参数设置代理（如果方案一不行）

如果 VPN 没有设置系统代理，或者方案一不工作，可以通过 JVM 参数手动设置：

#### 在 IDE 中配置（IntelliJ IDEA / Eclipse）

**IntelliJ IDEA**：
1. Run → Edit Configurations
2. 选择你的 Spring Boot 应用配置
3. 在 "VM options" 中添加：
   ```
   -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7890 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7890
   ```
   （将 `7890` 替换为你的 VPN 代理端口）

**Eclipse**：
1. Run → Run Configurations
2. 选择你的应用配置
3. 在 "Arguments" → "VM arguments" 中添加上述参数

#### 通过命令行启动

```bash
java -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7890 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7890 -jar your-app.jar
```

#### 如何获取 VPN 代理端口

不同 VPN 软件的默认端口不同：
- **Clash**: 7890 (HTTP), 7891 (SOCKS)
- **V2Ray**: 10808 (SOCKS), 10809 (HTTP)
- **Shadowsocks**: 1080 (SOCKS)
- **其他 VPN**: 查看 VPN 软件的设置或文档

### 方案三：在 application.yml 中配置代理

如果知道 VPN 的代理地址和端口，可以在 `application.yml` 中配置：

```yaml
spring:
  http:
    proxy:
      host: 127.0.0.1      # VPN 本地代理地址（通常是 127.0.0.1）
      port: 7890           # VPN 代理端口（根据你的 VPN 软件调整）
```

常见 VPN 代理端口：
- Clash: 7890
- V2Ray: 10809
- Shadowsocks: 1080

## 快速诊断

### 1. 检查 VPN 代理端口

**Windows**：
```powershell
# 查看系统代理设置
netsh winhttp show proxy
```

**或者查看 VPN 软件设置**：
- 打开 VPN 软件
- 查看"本地代理"或"HTTP 代理"设置
- 记录端口号（通常是 7890、10808、10809 等）

### 2. 测试代理是否工作

```bash
# 使用 curl 测试代理
curl -x http://127.0.0.1:7890 https://api.themoviedb.org
```

（将 `7890` 替换为你的 VPN 代理端口）

### 3. 检查 Java 是否使用了代理

在应用启动时，查看日志中是否有代理相关信息。

## 推荐操作步骤

1. **确认 VPN 已连接**
   - 可以正常访问 ChatGPT 等外网

2. **检查 VPN 代理端口**
   - 打开 VPN 软件查看代理设置
   - 记录 HTTP 代理端口（通常是 7890）

3. **配置代理**
   - **方式 A**：如果 VPN 设置了系统代理，直接重启应用即可（代码已自动支持）
   - **方式 B**：在 `application.yml` 中配置代理（见方案三）
   - **方式 C**：通过 JVM 参数设置（见方案二）

4. **重启应用并测试**

## 常见 VPN 代理端口

| VPN 软件 | HTTP 代理端口 | SOCKS 代理端口 |
|---------|--------------|---------------|
| Clash | 7890 | 7891 |
| V2Ray | 10809 | 10808 |
| Shadowsocks | 1080 | 1080 |
| Surge | 6152 | 6153 |

## 注意事项

1. **确保 VPN 已连接**：VPN 必须处于连接状态
2. **使用正确的端口**：不同 VPN 软件的端口不同
3. **HTTP vs SOCKS**：优先使用 HTTP 代理端口
4. **重启应用**：修改配置后必须重启应用

## 如果仍然无法连接

1. 检查 VPN 是否真的在工作（浏览器可以访问外网）
2. 确认 VPN 的代理端口号
3. 尝试在 `application.yml` 中直接配置代理
4. 查看应用日志，确认是否使用了代理

