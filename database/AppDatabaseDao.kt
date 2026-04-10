package com.example.memorycard.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface AppDatabaseDao {
    // FanggeCard CRUD operations
    @Insert
    suspend fun insertFanggeCard(card: FanggeCard): Long

    @Update
    suspend fun updateFanggeCard(card: FanggeCard)

    @Delete
    suspend fun deleteFanggeCard(card: FanggeCard)

    @Query("SELECT * FROM fangge_card WHERE id = :id")
    suspend fun getFanggeCardById(id: Int): FanggeCard?

    @Query("SELECT * FROM fangge_card ORDER BY orderIndex ASC")
    suspend fun getAllFanggeCards(): List<FanggeCard>

    @Query("SELECT * FROM fangge_card WHERE name LIKE '%' || :keyword || '%' ORDER BY orderIndex ASC")
    suspend fun searchFanggeCardsByName(keyword: String): List<FanggeCard>

    @Query("SELECT * FROM fangge_card ORDER BY orderIndex ASC")
    suspend fun getAllFanggeCardsByOrder(): List<FanggeCard>

    // 查询今日需要复习的卡片（nextReviewTime <= 当前时间）
    @Query("SELECT * FROM fangge_card WHERE nextReviewTime <= :currentTime ORDER BY nextReviewTime ASC")
    suspend fun getTodayReviewCards(currentTime: Date = Date()): List<FanggeCard>

    // 根据熟练度查询卡片
    @Query("SELECT * FROM fangge_card WHERE reviewLevel = :level ORDER BY name ASC")
    suspend fun getFanggeCardsByReviewLevel(level: Int): List<FanggeCard>

    // 获取卡片总数
    @Query("SELECT COUNT(*) FROM fangge_card")
    suspend fun getFanggeCardCount(): Int

    // 获取今日需要复习的卡片数量
    @Query("SELECT COUNT(*) FROM fangge_card WHERE nextReviewTime <= :currentTime AND reviewLevel >= 0")
    suspend fun getTodayReviewCardCount(currentTime: Date = Date()): Int

    // 获取未学习的卡片数量（reviewLevel = -1）
    @Query("SELECT COUNT(*) FROM fangge_card WHERE reviewLevel = -1")
    suspend fun getUnlearnedCardCount(): Int

    // 获取未学习的卡片
    @Query("SELECT * FROM fangge_card WHERE reviewLevel = -1 ORDER BY orderIndex ASC")
    suspend fun getUnlearnedCards(): List<FanggeCard>

    // 重置所有卡片学习进度
    @Query("UPDATE fangge_card SET reviewLevel = -1, reviewInterval = 0, nextReviewTime = :futureTime")
    suspend fun resetAllProgress(futureTime: Date)

    // 获取模糊卡片数量
    @Query("SELECT COUNT(*) FROM fangge_card WHERE isVague = 1")
    suspend fun getVagueCardCount(): Int

    // 获取所有模糊卡片
    @Query("SELECT * FROM fangge_card WHERE isVague = 1 ORDER BY orderIndex ASC")
    suspend fun getVagueCards(): List<FanggeCard>

    // 标记卡片为模糊状态
    @Query("UPDATE fangge_card SET isVague = 1 WHERE id = :cardId")
    suspend fun markCardAsVague(cardId: Int)

    // 标记卡片为熟记状态（移除模糊标记）
    @Query("UPDATE fangge_card SET isVague = 0 WHERE id = :cardId")
    suspend fun markCardAsMastered(cardId: Int)

    // ==================== 学习历史相关方法 ====================

    // 添加学习历史记录
    @Insert
    suspend fun insertStudyHistory(history: StudyHistory): Long

    // 获取某天的学习记录
    @Query("SELECT * FROM study_history WHERE studyDate = :date")
    suspend fun getStudyHistoryByDate(date: Date): List<StudyHistory>

    // 获取所有学习日期（去重）
    @Query("SELECT DISTINCT studyDate FROM study_history ORDER BY studyDate DESC")
    suspend fun getAllStudyDates(): List<Date>

    // 获取某天的学习卡片ID列表
    @Query("SELECT cardId FROM study_history WHERE studyDate = :date")
    suspend fun getCardIdsByDate(date: Date): List<Int>

    // 获取某天的学习卡片详情
    @Query("SELECT f.* FROM fangge_card f INNER JOIN study_history s ON f.id = s.cardId WHERE s.studyDate = :date ORDER BY f.orderIndex ASC")
    suspend fun getCardsByStudyDate(date: Date): List<FanggeCard>

    // 标记某天的学习记录为已复习
    @Query("UPDATE study_history SET isReviewed = 1 WHERE studyDate = :date")
    suspend fun markDateAsReviewed(date: Date)

    // 删除某天的学习记录
    @Query("DELETE FROM study_history WHERE studyDate = :date")
    suspend fun deleteStudyHistoryByDate(date: Date)

    // 清空所有学习历史记录
    @Query("DELETE FROM study_history")
    suspend fun deleteAllStudyHistory()

    // 重置所有卡片的模糊状态
    @Query("UPDATE fangge_card SET isVague = 0")
    suspend fun resetAllVagueStatus()

    // 清空所有卡片数据
    @Query("DELETE FROM fangge_card")
    suspend fun deleteAllFanggeCards()
}
