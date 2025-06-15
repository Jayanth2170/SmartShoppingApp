package com.example.smartshoppingcart



import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun DetectedObjectOverlay(rects: List<Rect>, imageWidth: Int, imageHeight: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (imageWidth == 0 || imageHeight == 0) return@Canvas

        val scaleX = size.width / imageWidth
        val scaleY = size.height / imageHeight

        for (rect in rects) {
            drawRect(
                color = Color.Red,
                topLeft = androidx.compose.ui.geometry.Offset(rect.left * scaleX, rect.top * scaleY),
                size = androidx.compose.ui.geometry.Size(
                    (rect.right - rect.left) * scaleX,
                    (rect.bottom - rect.top) * scaleY
                ),
                style = Stroke(width = 5f)
            )
        }
    }
}
