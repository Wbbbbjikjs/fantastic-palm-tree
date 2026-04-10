package com.example.memorycard.algorithm

import java.util.Date

class SM2Algorithm {
    // 复习间隔天数列表
    private val intervals = listOf(1, 3, 7, 15, 30)

    /**
     * 计算下次复习时间
     * @param currentTime 当前时间
     * @param reviewCount 复习次数
     * @param difficulty 难度
     * @param result 记忆结果（0：忘记，1：模糊，2：记住）
     * @return 下次复习时间
     */
    fun calculateNextReviewTime(
        currentTime: Date,
        reviewCount: Int,
        difficulty: Int,
        result: Int
    ): Date {
        val interval = calculateInterval(reviewCount, difficulty, result)
        val nextTime = Date(currentTime.time + interval * 24 * 60 * 60 * 1000L)
        return nextTime
    }

    /**
     * 计算复习间隔
     * @param reviewCount 复习次数
     * @param difficulty 难度
     * @param result 记忆结果
     * @return 间隔天数
     */
    private fun calculateInterval(reviewCount: Int, difficulty: Int, result: Int): Int {
        return when (result) {
            0 -> 1 // 忘记，重新开始
            1 -> 1 // 模糊，重新开始
            2 -> {
                // 记住，根据复习次数选择间隔
                val index = minOf(reviewCount, intervals.size - 1)
                intervals[index]
            }
            else -> 1
        }
    }

    /**
     * 更新卡片难度
     * @param currentDifficulty 当前难度
     * @param result 记忆结果
     * @return 新难度
     */
    fun updateDifficulty(currentDifficulty: Int, result: Int): Int {
        return when (result) {
            0 -> minOf(currentDifficulty + 1, 5) // 忘记，难度增加
            1 -> minOf(currentDifficulty + 1, 5) // 模糊，难度增加
            2 -> maxOf(currentDifficulty - 1, 0) // 记住，难度减少
            else -> currentDifficulty
        }
    }

    /**
     * 计算记忆率
     * @param totalReviews 总复习次数
     * @param correctReviews 正确复习次数
     * @return 记忆率（0-100）
     */
    fun calculateRetentionRate(totalReviews: Int, correctReviews: Int): Int {
        if (totalReviews == 0) return 0
        return (correctReviews.toDouble() / totalReviews * 100).toInt()
    }
}
