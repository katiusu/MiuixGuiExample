# 重写计划（把 AGPL 代码换成独立实现）

审计命令：

    python3 scripts/license_audit.py --template /tmp/tplsrc/MiuixGuiTemplate-master/app/src/main/java --example /tmp/miuix/example --json-out /tmp/audit.json

## 当前分档（2026-11-21）

| 判定 | 文件数 | 行数 | 含义 |
|---|---|---|---|
| DERIVED-AGPL | 58 | 7838 | 与上游模板高度相似，必须重写 |
| DERIVED-APACHE-OK | 14 | 2171 | 与 miuix 官方 example 一致，保留，补 Apache 头 |
| INDEPENDENT | 6 | 362 | 独立实现，只需补 SPDX 头 |
| REVIEW | 2 | 100 | 0.60-0.85 灰区，判断后处理 |

## 重写顺序（依赖叶子 -> 结构件 -> 页面）

### Batch 1 · 零依赖叶子
OptionType / OptionRegistry / MiuixAnimations / WindowBackground / HookStatus / DeviceType /
SystemVersionDetector / LauncherIconController / LocaleHelper / ConfigBackup / ConfigState /
DexKitCacheManager -- 约 350 行，无上游依赖或只依赖自身包。

### Batch 2 · 纯算法（无 Compose 依赖）
- BlurUtils.kt 418 行（0.99）：模糊的整数近似、位图缩放，纯数学，可独立重写并自测
- PagerSwipeOverride.kt 369 行（1.00）：ViewPager2 手势接管，纯 Android 触摸逻辑

### Batch 3 · 组件层
AppTopAppBar 239 / HookSliderCard 220 / HookOptionsPage 314 / AppArrowPreference /
SystemRestartConfirmDialog / UpdateDialog / FeaturesPage 200 / LicensePage 248 ...

### Batch 4 · 页面与壳
HomePage 395 / SettingsPage 396 / AboutPage 435 / ScopeListActivity 289 / MainActivity 651

## 重写规格来源（合法）

1. miuix 官方 example（Apache-2.0）：组件怎么用、动画参数、玻璃效果组合方式，全部以此为准。
2. 本项目自己的行为约定：改写前先把原文件行为写成 spec（输入、输出、状态、边界），再按 spec 重新实现，避免逐行翻译。
3. Android / androidx 官方 API 文档。

## 验收

每批结束必须：

1. scripts/license_audit.py 对该批文件返回 INDEPENDENT（相似度 <0.60）；
2. 每个改过的 .kt 补 SPDX 头；
3. 能编译时跑 assembleDebug；不能编译时至少人工逐行复核。

