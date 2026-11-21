# MiuixGui Example

一个以 [miuix](https://github.com/compose-miuix-ui/miuix)（HyperOS 风格 Compose 组件库）搭建的 Android 界面脚手架，MIT 许可，可直接拿来当新应用的壳。

界面样式全部来自 miuix 官方组件：本仓库不自己造按钮、不自己造底栏、不手写圆角样式。

## 界面

- **悬浮玻璃底栏**：`GlassNavigationBar` + `rememberLayerBackdrop` / `layerBackdrop` 背景采样层，底栏实时取下层内容做模糊；可在设置里关闭模糊（底栏随之停止采样）。
- **连续曲率圆角**：卡片、横幅、数字小卡统一用 `squircleClip(...)`（卡片 26dp，横幅 32dp），不使用普通 `RoundedCornerShape`。
- **边缘到边缘**：`enableEdgeToEdge()`，内容铺到状态栏与导航栏之下；页面底部预留 132dp 给悬浮底栏。
- **深浅色**：`ThemeController` + `MiuixTheme`，主题模式（跟随系统 / 浅色 / 深色）在设置页切换；系统栏图标颜色按实际 surface 亮度同步。

## 结构

```
app/src/main/java/com/katiusu/miuixgui/example/
├── MainActivity.kt                 入口：edge-to-edge + AppTheme + AppShell
├── AppShell.kt                     四标签页骨架、玻璃底栏、背景采样层
├── data/
│   ├── AppSettings.kt              SharedPreferences + Compose 可观察状态
│   └── DeviceInfo.kt               只读设备/版本信息
├── ui/theme/AppTheme.kt            ThemeController / MiuixTheme / 系统栏图标
├── ui/component/AppComponents.kt   分组卡片、数字小卡、横幅、页面标题（全部走 miuix）
└── ui/screen/
    ├── HomeScreen.kt               主页：横幅 + 设备信息
    ├── FeaturesScreen.kt           功能：界面能力说明
    ├── SettingsScreen.kt           设置：背景模糊、主题模式、重置
    └── AboutScreen.kt              关于：版本与许可声明
```

资源只有一套自适应图标（vector + shape 渐变），无位图依赖。

## 技术栈

| 项 | 版本 / 值 |
| --- | --- |
| Kotlin | 2.4.20 |
| AGP | 9.5.1 |
| Gradle | 9.7.1 |
| miuix | 0.9.5-beta03（core / ui / blur / preference / icons / squircle / nav / material / glass） |
| compileSdk | 37.2（miuix AAR 要求） |
| minSdk | 35 |

## 构建

本地构建需要 JDK 21 与 Android SDK（`compileSdk 37.2`、`build-tools 36.0.0`）：

```bash
./gradlew :app:assembleDebug
```

也可以直接推到 GitHub 由 CI 构建：`.github/workflows/android-build.yml` 会装好 SDK、跑 `assembleDebug`、校验 APK（条目数 / manifest / sha256）并把产物上传为 `app-debug` artifact。

## 作为脚手架使用

1. 改 `app/build.gradle.kts` 里的 `namespace` 与 `applicationId`。
2. 替换 `res/` 下的图标与 `values/strings.xml` 的 `app_name`。
3. 删除不需要的页面（`ui/screen/*`）并在 `AppShell.kt` 的 `ShellTab` 里增删标签。
4. 保留 `ui/theme`、`ui/component` 与 `AppShell` 的骨架写法——新页面的样式同样只取 miuix 组件。

## 许可

MIT，见 [LICENSE](LICENSE)。第三方组件声明见 [NOTICE](NOTICE)：

- miuix —— Apache-2.0
- AndroidX / Jetpack Compose —— Apache-2.0

本仓库不含来自任何其他项目的源代码或图片资源。
