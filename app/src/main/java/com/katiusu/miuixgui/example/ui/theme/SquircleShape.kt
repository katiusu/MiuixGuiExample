package com.katiusu.miuixgui.example.ui.theme

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon

/**
 * 连续曲率（squircle）圆角形状。
 *
 * 基于 androidx.graphics.shapes 的 [RoundedPolygon] 生成，角部曲率是连续的（G2），
 * 与纯圆弧的 RoundedCornerShape 相比在视觉上更“软”。
 *
 * @param cornerRadius 名义圆角半径（角部弧长的一半），默认 16dp
 * @param smoothing 平滑度，0 = 纯圆弧（等同 RoundedCornerShape），1 = 最大连续曲率
 */
class SquircleShape(
    private val cornerRadius: Dp = 16.dp,
    private val smoothing: Float = 0.6f,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        if (size.width <= 0f || size.height <= 0f) {
            return Outline.Rectangle(Rect(0f, 0f, size.width, size.height))
        }
        val radius = with(density) { cornerRadius.toPx() }
            .coerceAtMost(minOf(size.width, size.height) / 2f)
        val polygon = RoundedPolygon(
            vertices = floatArrayOf(
                0f, 0f,
                size.width, 0f,
                size.width, size.height,
                0f, size.height,
            ),
            rounding = CornerRounding(radius, smoothing),
        )
        val path = Path()
        val cubics = polygon.cubics
        if (cubics.isNotEmpty()) {
            path.moveTo(cubics[0].anchor0.x, cubics[0].anchor0.y)
            cubics.forEach { cubic ->
                path.cubicTo(
                    cubic.control0.x, cubic.control0.y,
                    cubic.control1.x, cubic.control1.y,
                    cubic.anchor1.x, cubic.anchor1.y,
                )
            }
            path.close()
        }
        return Outline.Generic(path)
    }
}

/** 便捷构造：连续曲率圆角 [Shape]。 */
fun squircleShape(radius: Dp = 16.dp, smoothing: Float = 0.6f): Shape =
    SquircleShape(cornerRadius = radius, smoothing = smoothing)
