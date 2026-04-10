package com.example.memorycard.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "fangge_card")
data class FanggeCard(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val song: String,
    val composition: String,
    val effect: String,
    val indication: String,
    val keyPoint: String,  // 证治要点
    val tips: String,
    val orderIndex: Int = 0,  // 方剂在原文档中的顺序
    val reviewLevel: Int = 0,
    val reviewInterval: Long = 0,
    val nextReviewTime: Date = Date(),
    val isVague: Boolean = false  // 标记是否为模糊状态
)
