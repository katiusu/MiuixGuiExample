# MiuixGui Template

一个以 [miuix](https://github.com/compose-miuix-ui/miuix) 组件库搭建的 Android Compose 界面骨架（MIT）。

## 结构

- `MainActivity`：edge-to-edge 入口，套 `AppTheme`。
- `AppShell`：悬浮玻璃底栏（`GlassNavigationBar`）+ 背景采样层（`rememberLayerBackdrop` / `layerBackdrop`）。
- `ui/theme/AppTheme`：`ThemeController` + `MiuixTheme`，系统栏图标颜色随主题亮度同步。
- `ui/component/AppComponents`：分组卡片、数字小卡、横幅、页面标题；圆角统一 `squircleClip`（连续曲率）。
- `ui/screen/*`：主页 / 功能 / 设置 / 关于 四个页面。
- `data/*`：设置持久化与只读设备信息。

## 约束

- 所有 UI 样式来自 miuix 官方组件，不新建自绘样式；圆角一律连续曲率。
- 不改系统设置、不采集数据。

## 构建

```bash
./gradlew :app:assembleDebug
```

## 许可

MIT，见 [LICENSE](LICENSE)；第三方声明见 [NOTICE](NOTICE)。
