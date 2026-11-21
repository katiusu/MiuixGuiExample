# MiuixExample

Miuix（HyperOS 风格 Compose 组件库）界面**事例工程**：把一整套可用的页面骨架摆出来当例子，
悬浮玻璃底栏、玻璃/液态玻璃表面、连续曲率圆角、edge-to-edge、深浅色切换、引导页、设置页与子页路由。

## 它是什么

- 一个**能跑起来的事例**，不是一个需要装进系统的模块：所有页面都是可直接复用、可直接改的 Compose 代码。
- 界面样式全部来自 [miuix](https://github.com/compose-miuix-ui/miuix) 官方组件，不自己造控件。
- 所有圆角都是**连续曲率**（squircle），几何由 `miuix-squircle` 提供。
- 不包含 hook / Xposed / LSPosed / Root 相关的任何框架与选项。

## 目录结构

```
android/
├─ app/src/main/kotlin/com/katiusu/miuixexample/
│  ├─ MainActivity.kt                 # edge-to-edge 入口 + 引导页/主界面分支
│  ├─ AppLocaleController.kt
│  ├─ compose/
│  │  ├─ navigation/AppShell.kt       # 应用外壳：Predictive Back、子页路由、悬浮底栏
│  │  ├─ component/                   # 底栏、液态玻璃、对话框宿主、偏好组件、Backdrop
│  │  ├─ page/                        # 首页 / 应用 / 设置 / 关于 / 更新日志 / 引导页
│  │  ├─ page/settings/               # 外观、备份恢复、预设、隐藏行为、材质…
│  │  ├─ service/                     # 配置读写、更新检查、系统信息
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

`appVersionName` / `appVersionCode` 是必填 Gradle property，`gradle.properties` 已给默认值，
可用 `-PappVersionName=1.0.0 -PappVersionCode=1` 覆盖。

CI：推送到 `main` 或手动 `workflow_dispatch` 即在 GitHub Actions 上出包，产物为 artifact `app-debug`。

## 改成自己的应用

1. `applicationId` 与 `namespace`（`android/app/build.gradle.kts`）；
2. 包名目录 `com/katiusu/miuixexample` 整体重命名，源码里的包引用同步替换；
3. `res/values/strings.xml` 的 `app_name`、图标资源、`values/themes.xml`；
4. 版本号与签名配置（未配置 release 签名时回退 debug 签名）。

## 连续曲率圆角

`compose/theme/SquircleShape.kt` 用 miuix 的 `addSquircleRect` 生成路径：

```kotlin
Card(modifier = Modifier.clip(squircleShape(26.dp)))
```

不要再用 `RoundedCornerShape`：它画的是圆弧转角，和 HyperOS 的连续曲率不是一回事。

## 许可

MIT，见 [LICENSE](LICENSE)；第三方组件与上游声明见 [NOTICE](NOTICE)。
