package com.example.memorycard.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 学习历史记录
 * 记录每天学习的方剂
 */
@Entity(
    tableName = "study_history",
    foreignKeys = [
        ForeignKey(
            entity = FanggeCard::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"]), Index(value = ["studyDate"])]
)
data class StudyHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cardId: Int,  // 方剂ID
    val studyDate: Date,  // 学习日期
    val isReviewed: Boolean = false  // 是否已复习
)