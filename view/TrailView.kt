package com.example.memorycard.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.random.Random

/**
 * 动态拖尾效果视图
 * 显示漂浮的粒子带拖尾动画
 */
class TrailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    
    // 拖尾颜色配置
    private val trailColors = listOf(
        Color.parseColor("#5E9E82"),  // 绿色
        Color.parseColor("#E08A93"),  // 粉色
        Color.parseColor("#4080D0"),  // 蓝色
        Color.parseColor("#F5D547")   // 黄色
    )
    
    init {
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        startAnimation()
    }
    
    private fun startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16 // 60fps
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                updateParticles()
                invalidate()
            }
            start()
        }
    }
    
    private fun updateParticles() {
        // 添加新粒子
        if (particles.size < 30 && Random.nextFloat() < 0.1f) {
            particles.add(createParticle())
        }
        
        // 更新现有粒子
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.update()
            if (particle.isDead()) {
                iterator.remove()
            }
        }
    }
    
    private fun createParticle(): Particle {
        val startX = Random.nextFloat() * width
        val startY = Random.nextFloat() * height
        val color = trailColors.random()
        return Particle(startX, startY, color)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        particles.forEach { particle ->
            drawParticleTrail(canvas, particle)
        }
    }
    
    private fun drawParticleTrail(canvas: Canvas, particle: Particle) {
        val trail = particle.trail
        if (trail.size < 2) return
        
        for (i in 1 until trail.size) {
            val alpha = ((i.toFloat() / trail.size) * particle.alpha * 255).toInt()
            paint.color = particle.color
            paint.alpha = alpha
            paint.strokeWidth = particle.size * (i.toFloat() / trail.size)
            
            val start = trail[i - 1]
            val end = trail[i]
            canvas.drawLine(start.x, start.y, end.x, end.y, paint)
        }
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
    
    /**
     * 粒子类
     */
    private inner class Particle(
        startX: Float,
        startY: Float,
        val color: Int
    ) {
        val trail = mutableListOf<TrailPoint>()
        var alpha = 1f
        var size = Random.nextFloat() * 8f + 4f
        private var vx = (Random.nextFloat() - 0.5f) * 3f
        private var vy = (Random.nextFloat() - 0.5f) * 3f
        private var life = 1f
        private val decay = Random.nextFloat() * 0.005f + 0.002f
        
        init {
            trail.add(TrailPoint(startX, startY))
        }
        
        fun update() {
            life -= decay
            alpha = life
            
            val lastPoint = trail.last()
            var newX = lastPoint.x + vx
            var newY = lastPoint.y + vy
            
            // 边界反弹
            if (newX < 0 || newX > width) {
                vx = -vx
                newX = lastPoint.x + vx
            }
            if (newY < 0 || newY > height) {
                vy = -vy
                newY = lastPoint.y + vy
            }
            
            trail.add(TrailPoint(newX, newY))
            
            // 限制拖尾长度
            if (trail.size > 15) {
                trail.removeAt(0)
            }
        }
        
        fun isDead(): Boolean = life <= 0
    }
    
    /**
     * 轨迹点
     */
    private data class TrailPoint(val x: Float, val y: Float)
}
