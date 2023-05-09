package com.novandi.dicodingstory.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.novandi.dicodingstory.api.ApiService
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.api.StoryResponse
import com.novandi.dicodingstory.ui.home.HomeViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StoryPagingSource(private val apiService: ApiService, private val token: String)
    : PagingSource<Int, StoryItems>() {
    override fun getRefreshKey(state: PagingState<Int, StoryItems>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItems> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        return suspendCoroutine { continuation ->
            apiService.getStories(token, size = HomeViewModel.STORY_SIZE).enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    val responseData = response.body()?.listStory ?: emptyList()
                    continuation.resume(
                        LoadResult.Page(
                            data = responseData,
                            prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                            nextKey = if (responseData.isEmpty()) null else position + 1
                        )
                    )
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    continuation.resume(LoadResult.Error(t))
                }
            })
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}