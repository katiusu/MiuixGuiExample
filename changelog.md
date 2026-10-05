# 更新日志

## 1.0.0

> 发布于 2026-10-03

本版本是 **MiuixGuiExample** 的首个版本：从 [MiuixGuiTemplate](https://github.com/Ianzb/MiuixGuiTemplate) 0.5.2 中提取**纯 GUI 部分**，剥离全部 Xposed / Hook 相关内容，界面层代码与资源保持原样。

### 变更（相对 MiuixGuiTemplate 0.5.2）

- **工程身份**：包名 `cn.ianzb.miuixguitemplate` → `com.katiusu.miuixgui.example`，应用名 → `MiuixGuiExample`，根工程名 → `MiuixGuiExample`，版本号重置为 `1.0`（`versionCode 1`）
- **删除 `:hook` 模块**：`BaseHook` / `BaseLoad` / `NativeHookHelper` / DexKit 缓存 / 版本与设备规则 / 安全模式管理 / Hook 状态上报 / `XposedEntry` 与 `META-INF/xposed/*` 全部移除；`settings.gradle.kts` 不再 `include(":hook")`
- **移除依赖**：`libxposed-api` / `libxposed-service` / `dexkit` 以及 `packaging.resources.merges += "META-INF/xposed/*"`
- **新增 `bridge/` 桩实现**（保持原 API 形状，界面层调用点零改动）：`XposedServiceManager`（`isActivated` 恒 `false`、仍以 `su -c id` 探测 Root、`ensureScope()` 直接成功）、`HookStatusStore`（改为本地 device-protected 记录）、`AppRestarter`（内联 `su -c` 执行）
- **`device/` 迁入应用侧**：`DeviceType` / `DeviceContext` 从 hook 模块复制到 `com.katiusu.miuixgui.example.device`，设备类型覆盖项改读本地 `PrefsStore`
- **删除页面**：作用域列表页 `ScopeListActivity`、安全模式管理页 `SafeModeActivity`；`AndroidManifest.xml` 同步移除对应 Activity 与 `HookStatusReceiver`
- **主页**：状态卡三条数据线与三卡布局保持不变，「作用域申请」入口改为仅刷新本地状态
- **设置页**：原「模块」分区仅保留「当前设备类型」下拉，移除安全模式入口、申请作用域、清空 DexKit 缓存三项
- **构建与清单**：`compileSdk 37` / `buildToolsVersion 36.0.0` / `minSdk 34` / `targetSdk 34`；删除 `splits.abi`（不再限定 arm64-v8a 产物）、删除 `de.robv.android.xposed.category.MODULE_SETTINGS` intent-filter（主 Activity 改为 `MAIN` + `LAUNCHER`）
- **随 hook 一并移除**：`docs/`、`.github/workflows/`、`gradle/gradle-daemon-jvm.properties`（要求 JDK 25）；`README.md` 按纯 GUI 工程重写

### 后续调整（2026-10-05，版本号仍为 1.0）

- **许可证改为 Apache-2.0**：根目录 `LICENSE` 由 The Unlicense 换为 Apache License 2.0，关于页许可条目与链接同步更新
- **移除隐藏桌面图标机制**：删除 `activity-alias`（`LauncherAlias`）与 `LauncherIconController`，桌面图标改由 `MainActivity` 单一提供（修复「桌面出现两个图标」）
- **切页性能优化（重写）**：改为给 pager 每一页加硬件层（`Modifier.graphicsLayer()`），切页动画只做图层合成，页面内容不再逐帧重绘；补齐上游 Miuix 示例中「背景全透明时暂停动效帧循环」的逻辑。不改页面结构、不改底栏，模糊总开关开启时动画期间不再中途关闭
- **README 重写**：新增切页性能与目录结构说明，许可证章节更新为 Apache-2.0
- **清理**：删除无引用的字符串资源、关于页版权行与占位说明

### 流畅度定位与 release 构建（2026-10-05）

- **逐页对比结论**：与 [MiuixGuiTemplate](https://github.com/Ianzb/MiuixGuiTemplate) 逐文件归一化 diff 后确认，除本工程主动删掉的模块框架相关功能外，页面代码与模板逐行相同（`FeaturesPage.kt` / `BlurUtils.kt` / `BgEffectBackground.kt` / `HookOptionsPage.kt` / 功能子页 / `SubPageScaffold.kt` 完全一致）。切页掉帧不是页面写法问题，模板本身也是这套代码。
- **真正的大头是构建类型**：此前提供的演示包是 debug 构建 —— `debuggable = true`（ART 不做优化）、不经 R8、且不含依赖库基线配置（实测 debug APK 内没有 `assets/dexopt/baseline.prof`，release 包内有）。Compose 在 debug 下的固定开销远大于页面层可优化空间。
- **release 构建**：`isMinifyEnabled = true` + `proguard-android-optimize.txt`（与模板一致），新增 `app/proguard-rules.pro`；实测 APK 由 42.99 MB 降到 2.99 MB，包含 `baseline.prof` / `baseline.profm`。
- **修复容器构建坑**：build-tools 36.0.0 的 aarch64 aapt2 下，AGP 的 release 资源优化链路会静默产出空的 `resources-release-optimize.ap_`，导致 `packageRelease` 打出一个没有 `AndroidManifest.xml` / `resources.arsc` 的坏 APK（`aapt2 dump badging` 报 `could not identify format of APK`）。`gradle.properties` 增加 `android.enableResourceOptimizations=false`，release 暂不启用 `isShrinkResources`。
- **README**：新增「流畅度：请用 release 包」「与参考模板的差异」「生成 release 包」与容器注意事项。

## 0.5.2

> 发布于 2026-10-01

### 变更

- **安全模式加入白名单**：自动安全模式仅对系统界面、桌面、系统进程（`com.android.systemui` / `com.miui.home` / `android` / `system`）生效；其余进程本身会自行重启，不再计数与触发，避免误判（`SafeModeReader` 与 `SafeModeManager` 同步）

## 0.5.1

> 发布于 2026-09-30

### 新增

- **CI 构建产物推送 Telegram 测试话题**：`CI Build` 工作流构建 Debug APK 并上传 Artifacts 后，自动把测试包推送到 Telegram 群的指定话题；与正式版共用 `CHANNEL_ID` / `BOT_TOKEN`，仅新增 Secret `TEST_MESSAGE_THREAD_ID` 存放话题 `message_thread_id`（`getUpdates` 中的 `message_thread_id`，或话题链接 `https://t.me/c/<群id>/<话题id>` 的末段）。消息带版本号与 Actions 运行链接；未配置该 Secret、缺 `CHANNEL_ID` / `BOT_TOKEN` 或为 `pull_request` 时自动跳过

## 0.5.0

> 发布于 2026-09-29

### 新增

- **统一的「设备独占」API**：`OptionSpec.deviceScope: Set<DeviceType>?` + `rememberOptionEnabled(spec)`（依赖项 ∧ 设备白名单）。仅某类设备可用的功能在其它设备形态上**禁用灰显、不隐藏**；读取 `ConfigState`，切换「设置 → 当前设备类型」后实时刷新；所有 Hook 卡片统一改用 `rememberOptionEnabled` 作为 `enabled`
- **Hook 生效状态（广播回报）**：目标进程在 `BaseLoad` 完成一组注册后，合并本进程已成功安装的配置键，**一次性定向广播**回报（`HookStatusReporter` / `HookStatusContract`）；App 侧 `HookStatusReceiver` 经「发送者 UID → 包集合 + 模块版本 + 已声明键子集」校验后写入 `HookStatusStore`（device-protected，按 `versionCode` + `BOOT_COUNT` 作用域，旧版本 / 上次开机自动丢弃）；`OptionSpec.showStatus` + `rememberHookApplied(key)` 在副标题展示「已生效 / 未生效」，开关变更后先清证据、待目标进程重启重新回报。不依赖 hook 侧写远程偏好，也无需系统签名级权限

### 变更

- **移除旧「Hook 状态提示（标题染色）」实现，改为下述广播回报机制**：libxposed 远程文件为「App 写、Hook 读」，Hook 侧 `XposedInterface.openRemoteFile` 在 Vector 等新框架上明确为**只读**，原 `HookStatusWriter` 从 Hook 侧写入必然失败、状态始终为空，故整体移除：删除 `HookStatusWriter` / `HookStatusReader` / `HookStatus` 枚举与示例「状态提示」分区，移除 `OptionSpec.hookId` / `statusId` / `demoStatus`、`rememberHookStatus` / `HookStatusTitleColor`、各卡片 `titleColor` 染色、`hook_status_*` 字符串及 `BaseLoad` / `NativeHookHelper` / `XposedEntry` 中的状态写入调用和相应文档

### 修复

- **安全模式（崩溃循环保护）在 libxposed 上失效**：远程偏好对 hooked app 为**只读**，原 `SafeModeManager` 从 hook 侧写入必然抛 `UnsupportedOperationException`（被吞后静默失效）。改为：hook 侧仅**只读** `safe_mode_<pkg>`；App 侧根据「目标进程成功装载 hook」的回报记录启动、窗口内重复启动累计为疑似崩溃，达到阈值（普通 3 / 关键 2）后把 `safe_mode_<pkg>` 回写远程偏好；hook 下次启动读到即跳过全部 hook

## 0.4.3

> 发布于 2026-09-28

### 新增

- **隐藏桌面图标**：设置页「界面」分区新增开关，开启后隐藏本模块桌面图标；桌面图标改由 `activity-alias`（`LauncherAlias`）提供，`MainActivity` 保留 Xposed 模块设置入口（`MODULE_SETTINGS`），隐藏后仍可从模块管理器进入应用

## 0.4.2

> 发布于 2026-09-27

### 新增

- **作用域页 / 安全模式页支持搜索**：两页顶部新增搜索框，按应用名 / 包名实时过滤列表；无匹配时显示空态

## 0.4.1

> 发布于 2026-09-26

### 新增

- **重启成功提示**：`QuickActionDialog` 批量重启完成后弹出 `quick_action_restart_success`（「重启成功」），缺少 Root 时仍提示 `scope_restart_need_root`；同步更新 `docs/API.md` 5.6 与 `docs/CUSTOMIZE.md` 7.6

## 0.4.0

> 发布于 2026-09-25

### 新增

- **原生 Hook 二次封装**：新增 `NativeHookHelper` / `BaseNativeHook` 与 `BaseLoad.initNativeHook`，与 JavaHook 对称的一键接入（声明 / 加载 / 开关 / 状态 / 安全模式），面向 Rust 应用（`flutter_rust_bridge` / 纯 Rust 库）；`rusthook` 统一更名为 `nativehook`
- **版本 / 设备筛选**：新增 `HookVersionGate`（`hook/rule`）与 `hook/device`（`DeviceType`、`DeviceContext`）。版本支持 `>` `<` 比较与多重规则（AND/OR），可按 Android / HyperOS / MIUI / 应用版本；设备支持手机 / 平板 / 折叠屏区分，默认各设备通用；`versionGate` / `deviceScope` / `variants` 可组合使用
- **安全模式页**：设置页「模块」分区新增安全模式声明与入口，二级页可逐应用开关安全模式并查看崩溃计数（`SafeModeReader.setSafeMode`）
- **设置页「模块」整合**：设备类型（默认 / 手机 / 平板 / 折叠屏，支持手动覆盖）与安全模式置于模块区域
- **文档**：新增/重写 [原生 Hook 开发指南](docs/NATIVE_HOOK.md)，明确 JavaHook 与 NativeHook 的区分，聚焦 Rust 应用 Hook 全流程；同步更新接口文档
- **子页面搜索（支持多级）**：`HookOptionsPage` 新增 `subPages` / `HookSubPage`，可把子页面内的功能并入功能页搜索；`HookSubPage.subPages` 支持**递归嵌套**，多级页面深处的功能同样可被搜索直达（搜索结果摘要显示「父 / 子」路径，命中后直接打开对应层级页面，子页面无需再放搜索栏）
- **Miuix 标准显隐动画**：新增 `ui/util/MiuixAnimations.kt`（`MiuixExpandSpec`），组件显示 / 隐藏统一使用 Miuix 弹簧动画（`folmeSpring(damping = 1.0f, response = 0.4f)`）
- **顶栏扩展槽与重启应用**：`HookOptionsPage` / `SubPageScaffold` / `BaseSubPageActivity` 新增 `topBarActions` 扩展槽；新增通用 `QuickActionsAction`（`MiuixIcons.Refresh` 重启图标 → `QuickActionDialog`），统一右上角「重启」入口样式；`QuickActionDialog` 统一为「重启应用」：标题「重启应用」且无小标题，列表用 `Card` 圆角容器，每行 `CheckboxPreference`（对勾在右、默认全选），底部「全选 / 全不选」+「重启」（无勾选时禁用）
- **SystemUI 重启优化**：新增 `AppRestarter.restartSystemUi()`（`pkill -f` → `killall` → `force-stop` 兜底），`restart()` 对 `com.android.systemui` 特判，结束进程由系统自动拉起而不再触发系统重启
- **`BaseHook.target`**：新增 `protected lateinit var target: PackageTarget`，由 `BaseLoad` 在安装前注入，`init()` 内可直接使用 `target.classLoader`
- **CI / Release 工作流**：新增 GitHub Actions `.github/workflows/ci.yml`（Debug 构建 + Artifact）与 `release.yml`（签名 Release + GitHub Release + 可选 Telegram）；`app/build.gradle.kts` 增加基于环境变量的 `signingConfigs.release` 与 arm64-v8a ABI 拆分；`release.keystore` 与 GitHub Secrets 配置方式见 [README · 发布与 CI](README.md#发布与-ci)
- **应用内检查更新**：新增 `UpdateChecker`（请求 GitHub Releases API，比较语义化版本）与 `UpdateDialog`；设置页「更新」分区支持启动自动检查与手动检查，发现新版本弹窗展示当前 / 最新版本与 Release 更新说明，确认后跳转 GitHub Release 下载页；替换原占位 Toast，`AndroidManifest` 增加 `INTERNET` 权限。使用前请把 `UpdateChecker.REPO` 改为自己的仓库（见[二次开发指南](docs/CUSTOMIZE.md)）

### 变更

- **精简包体**：移除体积巨大的 `material-icons-extended`（约 83MB 的类），改用 `material-icons-core`，并在 `ui/icons/StatusIcons.kt` 内联原本使用的 3 个 Rounded 图标（外观不变）。（注意：**不要开启 R8 混淆**，它会破坏 libxposed 模块的加载与 Hook）
- **示例页 → 功能页**：`ui/screen/examples/ExamplesPage.kt` 更名为 `ui/screen/features/FeaturesPage.kt`（`ExamplesPageView` → `FeaturesPageView`、`exampleSpecs` → `featureSpecs`、子页 `ExampleSubPageActivity` → `FeatureSubPageActivity`），标签页「示例」改为「功能」，与真实模块形态一致；功能页小标题改为单语言，`HookSection.titleEn` 仅保留用于示例展示 API 英文组件名
- 搜索文案由「搜索组件」改为「搜索功能」（`search_hint`），同步更新二次开发指南与接口文档
- **许可要求调整**：衍生项目只需在应用内「关于」页保留 `Based on MiuixGuiTemplate <版本号>` 标注，**不再要求**在各自的 `README.md` 中强调；同步更新 README、二次开发指南与示例注释
- 关于页「反馈渠道」文案改为「Telegram 群组」（英文 `Telegram Group`），并强调不要在文档 / 界面中只写「反馈渠道 / 反馈方式」，以免用户看不出是 TG 群组
- **Telegram 话题推送**：`release.yml` 支持可选 Secret `MESSAGE_THREAD_ID`，多话题群（Forum）可指定发布到哪个话题（不填则发默认 / General 话题）
- **移除热重载功能**：删除设置页「全局热重载」、作用域页与重启应用入口中的热重载，以及 `XposedServiceManager.hotReload` / `runningTargets`、`NativeHookHelper.reset`、`PackageTarget.restored`、`XposedEntry` 的 `onHotReloading` / `onHotReloaded` 与 `module.prop` 的 `autoHotReload`；保留「重启」功能（含 SystemUI 重启优化）

### 修复

- **配置备份 `Float` 往返损坏**：字号等 `Float` 配置导出为 JSON 小数后，导入被解析为 `Double` 并被当作字符串写回，导致设置退回默认、且 hook 侧 `getFloat` 抛 `ClassCastException`；`PrefsStore` 增加 `Double → Float` 处理，`HookPrefs` 各 getter 增加类型不匹配兜底
- **导入失败误报成功**：设置页导入时未检查 `ConfigBackup.importJson` 返回值，非法文件也提示「导入成功」；改为按结果显示成功 / 失败，且仅在成功时重载页面

### 致谢

- 设备判定思路参考 HyperCeiler（AGPL-3.0，仅思路参考，未复用其代码）

## 0.3.1

> 发布于 2026-09-25

### 新增

- **模块开发工作流**：新增 `docs/WORKFLOW.md`，规范「指定应用 Hook 实现」与「参考其他模块复刻功能」两类流程，明确真机 `adb` 扫描须经用户逐次授权、只读命令白名单与隐私边界，以及第三方许可证合规与致谢落点

### 变更

- 「第三方许可证」页面更名为「第三方许可证与致谢」，`licenses_header` 文案覆盖非开源参考项
- 修正文档中 `licenses_section_credits` → `licenses_section_refs` 的笔误

## 0.3.0

> 发布于 2026-09-22

### 变更

- **Hook 状态提示改为标题染色**：规则生效时标题显示为绿色、失败为红色、未应用保持默认色；不再在标题左侧显示对号 / 叉号，`HookStatusStartAction` 更名为 `HookStatusTitleColor`（经 `titleColor` 传入，零额外占位）
- **重启目标应用仅在运行时执行**：应用在运行才 `force-stop` 并重新拉起，未运行则不更改、不自动打开应用
- **开源协议调整为 LGPL-3.0**：仓库同时包含 LGPL-3.0 与 Apache-2.0（自 miuix 引入的文件保留原始声明）许可的代码，按传染性要求整体以 LGPL-3.0 授权分发；新增根目录 `LICENSE`
- 依赖升级：Miuix `0.9.4-rc01` → `0.9.4`；`miuix-navigation3-ui` 更换为 `miuix-nav`
- 主页 Pager 统一为 **Cross-Axis** 拦截模式（`pagerGestureOverride` + `springAnimateToPage`），列表惯性滚动 / 回弹期间可横滑切页
- 「关于」页许可证入口改为 GNU LGPL v3.0，「第三方许可证」更名「第三方许可证与致谢」并新增参考项目分组

### 新增

- **参考与致谢**：README 与应用内说明本模板的脚手架定位，并致谢参考项目（具体清单见应用内「第三方许可证与致谢」页）
- **Based on 约定**：衍生项目须在 `README.md` 与「关于」页标注 `Based on MiuixGuiTemplate <版本号>`；示例程序已内建该标注（当前为 `Based on MiuixGuiTemplate 0.3.0`）
- `docs/CUSTOMIZE.md` 新增「开源协议与致谢（必读）」核对清单，二次开发可一次性对照完成

## 0.2.0

> 发布于 2026-09-13

### 新增

- **Hook 兜底 / 安全模式**：目标进程重复崩溃时自动禁用该包全部 hook，避免系统应用反复崩溃导致无法开机；关键应用阈值更低，支持在作用域页一键恢复
- **作用域页应用操作**：每个应用提供「热重载」「重启」文本按钮（重启更突出），支持对系统进程重启前二次确认
- **二级页面「包名列表」组件**：输入包名后页面右上角出现「快捷操作」按钮，可批量热重载 / 重启，并提供「全部热重载」「全部重启」；支持通过 `customActionPackages` 注入自定义应用
- 主页「模块状态」同时判断模块启用与 **Root 权限**，无 Root 时自动轮询检测，授予后卡片自动更新
- 二次开发指南 `docs/CUSTOMIZE.md`

### 优化

- 作用域列表实时轮询刷新，无需重新打开页面即可反映授权变化
- 「清空 DexKit 缓存」文案不再标注需要 Root（主页已标注 Root 状态）
- 关于页项目地址 / 反馈渠道改为字符串资源驱动，展示与跳转同源
- **顶栏模糊改用 Haze 实现**：`TopBarBlurConfig` 统一 `BlurRadius` / `SurfaceAlpha` / `FullStrengthFraction` / `ScrollFadeDistance`，渐进遮罩为「顶部满强度 → 底边渐隐」，`BlurredBar` 只接受 `HazeState`
- 模块版本号更新为 `0.2.0`

### 修复

- 修复深色模式冷启动瞬间白屏闪烁（新增 `values-night` 窗口背景并兼容手动强制深浅色）
- 修复作用域页刷新与打开动画冲突导致动画丢失
- 修复非作用域内应用无法加载应用信息（新增 `QUERY_ALL_PACKAGES` 权限）
- 修复二级页面（作用域页 / 示例二级页）顶栏不随滚动收起
- **修复顶栏模糊下卡片等硬边缘透出**：Haze 会先原样绘制来源内容再叠加模糊副本，模糊层必须用不透明 `backgroundColor`（surface）作为底层，否则卡片边缘会从模糊中透出，看起来像「组件盖在模糊之上」
- 实现 Android 自动备份规则，去除模板遗留 TODO

## 0.1.0

> 发布于 2026-09-11

### 新增

- 基于 **libxposed API 102** 的完整二次封装：`hookBefore` / `hookAfter` / `hookReplace` / `intercept` / `findAndHook*` / `hookAll*` / `hookClassInitializer` / `invokeOriginal`，统一 `HookRegistry` 管理句柄
- 适配新版热重载机制：`onHotReloading` 保存状态、`onHotReloaded` 重装 hook
- 主动申请作用域：启用选项时通过 `XposedService.requestScope` 自动为未授权目标申请
- DexKit 缓存：`DexKitCacheManager` + `JsonFileCache`，带版本失效校验与文件锁，支持 Root 清空缓存
- 统一配置系统：自定义键名、默认值、持久化、跨进程镜像到 LSPosed 远程偏好、JSON 导出导入
- 全局组件搜索：所有组件标题接入搜索
- Miuix 组件库：开关卡片、箭头卡片、下拉卡片、滑块卡片、复选框卡片、单选（右侧复选框）卡片、文本输入卡片
- 组件与 Hook 状态绑定：标题左侧显示对号 / 叉号，未应用时不显示
- 二级页面模板：`BaseSubPageActivity` + `SubPageScaffold`，自动套用主题模式与背景模糊等模块配置，使用系统默认页面切换动画
- 主页：模块激活状态卡片、作用域统计（点击查看应用图标 / 名称 / 包名 / 版本，实时更新）、模块功能总数
- 示例标签页：内置每种组件类型的实例（不含 hook 具体应用代码）

### 优化

- 滑块支持整数 / 小数 / 自定义范围，数值类型说明显示在滑动条上方、当前数值右侧
- 下拉卡片第一项为「默认（中速）」等默认项，表示不 hook
- 单选卡片改为右侧复选框表示选中项
- 滑块 / 文本输入弹窗内当前值与默认值同一行左右显示，滑块弹窗两侧显示最小值 / 最大值
- 关于页图标运行时读取应用启动图标，与桌面图标保持同步

### 修复

- 修复示例页配置键不匹配导致的启动闪退
- 修复作用域列表应用图标被错误着色的问题
