package com.example.memorycard.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView

/**
 * 动画工具类
 * 提供各种页面动效
 */
object AnimationUtils {
    
    /**
     * 数字跳动动画
     * 从 0 顺滑跳动到目标值
     */
    fun animateNumber(textView: TextView, targetValue: Int, duration: Long = 1000) {
        val animator = ValueAnimator.ofInt(0, targetValue)
        animator.apply {
            this.duration = duration
            interpolator = OvershootInterpolator(0.8f)
            addUpdateListener {
                textView.text = it.animatedValue.toString()
            }
            start()
        }
    }
    
    /**
     * 元素从下往上滑入 + 淡入
     * iOS 弹簧曲线
     */
    fun slideUpFadeIn(view: View, delay: Long = 0, duration: Long = 600) {
        view.alpha = 0f
        view.translationY = 50f
        
        val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        val translateAnimator = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f)
        
        AnimatorSet().apply {
            playTogether(alphaAnimator, translateAnimator)
            this.duration = duration
            startDelay = delay
            interpolator = OvershootInterpolator(0.5f)
            start()
        }
    }
    
    /**
     * 按钮呼吸动效
     * 轻微缩放引导点击
     */
    fun breathingAnimation(view: View, duration: Long = 3000) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.02f, 1f).apply {
            this.duration = duration
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.02f, 1f).apply {
            this.duration = duration
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        
        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            start()
        }
    }
    
    /**
     * 空状态图标浮动动画
     * 超慢上下浮动
     */
    fun floatingAnimation(view: View, duration: Long = 3000) {
        val translateY = ObjectAnimator.ofFloat(view, "translationY", 0f, -10f, 0f)
        translateY.apply {
            this.duration = duration
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }
    
    /**
     * 逐行淡入动画
     * 带轻微位移
     */
    fun fadeInLineByLine(views: List<View>, baseDelay: Long = 200, duration: Long = 400) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 20f
            
            val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            val translateAnimator = ObjectAnimator.ofFloat(view, "translationY", 20f, 0f)
            
            AnimatorSet().apply {
                playTogether(alphaAnimator, translateAnimator)
                this.duration = duration
                startDelay = baseDelay * index
                start()
            }
        }
    }
    
    /**
     * 点击反馈动画
     * 0.98 倍缩放 + 回弹
     */
    fun clickFeedback(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.98f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.98f, 1f)
        
        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 150
            interpolator = OvershootInterpolator(0.3f)
            start()
        }
    }
    
    /**
     * 错开入场动画
     * 多个元素依次滑入
     */
    fun staggeredSlideUp(views: List<View>, baseDelay: Long = 100, duration: Long = 500) {
        views.forEachIndexed { index, view ->
            slideUpFadeIn(view, baseDelay * index, duration)
        }
    }
}
