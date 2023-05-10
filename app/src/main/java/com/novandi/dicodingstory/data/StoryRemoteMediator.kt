package com.novandi.dicodingstory.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.novandi.dicodingstory.api.ApiService
import com.novandi.dicodingstory.database.StoryDatabase
import retrofit2.awaitResponse
import androidx.room.withTransaction
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.database.RemoteKeys

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, StoryItems>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItems>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getStories(header = token, size = INITIAL_PAGE_SIZE, page = page).awaitResponse()
            if (responseData.isSuccessful) {
                val data = responseData.body()
                return if (data != null) {
                    val endOfPaginationReached = data.listStory.isEmpty()

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            database.remoteKeysDao().deleteRemoteKeys()
                            database.storyDao().deleteAll()
                        }

                        val prevKey = if (page == 1) null else page -1
                        val nextKey = if (endOfPaginationReached) null else page + 1
                        val keys = data.listStory.map {
                            RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                        }
                        database.remoteKeysDao().insertAll(keys)
                        database.storyDao().insertStory(data.listStory)
                    }

                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                } else {
                    MediatorResult.Error(Exception())
                }
            } else {
                return MediatorResult.Error(Exception())
            }
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryItems>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryItems>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryItems>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val INITIAL_PAGE_SIZE = 5
    }
}