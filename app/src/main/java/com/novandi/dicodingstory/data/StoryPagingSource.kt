package com.novandi.dicodingstory.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.novandi.dicodingstory.api.ApiService
import com.novandi.dicodingstory.api.StoryItems
import retrofit2.awaitResponse

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
        return try {
            val responseData = apiService.getStories(token, size = INITIAL_PAGE_SIZE, page = position).awaitResponse()
            if (responseData.isSuccessful) {
                val data = responseData.body()
                return if (data != null) {
                    LoadResult.Page(
                        data = data.listStory,
                        prevKey = null,
                        nextKey = if (data.listStory.isEmpty()) null else position + 1
                    )
                } else {
                    LoadResult.Error(Exception())
                }
            } else {
                LoadResult.Error(Exception())
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val INITIAL_PAGE_SIZE = 5
    }
}