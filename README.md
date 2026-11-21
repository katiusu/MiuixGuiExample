# IslandExample

Miuix（HyperOS 风格 Compose 组件库）界面模板的示例工程：悬浮玻璃底栏、玻璃/液态玻璃表面、
连续曲率圆角、edge-to-edge、深浅色切换、完整的设置页与子页导航骨架。

界面代码派生自 [HyperIsland](https://github.com/1812z/HyperIsland)（MIT），
本次蒸馏**只做渲染层改造，不改变界面结构与模块划分**：

- 全部圆角由 `RoundedCornerShape` 换成连续曲率（squircle），几何来自 `miuix-squircle`；
- 移除 Xposed / LSPosed / Root 相关的全部框架、上层建筑与对应设置项；
- 包名、应用名、域名、仓库地址统一为 example。

## 目录结构

```
android/
├─ app/src/main/kotlin/com/katiusu/islandexample/
│  ├─ MainActivity.kt                 # edge-to-edge 入口 + 引导页/主界面分支
│  ├─ AppLocaleController.kt
│  ├─ compose/
│  │  ├─ navigation/AppShell.kt       # 应用外壳：Predictive Back、子页路由、悬浮底栏
│  │  ├─ component/                   # 底栏、液态玻璃、对话框宿主、偏好组件、Backdrop
│  │  ├─ page/                        # 首页 / 应用 / 设置 / 关于 / 更新日志 / 引导页
│  │  ├─ page/settings/               # 外观、备份恢复、预设、隐藏行为、岛屿材质…
│  │  ├─ service/                     # 配置读写、更新检查、分析、系统信息
│  │  ├─ data/                        # 偏好仓库、站点数据模型、预设配置
│  │  └─ theme/                       # MiuixTheme 封装 + SquircleShape
│  ├─ data/ core/ homeassistant/ screenrecorder/ utils/
│  └─ ...
├─ gradle/libs.versions.toml          # 版本目录
└─ .github/workflows/android-build.yml# GitHub Actions 构建
```

## 技术栈

| 项 | 版本 |
|---|---|
| Kotlin | 2.4.21 |
| AGP | 9.5.1 |
| Gradle | 9.7.1 |
| miuix | 0.9.5-beta01（含 miuix-squircle） |
| Compose Multiplatform | 1.12.2 |
| compileSdk / targetSdk | 37.2 / 37 |
| minSdk | 33 |
| JDK | 21 |

## 构建

本地：

```bash
cd android
echo "sdk.dir=/path/to/android-sdk" > local.properties
./gradlew :app:assembleDebug
```

`appVersionName` / `appVersionCode` 是必填的 Gradle property，已在 `gradle.properties` 给出默认值，
可用 `-PappVersionName=1.0.0 -PappVersionCode=1` 覆盖。

CI：推送到 `main` 或手动 `workflow_dispatch` 即在 GitHub Actions 上出包，
产物为 artifact `app-debug`；构建日志失败时会把 Kotlin 错误逐条打成 annotation。

## 改成你自己的应用

1. `applicationId` 与 `namespace`（`android/app/build.gradle.kts`）；
2. 包名目录 `com/katiusu/islandexample` 整体重命名，源码里的包引用同步替换；
3. `res/values/strings.xml` 的 `app_name`、图标资源、`values/themes.xml`；
4. `gradle.properties` 的版本号与 `android/app/build.gradle.kts` 的签名配置（未配置时回退 debug 签名）。

## 连续曲率圆角

`compose/theme/SquircleShape.kt` 用 miuix 的 `addSquircleRect` 生成路径：

```kotlin
Card(modifier = Modifier.clip(squircleShape(26.dp)))
```

不要再用 `RoundedCornerShape`：它生成的是圆弧转角，与 HyperOS 的连续曲率不一致。

## 许可

MIT，见 [LICENSE](LICENSE)。界面代码的上游许可与改动摘要见 [NOTICE](NOTICE) 与
[LICENSE-HyperIsland](LICENSE-HyperIsland)。
