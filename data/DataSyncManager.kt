package com.example.memorycard.data

import android.content.Context
import com.example.memorycard.database.AppDatabase
import com.example.memorycard.database.FanggeCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Calendar
import java.util.Date

/**
 * 数据同步管理器
 * 用于在 assets 文件修改后同步更新数据库
 */
class DataSyncManager(private val context: Context) {

    private val dao = AppDatabase.getInstance(context).appDatabaseDao()

    /**
     * 同步数据到数据库
     * 策略：保留用户的学习进度(reviewLevel, reviewInterval, nextReviewTime, isVague)，
     * 更新其他字段(name, song, composition, effect, indication, keyPoint, tips)
     */
    suspend fun syncData() {
        withContext(Dispatchers.IO) {
            try {
                // 解析文档文件
                val newCards = parseDocument()
                
                // 获取现有数据
                val existingCards = dao.getAllFanggeCards()
                
                // 创建现有数据的名称到数据的映射
                val existingMap = existingCards.associateBy { it.name }
                
                // 创建新数据的名称到数据的映射
                val newMap = newCards.associateBy { it.name }
                
                // 1. 更新或插入卡片
                for (newCard in newCards) {
                    val existingCard = existingMap[newCard.name]
                    if (existingCard != null) {
                        // 更新现有卡片（保留学习进度）
                        val updatedCard = existingCard.copy(
                            song = newCard.song,
                            composition = newCard.composition,
                            effect = newCard.effect,
                            indication = newCard.indication,
                            keyPoint = newCard.keyPoint,
                            tips = newCard.tips,
                            orderIndex = newCard.orderIndex
                        )
                        dao.updateFanggeCard(updatedCard)
                    } else {
                        // 插入新卡片
                        dao.insertFanggeCard(newCard)
                    }
                }
                
                // 2. 删除数据库中已不存在的卡片
                for (existingCard in existingCards) {
                    if (!newMap.containsKey(existingCard.name)) {
                        dao.deleteFanggeCard(existingCard)
                    }
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 强制重新导入所有数据
     * 警告：这将清除所有学习进度！
     */
    suspend fun forceReimport() {
        withContext(Dispatchers.IO) {
            try {
                // 清空所有现有数据（使用SQL直接删除，更高效）
                dao.deleteAllFanggeCards()
                
                // 清空学习历史
                dao.deleteAllStudyHistory()
                
                // 重新解析并插入
                val newCards = parseDocument()
                for (card in newCards) {
                    dao.insertFanggeCard(card)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    /**
     * 解析文档文件
     * 格式：
     * 第一行：方剂名称
     * 第二行：方歌内容
     * 【功用】...
     * 【主治】...
     * 【证治要点】...
     * 【药物组成】...
     * 【方剂趣记】...
     * 空行分隔
     */
    private fun parseDocument(): List<FanggeCard> {
        val cards = mutableListOf<FanggeCard>()
        var orderIndex = 0

        try {
            context.assets.open("方剂学方歌+趣味方歌.txt").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
                    val lines = reader.readLines()
                    var i = 0
                    
                    while (i < lines.size) {
                        // 跳过空行
                        while (i < lines.size && lines[i].trim().isEmpty()) {
                            i++
                        }

                        if (i >= lines.size) break

                        // 第一行：方剂名称
                        val name = lines[i].trim()
                        i++

                        // 跳过空行
                        while (i < lines.size && lines[i].trim().isEmpty()) {
                            i++
                        }

                        if (i >= lines.size) break

                        // 第二行：方歌内容
                        val song = lines[i].trim()
                        i++

                        // 解析其他字段
                        var effect = "" // 功用
                        var indication = "" // 主治
                        var keyPoint = "" // 证治要点
                        var composition = "" // 药物组成
                        var tips = "" // 方剂趣记

                        // 继续解析直到遇到空行或文件结束
                        while (i < lines.size && lines[i].trim().isNotEmpty()) {
                            val line = lines[i].trim()
                            when {
                                line.startsWith("【功用】") -> {
                                    effect = line.substringAfter("【功用】").trim()
                                }
                                line.startsWith("【主治】") -> {
                                    indication = line.substringAfter("【主治】").trim()
                                }
                                line.startsWith("【证治要点】") -> {
                                    keyPoint = line.substringAfter("【证治要点】").trim()
                                }
                                line.startsWith("【药物组成】") -> {
                                    composition = line.substringAfter("【药物组成】").trim()
                                }
                                line.startsWith("【方剂趣记】") -> {
                                    tips = line.substringAfter("【方剂趣记】").trim()
                                }
                            }
                            i++
                        }

                        // 创建卡片
                        if (name.isNotEmpty() && song.isNotEmpty()) {
                            // 新卡片默认设置为未学习状态
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.YEAR, 100)
                            
                            cards.add(
                                FanggeCard(
                                    name = name,
                                    song = song,
                                    composition = composition,
                                    effect = effect,
                                    indication = indication,
                                    keyPoint = keyPoint,
                                    tips = tips,
                                    orderIndex = orderIndex++,
                                    reviewLevel = -1,  // -1 表示未学习
                                    reviewInterval = 0,
                                    nextReviewTime = calendar.time
                                )
                            )
                        }
                        
                        // 跳过空行
                        while (i < lines.size && lines[i].trim().isEmpty()) {
                            i++
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return cards
    }

    /**
     * 获取解析的卡片数量（用于预览）
     */
    suspend fun getParsedCardCount(): Int {
        return withContext(Dispatchers.IO) {
            parseDocument().size
        }
    }

    /**
     * 获取数据库中的卡片数量
     */
    suspend fun getDatabaseCardCount(): Int {
        return withContext(Dispatchers.IO) {
            dao.getFanggeCardCount()
        }
    }
}
