package com.novandi.dicodingstory.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.novandi.dicodingstory.api.StoryItems
import com.novandi.dicodingstory.data.StoryRepository
import com.novandi.dicodingstory.utils.DataDummy
import com.novandi.dicodingstory.utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val data: PagingData<StoryItems> = StoryPagingSource.snapshot(dummyStory)
        val expectedData = MutableLiveData<PagingData<StoryItems>>()
        expectedData.value = data
        `when`(storyRepository.getStories()).thenReturn(expectedData)

        val homeViewModel = HomeViewModel(storyRepository)
        val actualStory: PagingData<StoryItems> = homeViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryItems> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<StoryItems>>()
        expectedStory.value = data
        `when`(storyRepository.getStories()).thenReturn(expectedStory)

        val homeViewModel = HomeViewModel(storyRepository)
        val actualStory: PagingData<StoryItems> = homeViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryItems>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryItems>>>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryItems>>> =
        LoadResult.Page(emptyList(), 0, 1)

    companion object {
        fun snapshot(items: List<StoryItems>): PagingData<StoryItems> = PagingData.from(items)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}