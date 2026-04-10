package com.example.memorycard.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * 呼吸微光背景视图
 * 模拟「林间晨雾」的呼吸感
 */
class BreathingGlowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    private var progress = 0f
    
    // 微光颜色 - 淡绿色系
    private val glowColors = intArrayOf(
        Color.parseColor("#30E8F3EF"),  // 中心淡绿
        Color.parseColor("#10F2F8F6"),  // 过渡
        Color.parseColor("#00FAFAFA")   // 边缘透明
    )
    
    init {
        startAnimation()
    }
    
    private fun startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 8000 // 8秒一个循环，超慢呼吸
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 计算呼吸效果参数
        val breatheAlpha = 0.3f + progress * 0.2f // 透明度在 0.3-0.5 之间变化
        val offsetX = (progress - 0.5f) * 100f // 轻微平移
        val offsetY = (progress - 0.5f) * 50f
        
        // 绘制多个微光点
        drawGlow(canvas, width * 0.7f + offsetX, height * 0.2f + offsetY, 
            width * 0.4f, breatheAlpha)
        drawGlow(canvas, width * 0.3f - offsetX, height * 0.6f - offsetY, 
            width * 0.3f, breatheAlpha * 0.7f)
        drawGlow(canvas, width * 0.8f + offsetX, height * 0.8f + offsetY, 
            width * 0.25f, breatheAlpha * 0.5f)
    }
    
    private fun drawGlow(canvas: Canvas, cx: Float, cy: Float, radius: Float, alpha: Float) {
        val adjustedColors = glowColors.map { color ->
            val a = (Color.alpha(color) * alpha).toInt().coerceIn(0, 255)
            Color.argb(a, Color.red(color), Color.green(color), Color.blue(color))
        }.toIntArray()
        
        paint.shader = RadialGradient(
            cx, cy, radius,
            adjustedColors,
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, radius, paint)
        paint.shader = null
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
