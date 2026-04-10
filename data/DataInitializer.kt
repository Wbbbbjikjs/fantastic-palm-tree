package com.example.memorycard.data

import android.content.Context
import com.example.memorycard.database.AppDatabase
import com.example.memorycard.database.FanggeCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Date
import java.util.Calendar

class DataInitializer(private val context: Context) {

    private val dao = AppDatabase.getInstance(context).appDatabaseDao()

    /**
     * 初始化数据
     * 使用协程在IO线程中执行数据库操作
     */
    suspend fun initializeData() {
        withContext(Dispatchers.IO) {
            try {
                // 检查是否已有数据，避免重复导入
                val existingCount = dao.getFanggeCardCount()
                if (existingCount > 0) {
                    return@withContext
                }

                // 解析文档文件并插入卡片
                val cards = parseDocument()
                insertCards(cards)
            } catch (e: Exception) {
                e.printStackTrace()
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
        var orderIndex = 0  // 用于保存方剂顺序

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
                            // nextReviewTime 设置为一个遥远的未来（100年后），表示未学习
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
                                    orderIndex = orderIndex++,  // 保存顺序索引
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
     * 插入卡片数据
     */
    private suspend fun insertCards(cards: List<FanggeCard>) {
        for (card in cards) {
            dao.insertFanggeCard(card)
        }
    }
}
