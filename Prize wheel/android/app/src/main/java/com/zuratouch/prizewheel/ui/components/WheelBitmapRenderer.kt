package com.zuratouch.prizewheel.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.ui.theme.ZuraColors
import kotlin.math.cos
import kotlin.math.sin

fun renderWheelBitmap(
    sizePx: Int,
    density: Density,
    layoutDirection: LayoutDirection,
    categories: List<PrizeCategory>,
    labelLayouts: List<TextLayoutResult>,
): ImageBitmap {
    val bitmap = ImageBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)
    val drawScope = CanvasDrawScope()
    drawScope.draw(
        density = density,
        layoutDirection = layoutDirection,
        canvas = canvas,
        size = Size(sizePx.toFloat(), sizePx.toFloat()),
    ) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawWheelDisc(center, radius, categories, labelLayouts)
    }
    return bitmap
}

private fun DrawScope.drawWheelDisc(
    center: Offset,
    radius: Float,
    categories: List<PrizeCategory>,
    labelLayouts: List<TextLayoutResult>,
) {
    if (categories.isEmpty()) {
        drawCircle(color = Color(0xFF1A2744), radius = radius * 0.92f, center = center)
    } else {
        val segmentRadius = radius * 0.92f
        if (categories.size == 1) {
            val category = categories.first()
            drawCircle(color = Color(category.color), radius = segmentRadius, center = center)
            drawSegmentLabel(
                layout = labelLayouts.first(),
                center = center,
                radius = segmentRadius,
                midAngle = -90f,
            )
        } else {
            val sweep = 360f / categories.size
            categories.forEachIndexed { index, category ->
                drawArc(
                    color = Color(category.color),
                    startAngle = index * sweep - 90f,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(center.x - segmentRadius, center.y - segmentRadius),
                    size = Size(segmentRadius * 2f, segmentRadius * 2f),
                )
                drawArc(
                    color = Color.Black.copy(alpha = 0.15f),
                    startAngle = index * sweep - 90f,
                    sweepAngle = 1f,
                    useCenter = true,
                    topLeft = Offset(center.x - segmentRadius, center.y - segmentRadius),
                    size = Size(segmentRadius * 2f, segmentRadius * 2f),
                )
                drawSegmentLabel(
                    layout = labelLayouts[index],
                    center = center,
                    radius = segmentRadius,
                    midAngle = index * sweep + sweep / 2f - 90f,
                )
            }
        }
    }

    val hubRadius = radius * 0.17f
    drawCircle(color = Color(0xFF111827), radius = hubRadius * 1.08f, center = center)
    drawCircle(color = Color(0xFFF8FAFC), radius = hubRadius, center = center)
    drawCircle(color = Color(0xFF374151), radius = hubRadius, center = center, style = Stroke(width = 2f))
}

private fun DrawScope.drawSegmentLabel(
    layout: TextLayoutResult,
    center: Offset,
    radius: Float,
    midAngle: Float,
) {
    val labelAngleRad = Math.toRadians(midAngle.toDouble())
    val labelRadius = radius * 0.54f
    val labelX = center.x + cos(labelAngleRad).toFloat() * labelRadius
    val labelY = center.y + sin(labelAngleRad).toFloat() * labelRadius
    val normalized = ((midAngle % 360f) + 360f) % 360f
    var textRotation = midAngle + 90f
    if (normalized in 90f..270f) textRotation += 180f
    rotate(textRotation, pivot = Offset(labelX, labelY)) {
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                labelX - layout.size.width / 2f,
                labelY - layout.size.height / 2f,
            ),
        )
    }
}
