package com.novandi.dicodingstory.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.novandi.dicodingstory.api.StoryItems

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryItems>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoryItems>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}