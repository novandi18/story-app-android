package com.novandi.dicodingstory.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.novandi.dicodingstory.api.ApiService
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.ui.home.HomeViewModel

class StoryRepository(private val service: ApiService, private val token: String) {
    fun getStories(): LiveData<PagingData<StoryItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = HomeViewModel.STORY_SIZE
            ),
            pagingSourceFactory = {
                StoryPagingSource(service, token)
            }
        ).liveData
    }
}