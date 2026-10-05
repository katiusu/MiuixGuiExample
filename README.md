<div align="center">

# MiuixGuiExample

一个基于 [Miuix](https://github.com/Yukonga/miuix) 组件库的 Android 纯 GUI 示例工程：四个标签页、三种底部导航形态、随滚动渐进的模糊顶栏，以及一套完整的设置 / 关于 / 开源许可页面。

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Miuix](https://img.shields.io/badge/Miuix-0.9.4-blue)
![Compose](https://img.shields.io/badge/Compose-BOM%202026.09.00-blue)
![License](https://img.shields.io/badge/License-Apache--2.0-orange)

[下载演示包](https://github.com/katiusu/MiuixGuiExample/releases/latest) · [更新日志](changelog.md)

</div>

---

## 简介

本工程是 MIUI / HyperOS 风格 GUI 的最小可用骨架：所有界面能力（导航、模糊、动效、偏好组件、主题、多语言、配置导入导出、更新检查、开源许可展示）都是完整可运行的实现，但不包含任何 Xposed / LSPosed 功能代码 —— 原先依赖模块框架的调用点由 `bridge/` 下的纯 GUI 桩实现补齐，界面调用签名保持一致，因此可以直接在此之上开发自己的界面与业务。

## 界面能力

- **四个标签页**：首页、功能、设置、关于，使用 `HorizontalPager` 承载，底栏点击后以弹簧动画切页。
- **三种底部导航形态**：普通底栏、悬浮底栏（圆角胶囊）、液态玻璃底栏（折射 + 高光），可在设置页实时切换。
- **宽屏自适应**：宽度 ≥ 840dp 时普通底栏自动变为左侧 `NavigationRail`，≥ 1200dp 自动展开为完整侧栏。
- **渐进模糊顶栏**：基于 Haze，顶栏背景随内容滚动渐进加深，内容滚到底时收成不透明。
- **纹理模糊底栏**：基于 Miuix `textureBlur` + `rememberLayerBackdrop`，底栏对页面内容做真实模糊。
- **动效背景**：关于页顶部的 MIUI/HyperOS 风格流动背景（OS2 / OS3 两套 painter）。
- **完整设置页**：主题（含 Monet 系统取色）、悬浮底栏 / 液态玻璃 / 模糊总开关、语言（跟随系统 / 简体中文 / English）、启动时检查更新、配置导出与导入（JSON）。
- **关于页**：应用图标与版本、玻璃信息卡、开源许可页、源码 / 频道入口、检查更新。
- **功能页**：以开关、复选框、箭头、下拉、单选、滑块、文本框、应用列表等组件示例，演示 `HookOptionsPage` / `HookSection` / `SubPageScaffold` 的用法。
- **多语言**：`values/`（简体中文）与 `values-en/`（英文）两套字符串资源，条目一一对应；切换语言即时重建界面。

## 切页性能

模板结构在切页时会有可感知的掉帧，本工程针对三处热区做了处理（`MainActivity.kt`、`ui/screen/about/AboutPage.kt`）：

1. **动效背景按需播放**：关于页的动效背景内部是每帧重绘的帧循环，同时又是该页多张 `textureBlur` 卡片的模糊来源。现在通过 `AboutPageContent(animateBackground = …)`，只在「关于页可见且不在切页动画中」时才播放，离开该页或处于切页动画期间彻底停掉帧循环。
2. **切页动画期间挂起底栏模糊**：`val navBlurActive = blurActive && !isNavigating`。动画期间页面内容逐帧位移，整页 backdrop 会被逐帧重录、模糊被逐帧重算，是最主要的开销；动画期间底栏 / 侧栏改用实色，整页 `layerBackdrop` 也同步停止录制，动画结束后立即恢复。
3. **预组合全部页面**：`beyondViewportPageCount = 3`，避免跨页跳转时目标页在动画进行中才首次组合（关于页的首次组合最贵：图标解码 + 多张模糊卡片 + 动效 painter）。

## 与参考模板的差异

本工程的页面结构最初参考 [Ianzb/MiuixGuiTemplate](https://github.com/Ianzb/MiuixGuiTemplate) 做了一次纯 GUI 提取，之后按自己的需要做了调整与优化：

| 方面 | 本工程 |
| --- | --- |
| 模块框架 | 无任何 Xposed / LSPosed 代码，`bridge/` 下为保留原签名的纯 GUI 桩实现（`isActivated` 恒 `false`、作用域恒为空） |
| 页面 | 仅保留 GUI：首页 / 功能 / 设置 / 关于 / 开源许可 / 功能子页 |
| 设置页 | 精简为界面、语言、更新、数据四组；无隐藏桌面图标开关，无设备类型下拉 |
| 关于页 | 无模板来源标注，许可条目指向 Apache-2.0 |
| 性能 | 见上一节的三项切页优化（本项目实现） |
| 桌面图标 | 仅 `MainActivity` 单一入口（不再使用 `activity-alias` 别名机制） |

## 系统要求

- Android 8.0（API 26）以上；编译 `compileSdk 37`，`minSdk 34`，`targetSdk 34`。
- Android Studio（或命令行 Gradle）+ JDK 21。
- 模糊效果依赖运行时着色器，Android 13（API 33）以上才生效；低版本自动回退为不透明顶栏 / 底栏。

## 构建

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-arm64
export ANDROID_HOME=/opt/android-sdk
./gradlew :app:assembleDebug
```

产物：`app/build/outputs/apk/debug/app-debug.apk`。

Release 包需要 `release.keystore` 与三个环境变量：`KEYSTORE_PASSWORD`、`KEY_ALIAS`（默认 `release`）、`KEY_PASSWORD`。

### 在 aarch64 / PRoot 容器里构建

1. `app/build.gradle.kts` 固定使用 `buildToolsVersion = "36.0.0"`：该版本的 aapt2 有 aarch64 二进制，更新的 build-tools 只有 x86_64 版 aapt2，容器里无法执行。
2. Gradle 主目录放在容器文件系统内，不要放在 `/sdcard`（共享主目录可能因文件锁不可靠而报 `FileAccessTimeJournal … java.io.IOException: Operation not permitted`）：

   ```bash
   GRADLE_USER_HOME=/root/.gradle-miuix ./gradlew --no-daemon --console=plain :app:assembleDebug
   ```

3. 容器里建议加 `--no-daemon`，避免残留 daemon 互相抢锁。

## 目录结构

```
app/src/main/java/com/katiusu/miuixgui/example/
├── MainActivity.kt                 # 入口：设置状态、HorizontalPager、底栏 / 侧栏
├── AppSettings.kt                  # 设置项数据类与持久化
├── LocaleHelper.kt                 # 语言切换
├── UpdateChecker.kt                # GitHub Release 更新检查
├── TemplateApp.kt                  # Application
├── LicenseActivity.kt              # 开源许可页宿主
├── bridge/                         # 原模块框架 API 的纯 GUI 桩实现
├── device/                         # 设备类型判定
├── prefs/                          # 选项注册表与配置存储 / 导入导出
├── util/                           # 系统版本探测
└── ui/
    ├── component/
    │   ├── animation/              # 阻尼拖拽与按压高光动画
    │   ├── effect/                 # 动效背景 painter / modifier
    │   ├── liquid/                 # 液态玻璃折射、高光、内阴影
    │   └── pref/                   # HookOptionsPage、各类偏好卡片
    ├── icons/                      # 状态图标
    ├── screen/                     # home / features / settings / about / subpage
    ├── theme/                      # AppTheme
    └── util/                       # BlurUtils（Haze 顶栏、textureBlur 底栏、断点）
app/src/main/res/values[-en]/strings.xml   # 中英文案（两份条目一一对应）
```

## 二次开发

1. 改包名：`app/build.gradle.kts` 的 `namespace` / `applicationId`，以及 `app/src/main/AndroidManifest.xml` 与源码目录结构。
2. 改应用名与图标：`res/values/strings.xml` 的 `app_name`、`res/mipmap-anydpi/ic_launcher.xml` 等图标资源。
3. 改关于页链接与更新源：`strings.xml` 的 `about_source_code_summary`、`about_telegram_summary`，以及 `UpdateChecker.REPO`（当前仍是 `your-name/your-repo` 占位符，需替换为自己的仓库）。
4. 接入自己的功能：`FeaturesPage.kt` 里用 `OptionSpec` / `HookSection` 描述选项，`HookOptionsPage` 会自动生成界面、搜索与依赖关系。
5. 删掉用不到的示例：`ui/component/effect/`、`ui/component/liquid/`、`prefs/` 与 `bridge/` 可整体移除，只要同步清掉引用点即可。

## 已知取舍

- 模糊基于运行时着色器与 RenderEffect，低端机或长时间滚动仍有开销；不需要时可在设置页关闭总开关。
- 切页动画期间底栏会短暂变为实色（页面背景本身就是 surface 色，肉眼几乎无差异；只有关于页背后是动效背景时能看出细微变化），这是为了避免动画期间逐帧重算模糊。
- `beyondViewportPageCount = 3` 让四个页面常驻组合，换来切页时不再中途组合新页面，代价是内存与冷启动组合量略增。

## 第三方依赖

| 库 | 版本 | 说明 |
| --- | --- | --- |
| Miuix（`top.yukonga.miuix.kmp`） | 0.9.4 | MIUI / HyperOS 风格 Compose 组件库（core / ui / shader / blur / preference / icons / squircle / navigation） |
| Haze（`dev.chrisbanes.haze`） | 1.7.3 | 顶栏渐进模糊 |
| AndroidX Compose | BOM 2026.09.00 | Compose、Material3、Activity Compose |
| Material Icons Core | 1.7.8 | 图标 |
| AndroidLiquidGlass（Kyant0） | — | `ui/component/liquid/`、`ui/component/animation/` 的折射、高光与阻尼动画移植自该项目（Apache-2.0） |

## 致谢

- 感谢 [Miuix](https://github.com/Yukonga/miuix) 提供了整套 MIUI 风格组件。
- 感谢 [Haze](https://github.com/chrisbanes/haze) 提供的渐进模糊实现。
- 感谢 [Kyant0/AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) 提供的液态玻璃参考实现。
- 感谢 [Ianzb/MiuixGuiTemplate](https://github.com/Ianzb/MiuixGuiTemplate)：本工程的页面结构最初由它做纯 GUI 提取而来（该仓库以 AGPL-3.0 发布，本工程只保留界面部分，未包含其模块框架相关实现）。

## 许可证

本项目以 [Apache License 2.0](LICENSE) 发布，可自由使用、修改与分发（含商业用途），需保留版权与许可声明。

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
