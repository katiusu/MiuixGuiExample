<!--suppress HtmlDeprecatedAttribute, HtmlDeprecatedTag -->
<div align="center">

# MiuixGuiExample

### 从 MiuixGuiTemplate 提取的纯 GUI 工程

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Miuix](https://img.shields.io/badge/Miuix-0.9.4-blue)
![Compose](https://img.shields.io/badge/Compose-BOM%202026.09.00-4285F4)
![License](https://img.shields.io/badge/License-Unlicense-blue)

</div>

**MiuixGuiExample** 是从 [MiuixGuiTemplate](https://github.com/Ianzb/MiuixGuiTemplate)（一个基于 [libxposed API 102](https://libxposed.github.io/api/index-all.html) 的 LSPosed 模块模板）中**单独提取出来的 GUI 部分**：Xposed / Hook / DexKit / 安全模式 / 作用域等相关内容已全部剥离，界面层代码（Compose 页面、主题、组件、特效、动画与资源）保持原样，可直接作为普通 Android 应用构建、安装与运行，也可当作 Miuix（HyperOS 风格）Compose 应用的起手模板。

<br>

# 下载体验

不想自己构建？直接装演示包：[**MiuixGuiExample 1.0 演示包**](https://github.com/katiusu/MiuixGuiExample/releases/latest)（`MiuixGuiExample-1.0-debug.apk`，universal，约 40 MB）

- 系统要求 Android 14 (API 34) 及以上；debug 签名，仅用于演示体验，请勿用于正式分发
- 无 Hook 环境下首页状态卡恒显示「未激活」，「重启应用」按钮依赖 Root，「已生效 / 未生效」为本地记录

<br>

# 界面能力（保留自原模板）

- **四标签页外壳** — 主页 / 功能 / 设置 / 关于，`HorizontalPager` + 弹簧切页动画，禁用横滑手势避免与次级手势冲突
- **三种底栏形态** — 普通 `NavigationBar`、悬浮 `FloatingNavigationBar`、iOS 风格液态玻璃底栏（`LiquidGlassNavigationBar` + `CombinedBackdrop` / `InnerShadow` / `Lens` / `Vibrancy`）
- **宽屏适配** — 宽屏自动切 `NavigationRail`（`shouldShowSplitPane()` / `shouldExpandNavigationRail()`），窄屏自动切换状态卡排布
- **顶栏渐变模糊** — 基于 [Haze](https://github.com/chrisbanes/haze) 的 `BlurredBar`：模糊半径、渐变强度、表面色叠加与深色主题判断集中在 `TopBarBlurConfig` / `rememberBlurState()` / `Modifier.blurSource()`
- **主题与配色** — `AppTheme(themeMode)` 支持 6 档 `ColorSchemeMode`（System / Light / Dark / MonetSystem / MonetLight / MonetDark），并同步系统状态栏、导航栏与窗口背景
- **背景特效组件** — `BgEffectBackground` + `BgEffectPainter` / `BgEffectModifier`（`OS2BgFrag` / `OS3BgFrag` 两种风格片段）
- **配置系统** — `OptionSpec` / `OptionRegistry` / `ConfigState` / `PrefsStore` 驱动的声明式配置项，`HookOptionView` 按类型自动渲染开关 / 复选框 / 箭头 / 下拉 / 单选 / 滑块 / 文本 / 包名列表卡片，`HookOptionsPage` 提供分区 + 子页面 + 全局搜索
- **对话框与动效** — `WindowDialog` 系列（滑块取值、文本取值、包名列表、系统重启确认、更新提示）、`DampedDragAnimation` / `InteractiveHighlight` / `folmeSpring` 动效规格
- **设置页** — 界面（主题模式 / 悬浮底栏 / 液态玻璃 / 模糊开关）、语言（跟随系统 / 简体中文 / English，切换后 `recreate()`）、更新（启动自动检查 + 手动检查）、数据（配置 JSON 导出 / 导入）
- **关于页** — Logo 折叠渐显顶栏、`textureBlur` 纹理模糊卡片、版本号、项目地址 / 群组 / 许可证入口，以及应用内第三方许可证与致谢页
- **示例功能页** — `featureSpecs()` 内置每种组件类型的示例卡片与子页面（含子页面搜索），便于照葫芦画瓢
- **应用内检查更新** — 启动自动检查与手动检查，发现新版本弹窗展示更新说明并跳转下载页
- **语言与图标** — `LocaleHelper` 包语言（`attachBaseContext`）；桌面图标由 `activity-alias` + `LauncherIconController` 控制，启动时默认隐藏

<br>

# 与原模板的差异（剥离清单）

| 项 | 处理 |
|---|---|
| `:hook` 模块（BaseHook / BaseLoad / DexKit / nativehook / rule / safemode / status / xposed 等） | **整个删除** |
| 依赖 `libxposed-api` / `libxposed-service` / `dexkit` | 已从 `libs.versions.toml` 与 `app/build.gradle.kts` 移除 |
| `packaging.resources.merges += "META-INF/xposed/*"`、`META-INF/xposed/*` 资源 | 已删除 |
| `MainActivity` 的 `de.robv.android.xposed.category.MODULE_SETTINGS` intent-filter | 已删除（改为普通 `MAIN` + `LAUNCHER`） |
| `HookStatusReceiver`（Xposed 广播回报接收器）、`SafeModeReader`、`RootHelper`、`AppRestarter` 原实现 | 已删除，改为 `bridge/` 下的同名桩实现（见下） |
| 作用域列表页 `ScopeListActivity`、安全模式管理页 `SafeModeActivity` | 按需求删除（入口按钮同步移除） |
| 设置页「模块」分区（安全模式 / 申请作用域 / 清空 DexKit 缓存 / 当前设备类型下拉） | 已删除，分区整体移除 |
| 主页状态卡的「作用域申请」入口 | 已改为仅刷新本地状态（不再跳转已删除的页面） |
| `build.gradle.kts` 的 `splits.abi`（仅 arm64-v8a）与 release 签名配置 | 已删除通用 ABI 切分，保留默认产物；签名配置仍按环境变量读取 |
| `docs/`、`.github/workflows/`、`gradle/gradle-daemon-jvm.properties` | 已删除（前者随 hook 文档一并移除，后者要求 JDK 25） |

**`bridge/` 桩实现**（保持原 `XposedServiceManager` / `HookStatusStore` 的 API 形状，使界面层调用点无需改动）：

- `bridge/XposedServiceManager` — `isActivated` 恒为 `false`，`scope` 恒为空；`checkRoot()` 仍以 `su -c id` 探测 Root 并驱动主页状态卡；`ensureScope()` 直接回调成功，`removeScope()` 为空操作
- `bridge/HookStatusStore` — 本地 device-protected `SharedPreferences` 记录「已生效」配置键，`rememberHookApplied()` 的「已生效 / 未生效」文案继续工作（无 Hook 回报，仅本地记录）
- `bridge/AppRestarter` — 内联 `su -c` 执行能力，保留「重启应用」对话框的行为路径
- `device/DeviceType` + `device/DeviceContext` — 设备形态探测（手机 / 平板 / 折叠屏，读 `persist.sys.multi_display_type`、`miui.os.Build.IS_TABLET`、`smallestScreenWidthDp`），覆盖项改由本地 `PrefsStore` 读取

<br>

# 系统要求

- Android 14+（`minSdk 34`，`targetSdk 34`，`compileSdk 37`）
- 构建环境：JDK 21、Gradle 9.7.1（`gradlew` 自带 wrapper）、Android SDK `platforms;android-37.0` + `build-tools;36.0.0`
- 主页 Root 状态卡依赖设备已授权 `su`；「清空 DexKit 缓存」「申请作用域」等按钮已随剥离移除

<br>

# 构建

```bash
# Debug APK
./gradlew assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk

# Release APK（需要根目录 release.keystore 与下列环境变量）
# KEYSTORE_PASSWORD / KEY_ALIAS（默认 release）/ KEY_PASSWORD
./gradlew assembleRelease
```

## aarch64 / PRoot 容器上构建的注意事项

本工程在一台 aarch64 Android 设备的 PRoot Ubuntu 容器内验证通过，以下三点在该环境下是必需的：

1. **aapt2 必须指向 aarch64 原生版本**：`build-tools;37.0.0` 在 SDK 仓库里只有 x86_64 产物，容器跑不了；因此 `app/build.gradle.kts` 显式声明 `buildToolsVersion = "36.0.0"`（其中 `aapt2` 是 aarch64 原生 ELF，可正常解析 `android-37.0` 的 `resources.arsc`），并在 `gradle.properties` 或全局配置里设置：
   ```properties
   android.aapt2FromMavenOverride=/opt/android-sdk/build-tools/36.0.0/aapt2
   ```
2. **`GRADLE_USER_HOME` 放在容器内文件系统**：`/sdcard` 是 FAT 类共享存储，无法 `mmap` 执行本地库（`libnative-platform.so` 加载失败），缓存放容器内即可。
3. **用 `--no-daemon` 单进程构建**：容器内 `fcntl` 文件锁不可靠，多个 Gradle 守护进程会互相抢 `journal-1.lock` 导致 `Could not create service of type FileAccessTimeJournal ... java.io.IOException: Operation not permitted`。构建完请确认没有残留守护进程，否则下一次构建会再次失败。

```bash
cd <工程目录>
LANG=C.UTF-8 LC_ALL=C.UTF-8 JAVA_HOME=/usr/lib/jvm/java-21-openjdk-arm64 \
ANDROID_HOME=/opt/android-sdk ANDROID_SDK_ROOT=/opt/android-sdk \
./gradlew --no-daemon --console=plain :app:assembleDebug
```

> 在标准 x86_64 开发机（Android Studio）上无需上述任何特殊配置。

<br>

# 目录结构

```
app/src/main/java/com/katiusu/miuixgui/example/
├── MainActivity.kt              # 四标签页外壳 + 三种底栏 + 宽屏导航栏
├── TemplateApp.kt               # Application：初始化配置系统与桥接对象
├── AppSettings.kt               # 应用级设置（主题模式 / 底栏 / 模糊 / 语言）
├── LauncherIconController.kt    # 桌面图标默认隐藏（activity-alias 启停）
├── LocaleHelper.kt              # 语言切换与包 Context
├── UpdateChecker.kt             # GitHub Release 检查更新
├── LicenseActivity.kt
├── bridge/                      # 原 Xposed 侧对象的桩实现（API 形状保持不变）
├── device/                      # 设备形态探测（DeviceType / DeviceContext）
├── prefs/                       # OptionSpec / OptionRegistry / ConfigState / PrefsStore / ConfigBackup
├── ui/component/                # 卡片、对话框、子页面骨架、动画（animation/）、特效（effect/）、液态玻璃（liquid/）
├── ui/component/pref/           # 各类型配置卡片 + 分区页（HookOptionsPage）
├── ui/icons/                    # 手写 ImageVector 状态图标
├── ui/screen/                   # home / features / settings / about / subpage
├── ui/theme/                    # AppTheme + ColorSchemeMode
├── ui/util/                     # BlurUtils / MiuixAnimations / WindowBackground
└── util/                        # SystemVersionDetector
```

<br>

# 二次开发

1. 改包名 / 应用名：`app/build.gradle.kts` 的 `namespace` / `applicationId`、`settings.gradle.kts` 的 `rootProject.name`、`res/values*/strings.xml` 的 `app_name`（若改包名，`LauncherIconController.LAUNCHER_ALIAS_CLASS` 与 `ExampleInstrumentedTest` 也要同步）。
2. 换应用图标：`res/drawable/ic_launcher_*.xml`、`res/mipmap-*/ic_launcher*`。
3. 改关于页链接：`strings.xml` 的 `about_source_code_summary`、`about_telegram_summary`；更新检查仓库改 `UpdateChecker.REPO`。
4. 增删配置项：在 `ui/screen/features/FeaturesPage.kt` 的 `featureSpecs()` 里声明 `OptionSpec`，界面会自动渲染对应组件；子页面用 `HookSubPage` 并入功能页搜索。
5. 增删页面：页面继承 `BaseSubPageActivity` 即可自动套用主题、背景与顶栏模糊。

<br>

# 已知取舍

- **桌面图标默认隐藏且无设置项**：应用每次启动都会禁用 `activity-alias`，安装后不会出现桌面图标；原模板隐藏图标后可从 LSPosed 管理器进入设置页，剥离后已无第二个入口。若需要桌面入口，可把 `LauncherIconController.HIDE_BY_DEFAULT` 改为 `false` 后重新构建。
- **「已生效 / 未生效」为本地状态**：无 Hook 回报机制，仅由 `HookStatusStore` 记录本地配置变更，语义上等同于「已修改」。
- **「重启应用」依赖 `su`**：`bridge/AppRestarter` 通过 `su -c` 执行 `am force-stop` 与重启；无 Root 时对话框会提示失败。
- **作用域相关入口已移除**：`ensureScope()` 为直接成功的桩实现，主页状态卡恒显示「未激活」，这是剥离 Xposed 后的预期表现。

<br>

# 第三方库

- [miuix](https://github.com/compose-miuix-ui/miuix) — HyperOS 风格 Compose UI 组件库（0.9.4）
- [Haze](https://github.com/chrisbanes/haze) — Compose 背景模糊（1.7.3）
- [AndroidX Compose](https://developer.android.com/jetpack/compose) — 声明式 UI 框架（BOM 2026.09.00）
- [Material Icons](https://developer.android.com/jetpack/androidx/releases/compose-material) — 图标资源（1.7.8）

<br>

# 来源与致谢

本工程的界面层代码最初参考 [Ianzb/MiuixGuiTemplate](https://github.com/Ianzb/MiuixGuiTemplate)（LGPL-3.0）整理而来，只保留纯 GUI 部分，Xposed / Hook 相关实现与依赖均已剥离。感谢 [Miuix](https://github.com/compose-miuix-ui/miuix) 项目作者与所有开源贡献者。

<br>

# 许可证

本项目以 [The Unlicense](LICENSE) 发布：放弃全部著作权，进入公有领域，可自由复制、修改、分发（含商业用途），无需署名。

第三方依赖（Miuix、AndroidX、Haze 等）仍按其各自许可证（主要为 Apache-2.0）授权，详见应用内「关于 → 第三方许可证与致谢」。
