package com.novandi.dicodingstory.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.novandi.dicodingstory.api.ApiService
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.database.StoryDatabase

class StoryRepository(private val service: ApiService, private val token: String, private val storyDatabase: StoryDatabase) {
    fun getStories(): LiveData<PagingData<StoryItems>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 2,
                enablePlaceholders = false,

            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, service, token),
            pagingSourceFactory = {
                StoryPagingSource(service, token)
            }
        ).liveData
    }
}