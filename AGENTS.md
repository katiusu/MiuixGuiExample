# AGENTS.md — 本仓库给 AI agent / 贡献者的硬规则

本仓库是 HyperOS / Miuix 风格的 Compose 应用模板。所有进入本仓库的代码必须是独立实现，或以兼容许可证引入。

## 1. 许可证矩阵

| 来源 | 许可证 | 可否直接复制 |
|---|---|---|
| miuix 官方仓库 compose-miuix-ui/miuix（含 example/） | Apache-2.0 | 可以，但必须保留声明并在 NOTICE 列出来源 |
| MiuixGuiTemplate（上游 AGPL 模板） | AGPL-3.0 | 不可以 |
| 其他 GPL / AGPL 项目 | copyleft | 不可以 |
| androidx / kotlin 标准库 | Apache-2.0 | 作为依赖使用即可 |

## 2. 提交前必跑

    python3 scripts/license_audit.py --template <AGPL 模板 java 目录> --example <miuix 仓库>/example

退出码 0 才能提交；出现 DERIVED-AGPL 必须先把那个文件重写完。
REVIEW（与模板相似度 0.60-0.85）需人工判断：继续降到 0.60 以下，或在文件头写明独立实现依据。

## 3. 文件头要求

每个 .kt 的第一行在 package 之前：

    /* SPDX-License-Identifier: MIT */

若从 miuix 官方 example 移植：

    /* SPDX-License-Identifier: Apache-2.0 */
    /* Ported from compose-miuix-ui/miuix example: <相对路径> */

## 4. 重写纪律

- 重写 = 按行为规格重新实现，不是改名字、调换顺序、补注释。
- 禁止搬运上游模板的资源文件（图标、启动图、字体、动画 json）。
- 搬 UI 结构时优先抄 miuix 官方 example：它的 API 用法就是质量上限，且许可证兼容。

