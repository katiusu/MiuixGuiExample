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

## 流畅度：请用 release 包

debug 构建的固定开销远大于任何界面层优化，**测流畅度必须用 release 包**。同一份代码实测：

| | debug | release |
| --- | --- | --- |
| `debuggable` | `true`（ART 不做优化） | `false` |
| 依赖库基线配置 `assets/dexopt/baseline.prof` | 无 | 有（含 `baseline.prof` + `baseline.profm`） |
| R8 代码压缩 / 优化 | 无 | 有（`proguard-android-optimize.txt`） |
| APK 体积 | 42.99 MB | 2.99 MB |

因此本工程与参考模板一致，把 R8 与资源处理都放在 release 构建上（见 `app/build.gradle.kts`）。用 debug 包体验界面动效时出现掉帧，先换 release 包复测。

### 页面层能做的优化

参考模板 MiuixGuiTemplate 的页面代码与本工程逐行相同（见下节对比），切页掉帧不是某一页写法的问题，而是渲染路径上的固定开销。本工程**没有关闭任何模糊、没有改底栏、也没有改页面结构**，只减少每帧实际工作量（改动集中在 `MainActivity.kt` 与 `ui/component/effect/BgEffectModifier.kt`）：

1. **每个页面各占一个硬件层**：pager 的每一页外层包一个 `Modifier.graphicsLayer()`。切页动画里 pager 只需把已经渲染好的页面图层按新偏移合成，页面内部的卡片、顶栏模糊与动效背景不必逐帧重绘（等价于 `ViewPager2` 给页面加硬件层的做法）。页面自身状态变化时图层照常失效重绘，视觉与交互完全不变。
2. **补齐上游的动效背景暂停逻辑**：`BgEffectModifier.kt` 抄自参考模板，比上游 Miuix 官方示例旧了一版。上游在背景完全透明（`alpha() <= 0f`，即肉眼不可见）时会取消帧循环、重新可见时再启动；本工程把这段补上，避免不可见的动效一直触发重绘。

模糊总开关的行为完全保留：只要它是开着的，顶栏、底栏与卡片的模糊在整个切页动画期间持续生效，不会中途变实色。

## 与参考模板的差异

本工程的页面结构最初参考 [Ianzb/MiuixGuiTemplate](https://github.com/Ianzb/MiuixGuiTemplate) 做了一次纯 GUI 提取，之后按自己的需要做了调整与优化：

| 方面 | 本工程 |
| --- | --- |
| 模块框架 | 无任何 Xposed / LSPosed 代码，`bridge/` 下为保留原签名的纯 GUI 桩实现（`isActivated` 恒 `false`、作用域恒为空） |
| 页面 | 仅保留 GUI：首页 / 功能 / 设置 / 关于 / 开源许可 / 功能子页 |
| 设置页 | 精简为界面、语言、更新、数据四组；无隐藏桌面图标开关，无设备类型下拉 |
| 关于页 | 无模板来源标注，许可条目指向 Apache-2.0 |
| 构建 | release 开启 R8 代码压缩与优化（`proguard-android-optimize.txt`）；因容器 aapt2 限制关闭了 release 资源优化，见「构建」一节 |
| 页面层性能 | 每个页面一个硬件层；动效背景暂停逻辑与上游 Miuix 示例对齐 |
| 桌面图标 | 仅 `MainActivity` 单一入口（不再使用 `activity-alias` 别名机制） |

页面代码层面的差异只有上述被删掉的功能：`FeaturesPage.kt`、`BlurUtils.kt`、`BgEffectBackground.kt`、`HookOptionsPage.kt`、功能子页与 `SubPageScaffold.kt` 与模板逐行相同；`HomePage.kt` 只少了作用域页入口；设置页 / 关于页 / 许可页 / `MainActivity.kt` 的差异全部是本工程删掉的模块分区与隐藏图标开关。

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

### 生成 release 包（体验流畅度请用这个）

```bash
export KEYSTORE_PASSWORD=… KEY_ALIAS=… KEY_PASSWORD=…
GRADLE_USER_HOME=/root/.gradle-miuix ./gradlew --no-daemon --console=plain :app:assembleRelease
```

产物：`app/build/outputs/apk/release/app-release.apk`。签名读取 `../release.keystore` 与上述三个环境变量；该文件已被 `.gitignore` 排除，需要自备。本地只想试装、而设备上已装的是 debug 签名版本时，可以把 debug keystore 复制成 `release.keystore`，用 `KEYSTORE_PASSWORD=android KEY_ALIAS=androiddebugkey KEY_PASSWORD=android` 签名，这样可以直接覆盖安装。

### 在 aarch64 / PRoot 容器里构建

1. `app/build.gradle.kts` 固定使用 `buildToolsVersion = "36.0.0"`：该版本的 aapt2 有 aarch64 二进制，更新的 build-tools 只有 x86_64 版 aapt2，容器里无法执行。
2. Gradle 主目录放在容器文件系统内，不要放在 `/sdcard`（共享主目录可能因文件锁不可靠而报 `FileAccessTimeJournal … java.io.IOException: Operation not permitted`）：

   ```bash
   GRADLE_USER_HOME=/root/.gradle-miuix ./gradlew --no-daemon --console=plain :app:assembleDebug
   ```

3. 容器里建议加 `--no-daemon`，避免残留 daemon 互相抢锁。
4. `gradle.properties` 里关闭了 `android.enableResourceOptimizations`：AGP 的 release 资源优化链路（`aapt2 optimize` + proto 格式）在 build-tools 36.0.0 的 aapt2 下会**静默产出空的 `resources-release-optimize.ap_`**，于是 `packageRelease` 打出一个没有 `AndroidManifest.xml` / `resources.arsc` 的坏包（`aapt2 dump badging` 会报 `could not identify format of APK`，`apksigner` 验不出证书，安装必然失败）。关闭后 release 与 debug 走同一条资源打包链路，只损失一点包体；工具链正常的机器上可以删掉这一行并按需打开 `isShrinkResources`。

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
6. 引入反射 / 序列化库后，记得在 `app/proguard-rules.pro` 补 `-keep` 规则（release 已启用 R8）。

## 已知取舍

- 模糊基于运行时着色器与 RenderEffect，低端机或长时间滚动仍有开销；不需要时可在设置页关闭总开关。
- 硬件层让每个在场的页面多占一份离屏缓冲；`beyondViewportPageCount = 1` 时通常只有 2 个页面同时在场，开销有限。
- release 包会混淆类名与成员名，崩溃堆栈需要配合 `app/build/outputs/mapping/release/mapping.txt` 还原。
- 关于页的动效背景是每帧重绘的帧循环（这是 Miuix 官方示例本身的行为），它在这一页被组合时会持续产生绘制开销；本工程按「设置开着模糊就不中途关闭效果」的原则保留该行为，未做按需暂停。

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
