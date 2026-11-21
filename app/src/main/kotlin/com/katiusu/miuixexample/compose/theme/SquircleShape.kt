package com.katiusu.miuixexample.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import top.yukonga.miuix.kmp.squircle.addSquircleRect

/**
 * 连续曲率圆角（squircle）。几何完全来自 miuix 的 addSquircleRect，
 * 本文件只负责把它包成 Compose 的 Shape，不自己造任何曲线参数。
 */
@Immutable
internal class SquircleShape(private val cornerRadius: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        if (size.minDimension <= 0f) {
            return Outline.Rectangle(Rect(0f, 0f, size.width, size.height))
        }
        val path = Path()
        with(density) {
            path.addSquircleRect(
                width = size.width,
                height = size.height,
                cornerRadius = cornerRadius.toPx().coerceAtLeast(0f),
            )
        }
        return Outline.Generic(path)
    }
}

internal fun squircleShape(cornerRadius: Dp): Shape = SquircleShape(cornerRadius)
