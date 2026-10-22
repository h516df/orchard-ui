# 云端开发

默认在云端执行，不依赖用户旧电脑。先读 AGENTS、README、HANDOFF，核对当前分支、远端与相关 PR。当前环境、工具和凭据需每次检查，仓库文件不等于账户级或跨会话全局设置。

## 工具链

| 部分 | 本轮验证版本 | 初始化与命令 |
| --- | --- | --- |
| 共享变量与检查 | Python 3.12 | `bash cloud/check.sh quick` |
| Compose 与 Catalog | JDK21、SDK36/Build Tools36.0.0、Gradle9.1.0、AGP9.0.1、Kotlin2.3.20 | `bash cloud/setup.sh` 后 `source .cloud/env.sh`；`bash cloud/check.sh android` |
| Flutter | Flutter3.41.5 / Dart3.11.3 | 官方 SDK；`bash cloud/check.sh flutter` |
| React Native | Node24、RN0.83.2 / React19.2 | `bash cloud/check.sh react-native` |
| Web CSS | Playwright1.56.1、Chromium | `npm ci`、`npx playwright install chromium`；`bash cloud/check.sh web` |

`all` 按以上顺序检查全部。缺少工具时明确失败，不把静态守卫当成业务通过。已有依赖使用缓存；第一次构建需网络下载。

## Android 初始化

需要完整 JDK，必须同时有 `java` 和 `javac`，只有 JRE 会构建失败。用平台提供的 JDK 或官方发行版设置 `JAVA_HOME`，不要修改系统 HOME。

`cloud/setup.sh` 只初始化 Android SDK 与 `.cloud/env.sh`：命令行工具固定包号14742923、SHA256校验，SDK36/BuildTools36.0.0。没有下载正式签名或私人数据。需要平台网络规则允许官方依赖域名。

在带代理与自定义 CA 的云环境使用继承的代理和可信证书。脚本把无认证代理映射为 Java 参数；若系统有 `/etc/ssl/certs/java/cacerts`，将其作为 Java 信任库用于保持当前环境 CA 信任。不关闭 TLS 校验，不把凭据写进仓库。`.cloud/` 被忽略。

本轮临时 JDK/Flutter SDK 路径仅属于当前工作区，没有写入可移植配置。Flutter SDK 版本3.41.5对应提交 `2c9eb20739dfec95e2c74bd3dfa4601b0a8a36aa`。

## 私有源码与产物

仓库为 private。代码、验证摘要与合成截图进入 Git；APK/AAR、node_modules、SDK 与构建缓存不入 Git。展示 App 包名 `dev.h516.orchard.catalog`，独立于用户已有 App；不要把开发样本当作正式应用升级包。

GitHub Actions 提供手动 **Validate adapters** 工作流，不自动发布包或站点。用户旧项目已记录 Actions 账务/附件配额问题，本批不改预算、付费或删除历史产物，也不依赖其恢复来完成本地云端验证。本轮没有运行 Actions，不能把 YAML 已保存称为远端 CI 已通过。

## 续接

先读 HANDOFF 的当前记录，再核对 GitHub。规则、源码与验证要一起推送，准确提交由远端读回；不能把这台 VM 当长期备份。现有 App 接入是各项目后续独立任务，不在本库创建时自动改写其他仓库。

## 0.4 Flutter独立宿主

`examples/flutter-liquid`提供Android/Web可运行示例，`bash cloud/check.sh flutter`同时检查适配层与音乐宿主。在宿主目录运行`flutter build apk --debug`或`flutter build web --no-web-resources-cdn`。首次Android构建可能需下载Flutter固定的NDK28.2.13676358/CMake3.22.1/BuildTools35；通过环境代理和可信CA，不关闭TLS校验。仅本地构建，不触发Actions或公开部署。
