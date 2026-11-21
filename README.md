# MiuixGuiExample

基于 [miuix](https://github.com/compose-miuix-ui/miuix)（HyperOS 风格 Compose 组件库）搭建的界面事例工程，
可以直接当新应用的外壳，也可以当作 UI 结构的参考实现。

## 界面能力

- 两种贴底导航栏：HyperOS 玻璃底栏、iOS 液态玻璃底栏（液态玻璃实现在 `ui/component/liquid/`）。
- 宽屏自动切换为左侧 NavigationRail，窄屏只保留底栏。
- 连续曲率（squircle）圆角：`ui/theme/SquircleShape.kt` 基于 `androidx.graphics.shapes` 的 `RoundedPolygon` 生成。
- 背景特效（OS2 / OS3 动态背景）、模糊、沉浸式 edge-to-edge。
- 声明式配置系统：`prefs/OptionSpec.kt` 描述配置项，`ui/component/pref/` 负责渲染成卡片。
- 多语言（中 / 英）、深浅色 / Monet 主题、应用图标隐藏、配置导入导出、应用内更新检查。

## 工程结构

```
app/src/main/java/com/katiusu/miuixgui/example/
├── AppSettings.kt / MainActivity.kt / TemplateApp.kt
├── prefs/            配置项声明、存储、导入导出
├── ui/component/     通用组件（底栏、卡片、模糊、液态玻璃、搜索、配置卡片）
├── ui/screen/        页面（首页 / 功能 / 设置 / 关于 / 子页面）
├── ui/theme/         主题与连续曲率圆角
└── util/             系统版本探测
```

## 构建

要求：JDK 21、Android SDK Platform 37.2（`compileSdk` 需要 37.2 才能解析 miuix 0.9.5 的 AAR）、Build-Tools 36。

```bash
./gradlew :app:assembleDebug
```

仓库内置 GitHub Actions（`.github/workflows/android-build.yml`），push 到 `main` 会自动构建 debug APK 并作为 artifact 上传。

## 许可

The Unlicense（公有领域）。第三方依赖许可见 [NOTICE](NOTICE)。
